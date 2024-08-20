package com.processout.sdk.ui.checkout

import android.app.Application
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.ImageResult
import com.processout.sdk.R
import com.processout.sdk.api.dispatcher.card.tokenization.PODefaultCardTokenizationEventDispatcher
import com.processout.sdk.api.dispatcher.checkout.PODefaultDynamicCheckoutEventDispatcher
import com.processout.sdk.api.dispatcher.napm.PODefaultNativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.model.request.PODynamicCheckoutInvoiceInvalidationReason
import com.processout.sdk.api.model.request.PODynamicCheckoutInvoiceRequest
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.api.model.response.POBillingAddressCollectionMode
import com.processout.sdk.api.model.response.POBillingAddressCollectionMode.*
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.CardConfiguration
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.Display
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.Flow.express
import com.processout.sdk.api.model.response.POInvoice
import com.processout.sdk.api.model.response.POTransaction.Status.*
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.core.POFailure.Code.Cancelled
import com.processout.sdk.core.POFailure.Code.Generic
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import com.processout.sdk.ui.base.BaseInteractor
import com.processout.sdk.ui.card.tokenization.CardTokenizationCompletion
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModel
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration.BillingAddressConfiguration.CollectionMode
import com.processout.sdk.ui.checkout.DynamicCheckoutCompletion.*
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent.*
import com.processout.sdk.ui.checkout.DynamicCheckoutInteractorState.ActionId
import com.processout.sdk.ui.checkout.DynamicCheckoutInteractorState.PaymentMethod
import com.processout.sdk.ui.checkout.DynamicCheckoutInteractorState.PaymentMethod.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentCompletion
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModel
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.*
import com.processout.sdk.ui.shared.extension.orElse
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

internal class DynamicCheckoutInteractor(
    private val app: Application,
    private var invoiceRequest: POInvoiceRequest,
    private val invoicesService: POInvoicesService,
    private val cardTokenization: CardTokenizationViewModel,
    private val cardTokenizationEventDispatcher: PODefaultCardTokenizationEventDispatcher,
    private val nativeAlternativePayment: NativeAlternativePaymentViewModel,
    private val nativeAlternativePaymentEventDispatcher: PODefaultNativeAlternativePaymentMethodEventDispatcher,
    private val eventDispatcher: PODefaultDynamicCheckoutEventDispatcher
) : BaseInteractor() {

    private val _completion = MutableStateFlow<DynamicCheckoutCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    val cardTokenizationState = cardTokenization.state
    val nativeAlternativePaymentState = nativeAlternativePayment.state

    private val _paymentEvents = Channel<DynamicCheckoutPaymentEvent>()
    val paymentEvents = _paymentEvents.receiveAsFlow()

    private var latestInvoiceRequest: PODynamicCheckoutInvoiceRequest? = null

    init {
        start()
    }

    private fun start() {
        dispatchEvents()
        handleCompletions()
        collectTokenizedCard()
        collectAuthorizeInvoiceResult()
        collectInvoice()
        fetchConfiguration()
    }

    private fun restart(reason: PODynamicCheckoutInvoiceInvalidationReason) {
        val selectedPaymentMethodId = when (reason) {
            is PODynamicCheckoutInvoiceInvalidationReason.Failure -> null
            else -> _state.value.selectedPaymentMethodId
        }
        val errorMessage = when (reason) {
            is PODynamicCheckoutInvoiceInvalidationReason.Failure ->
                if (reason.failure.code == Cancelled) null
                else app.getString(R.string.po_dynamic_checkout_error_generic)
            else -> null
        }
        reset(
            state = _state.value.copy(
                invoice = POInvoice(id = invoiceRequest.invoiceId),
                selectedPaymentMethodId = selectedPaymentMethodId,
                errorMessage = errorMessage
            )
        )
        start()
    }

    private fun reset(state: DynamicCheckoutInteractorState) {
        interactorScope.coroutineContext.cancelChildren()
        latestInvoiceRequest = null
        _completion.update { Awaiting }
        _state.update { state }
    }

    private fun initState() = DynamicCheckoutInteractorState(
        loading = true,
        invoice = POInvoice(id = invoiceRequest.invoiceId),
        isInvoiceValid = false,
        paymentMethods = emptyList(),
        selectedPaymentMethodId = null,
        submitActionId = ActionId.SUBMIT,
        cancelActionId = ActionId.CANCEL
    )

    private fun fetchConfiguration() {
        interactorScope.launch {
            invoicesService.invoice(invoiceRequest)
                .onSuccess { invoice ->
                    when (invoice.transaction?.status()) {
                        WAITING -> setStartedState(invoice)
                        AUTHORIZED, COMPLETED -> _completion.update { Success }
                        else -> _completion.update {
                            Failure(
                                ProcessOutResult.Failure(
                                    code = Generic(),
                                    message = "Unsupported invoice state. Please create new invoice and restart dynamic checkout."
                                )
                            )
                        }
                    }
                }.onFailure { failure ->
                    _completion.update { Failure(failure) }
                }
        }
    }

    private fun setStartedState(invoice: POInvoice) {
        interactorScope.launch {
            val paymentMethods = invoice.paymentMethods
            if (paymentMethods.isNullOrEmpty()) {
                _completion.update {
                    Failure(
                        ProcessOutResult.Failure(
                            code = Generic(),
                            message = "Missing payment methods configuration."
                        )
                    )
                }
                return@launch
            }
            val mappedPaymentMethods = paymentMethods.map()
            preloadAllImages(
                paymentMethods = mappedPaymentMethods,
                coroutineScope = this@launch
            )
            _state.update {
                it.copy(
                    loading = false,
                    invoice = invoice,
                    isInvoiceValid = true,
                    paymentMethods = mappedPaymentMethods
                )
            }
            _state.value.selectedPaymentMethodId?.let { id ->
                paymentMethod(id)?.let { start(it) }
                    .orElse {
                        _state.update {
                            it.copy(
                                selectedPaymentMethodId = null,
                                errorMessage = app.getString(R.string.po_dynamic_checkout_error_method_unavailable)
                            )
                        }
                    }
            }
        }
    }

    private fun List<PODynamicCheckoutPaymentMethod>.map(): List<PaymentMethod> =
        mapIndexedNotNull { index, paymentMethod ->
            when (paymentMethod) {
                is PODynamicCheckoutPaymentMethod.Card -> Card(
                    id = index.toString(),
                    configuration = paymentMethod.configuration,
                    display = paymentMethod.display
                )
                is PODynamicCheckoutPaymentMethod.GooglePay -> GooglePay(
                    id = index.toString(),
                    configuration = paymentMethod.configuration
                )
                is PODynamicCheckoutPaymentMethod.AlternativePayment -> {
                    val redirectUrl = paymentMethod.configuration.redirectUrl
                    if (redirectUrl != null) {
                        AlternativePayment(
                            id = index.toString(),
                            redirectUrl = redirectUrl,
                            display = paymentMethod.display,
                            isExpress = paymentMethod.flow == express
                        )
                    } else {
                        NativeAlternativePayment(
                            id = index.toString(),
                            gatewayConfigurationId = paymentMethod.configuration.gatewayConfigurationId,
                            display = paymentMethod.display
                        )
                    }
                }
                else -> null
            }
        }

    //region Images

    private suspend fun preloadAllImages(
        paymentMethods: List<PaymentMethod>,
        coroutineScope: CoroutineScope
    ) {
        val logoUrls = mutableListOf<String>()
        paymentMethods.forEach {
            when (it) {
                is Card -> logoUrls.addAll(it.display.logoUrls())
                is AlternativePayment -> logoUrls.addAll(it.display.logoUrls())
                is NativeAlternativePayment -> logoUrls.addAll(it.display.logoUrls())
                else -> {}
            }
        }
        val deferredResults = logoUrls.map { url ->
            coroutineScope.async { preloadImage(url) }
        }
        deferredResults.awaitAll()
    }

    private fun Display.logoUrls(): List<String> {
        val urls = mutableListOf(logo.lightUrl.raster)
        logo.darkUrl?.raster?.let { urls.add(it) }
        return urls
    }

    private suspend fun preloadImage(url: String): ImageResult {
        val request = ImageRequest.Builder(app)
            .data(url)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build()
        return app.imageLoader.execute(request)
    }

    //endregion

    private fun paymentMethod(id: String): PaymentMethod? =
        _state.value.paymentMethods.find { it.id == id }

    private fun selectedPaymentMethod(): PaymentMethod? =
        _state.value.selectedPaymentMethodId?.let {
            paymentMethod(it)
        }

    fun onEvent(event: DynamicCheckoutEvent) {
        when (event) {
            is PaymentMethodSelected -> onPaymentMethodSelected(event)
            is FieldValueChanged -> onFieldValueChanged(event)
            is FieldFocusChanged -> onFieldFocusChanged(event)
            is Action -> onAction(event)
            is ActionConfirmationRequested -> {
                // TODO
            }
            is Dismiss -> {
                POLogger.warn("Dismissed: %s", event.failure)
                _completion.update { Failure(event.failure) }
            }
        }
    }

    private fun onPaymentMethodSelected(event: PaymentMethodSelected) {
        if (event.id != state.value.selectedPaymentMethodId) {
            paymentMethod(event.id)?.let { paymentMethod ->
                if (shouldInvalidateInvoice()) {
                    val failure = ProcessOutResult.Failure(
                        code = Generic(),
                        message = "Payment method has been changed by the user during processing. " +
                                "Invoice invalidated and the new one has not been provided."
                    )
                    invalidateInvoice(
                        failure = failure,
                        reason = PODynamicCheckoutInvoiceInvalidationReason.PaymentMethodChanged
                    )
                }
                cardTokenization.reset()
                nativeAlternativePayment.reset()
                if (state.value.isInvoiceValid) {
                    start(paymentMethod)
                }
                _state.update {
                    it.copy(
                        selectedPaymentMethodId = event.id,
                        errorMessage = null
                    )
                }
            }
        }
    }

    private fun shouldInvalidateInvoice(): Boolean {
        selectedPaymentMethod()?.let { selectedPaymentMethod ->
            return when (selectedPaymentMethod) {
                is NativeAlternativePayment -> nativeAlternativePayment.state.value.submittedAtLeastOnce()
                else -> false
            }
        }
        return false
    }

    private fun NativeAlternativePaymentViewModelState.submittedAtLeastOnce() =
        when (this) {
            is Loading -> false
            is UserInput -> submittedAtLeastOnce
            is Capture -> true
        }

    private fun start(paymentMethod: PaymentMethod) {
        when (paymentMethod) {
            is Card -> cardTokenization.start(
                configuration = cardTokenization.configuration
                    .apply(paymentMethod.configuration)
            )
            is NativeAlternativePayment -> nativeAlternativePayment.start(
                invoiceId = invoiceRequest.invoiceId,
                gatewayConfigurationId = paymentMethod.gatewayConfigurationId
            )
            else -> {}
        }
    }

    private fun POCardTokenizationConfiguration.apply(
        configuration: CardConfiguration
    ) = copy(
        isCardholderNameFieldVisible = configuration.cardholderNameRequired,
        billingAddress = billingAddress.copy(
            mode = configuration.billingAddress.collectionMode.map(),
            countryCodes = configuration.billingAddress.restrictToCountryCodes
        )
    )

    private fun POBillingAddressCollectionMode.map() = when (this) {
        full -> CollectionMode.Full
        automatic -> CollectionMode.Automatic
        never -> CollectionMode.Never
    }

    private fun onFieldValueChanged(event: FieldValueChanged) {
        val paymentMethod = paymentMethod(event.paymentMethodId)
        when (paymentMethod) {
            is Card -> cardTokenization.onEvent(
                CardTokenizationEvent.FieldValueChanged(event.fieldId, event.value)
            )
            is NativeAlternativePayment -> nativeAlternativePayment.onEvent(
                NativeAlternativePaymentEvent.FieldValueChanged(event.fieldId, event.value)
            )
            else -> {}
        }
    }

    private fun onFieldFocusChanged(event: FieldFocusChanged) {
        val paymentMethod = paymentMethod(event.paymentMethodId)
        when (paymentMethod) {
            is Card -> cardTokenization.onEvent(
                CardTokenizationEvent.FieldFocusChanged(event.fieldId, event.isFocused)
            )
            is NativeAlternativePayment -> nativeAlternativePayment.onEvent(
                NativeAlternativePaymentEvent.FieldFocusChanged(event.fieldId, event.isFocused)
            )
            else -> {}
        }
    }

    private fun onAction(event: Action) {
        when (event.actionId) {
            ActionId.CANCEL -> cancel()
            else -> event.paymentMethodId?.let {
                val paymentMethod = paymentMethod(it)
                when (paymentMethod) {
                    is Card -> cardTokenization.onEvent(
                        CardTokenizationEvent.Action(event.actionId)
                    )
                    is NativeAlternativePayment -> nativeAlternativePayment.onEvent(
                        NativeAlternativePaymentEvent.Action(event.actionId)
                    )
                    else -> {}
                }
                if (event.actionId == ActionId.SUBMIT) {
                    when (paymentMethod) {
                        is GooglePay -> {
                            interactorScope.launch {
                                _paymentEvents.send(
                                    DynamicCheckoutPaymentEvent.GooglePay(
                                        configuration = paymentMethod.configuration
                                    )
                                )
                            }
                        }
                        is AlternativePayment -> {
                            interactorScope.launch {
                                _paymentEvents.send(
                                    DynamicCheckoutPaymentEvent.AlternativePayment(
                                        redirectUrl = paymentMethod.redirectUrl,
                                        returnUrl = _state.value.invoice.returnUrl
                                            ?: String() // TODO: handle missing 'returnUrl'
                                    )
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun cancel() {
        _completion.update {
            Failure(
                ProcessOutResult.Failure(
                    code = Cancelled,
                    message = "Cancelled by the user with cancel action."
                ).also { POLogger.info("Cancelled: %s", it) }
            )
        }
    }

    fun handle(result: ProcessOutResult<POAlternativePaymentMethodResponse>) {
        result.onSuccess {
            _completion.update { Success }
        }.onFailure {
            invalidateInvoice(
                failure = it,
                reason = PODynamicCheckoutInvoiceInvalidationReason.Failure(it)
            )
        }
    }

    private fun invalidateInvoice(
        failure: ProcessOutResult.Failure,
        reason: PODynamicCheckoutInvoiceInvalidationReason
    ) {
        if (eventDispatcher.subscribedForInvoiceRequest()) {
            interactorScope.launch {
                _state.update { it.copy(isInvoiceValid = false) }
                if (latestInvoiceRequest == null) {
                    val request = PODynamicCheckoutInvoiceRequest(
                        invoice = _state.value.invoice,
                        invalidationReason = reason
                    )
                    latestInvoiceRequest = request
                    eventDispatcher.send(request)
                }
            }
        } else {
            _completion.update { Failure(failure) }
        }
    }

    private fun collectInvoice() {
        interactorScope.launch {
            eventDispatcher.invoiceResponse.collect { response ->
                if (response.uuid == latestInvoiceRequest?.uuid) {
                    latestInvoiceRequest = null
                    val invoiceRequest = response.invoiceRequest
                    val reason = response.invalidationReason
                    if (invoiceRequest == null) {
                        val failure = when (reason) {
                            PODynamicCheckoutInvoiceInvalidationReason.PaymentMethodChanged ->
                                ProcessOutResult.Failure(
                                    code = Generic(),
                                    message = "Payment method has been changed by the user during processing. " +
                                            "Invoice invalidated and the new one has not been provided."
                                )
                            is PODynamicCheckoutInvoiceInvalidationReason.Failure -> reason.failure
                        }
                        _completion.update { Failure(failure) }
                    } else {
                        onInvoiceChanged(invoiceRequest, reason)
                    }
                }
            }
        }
    }

    private fun onInvoiceChanged(
        invoiceRequest: POInvoiceRequest,
        reason: PODynamicCheckoutInvoiceInvalidationReason
    ) {
        this.invoiceRequest = invoiceRequest
        cardTokenization.reset()
        nativeAlternativePayment.reset()
        restart(reason)
    }

    private fun dispatchEvents() {
        interactorScope.launch {
            cardTokenizationEventDispatcher.events.collect { eventDispatcher.send(it) }
        }
        interactorScope.launch {
            nativeAlternativePaymentEventDispatcher.events.collect { eventDispatcher.send(it) }
        }
    }

    private fun collectTokenizedCard() {
        interactorScope.launch {
            cardTokenizationEventDispatcher.processTokenizedCard.collect { card ->
                // authorize invoice
            }
        }
    }

    private fun collectAuthorizeInvoiceResult() {
        interactorScope.launch {
            invoicesService.authorizeInvoiceResult.collect { result ->
                when (selectedPaymentMethod()) {
                    is Card -> cardTokenizationEventDispatcher.complete(result)
                    is GooglePay -> {
                        // TODO
                    }
                    else -> {}
                }
            }
        }
    }

    private fun handleCompletions() {
        interactorScope.launch {
            cardTokenization.completion.collect { completion ->
                when (completion) {
                    is CardTokenizationCompletion.Success -> _completion.update { Success }
                    is CardTokenizationCompletion.Failure -> invalidateInvoice(
                        failure = completion.failure,
                        reason = PODynamicCheckoutInvoiceInvalidationReason.Failure(completion.failure)
                    )
                    else -> {}
                }
            }
        }
        interactorScope.launch {
            nativeAlternativePayment.completion.collect { completion ->
                when (completion) {
                    NativeAlternativePaymentCompletion.Success -> _completion.update { Success }
                    is NativeAlternativePaymentCompletion.Failure -> invalidateInvoice(
                        failure = completion.failure,
                        reason = PODynamicCheckoutInvoiceInvalidationReason.Failure(completion.failure)
                    )
                    else -> {}
                }
            }
        }
    }
}
