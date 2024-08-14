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
import com.processout.sdk.api.model.response.POBillingAddressCollectionMode
import com.processout.sdk.api.model.response.POBillingAddressCollectionMode.*
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.CardConfiguration
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.Display
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.Flow.express
import com.processout.sdk.api.model.response.POInvoice
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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private var latestInvoiceRequest: PODynamicCheckoutInvoiceRequest? = null

    init {
        start()
    }

    private fun start() {
        handleCompletions()
        collectInvoice()
        dispatchEvents()
        fetchConfiguration()
    }

    private fun restart(reason: PODynamicCheckoutInvoiceInvalidationReason) {
        val selectedPaymentMethodId = when (reason) {
            is PODynamicCheckoutInvoiceInvalidationReason.Failure -> null
            else -> _state.value.selectedPaymentMethodId
        }
        val errorMessage = when (reason) {
            is PODynamicCheckoutInvoiceInvalidationReason.Failure ->
                app.getString(R.string.po_dynamic_checkout_error_generic)
            else -> null
        }
        reset(
            state = _state.value.copy(
                invoice = POInvoice(
                    id = invoiceRequest.invoiceId,
                    amount = String(),
                    currency = String()
                ),
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
        invoice = POInvoice(
            id = invoiceRequest.invoiceId,
            amount = String(),
            currency = String()
        ),
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
                    val paymentMethods = invoice.paymentMethods
                    if (paymentMethods.isNullOrEmpty()) {
                        _completion.update {
                            Failure(
                                ProcessOutResult.Failure(
                                    code = Generic(),
                                    message = "Missing remote configuration."
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
                            isInvoiceValid = true, // TODO: validate invoice transaction state
                            paymentMethods = mappedPaymentMethods
                        )
                    }
                    _state.value.selectedPaymentMethodId?.let { id ->
                        paymentMethod(id)?.let { start(it) }
                    }
                }.onFailure { failure ->
                    _completion.update { Failure(failure) }
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
                if (nativeAlternativePayment.state.value.submittedAtLeastOnce()) {
                    invalidateInvoice(reason = PODynamicCheckoutInvoiceInvalidationReason.PaymentMethodChanged)
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
                            // TODO
                        }
                        is AlternativePayment -> {
                            // TODO
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

    private fun invalidateInvoice(reason: PODynamicCheckoutInvoiceInvalidationReason) {
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

    private fun handleCompletions() {
        interactorScope.launch {
            cardTokenization.completion.collect {
                onCardTokenization(it)
            }
        }
        interactorScope.launch {
            nativeAlternativePayment.completion.collect {
                onNativeAlternativePayment(it)
            }
        }
    }

    private fun onCardTokenization(completion: CardTokenizationCompletion) {
        when (completion) {
            is CardTokenizationCompletion.Success -> _completion.update { Success }
            is CardTokenizationCompletion.Failure -> {
                // TODO
            }
            else -> {}
        }
    }

    private fun onNativeAlternativePayment(completion: NativeAlternativePaymentCompletion) {
        when (completion) {
            NativeAlternativePaymentCompletion.Success -> _completion.update { Success }
            is NativeAlternativePaymentCompletion.Failure ->
                if (eventDispatcher.subscribedForInvoiceRequest()) {
                    invalidateInvoice(reason = PODynamicCheckoutInvoiceInvalidationReason.Failure(completion.failure))
                } else {
                    _completion.update { Failure(completion.failure) }
                }
            else -> {}
        }
    }
}
