package com.processout.sdk.ui.checkout

import android.app.Application
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.ImageResult
import com.processout.sdk.R
import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.dispatcher.card.tokenization.PODefaultCardTokenizationEventDispatcher
import com.processout.sdk.api.dispatcher.napm.PODefaultNativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent.WillSubmitParameters
import com.processout.sdk.api.model.request.PODynamicCheckoutInvoiceInvalidationReason
import com.processout.sdk.api.model.request.PODynamicCheckoutInvoiceRequest
import com.processout.sdk.api.model.request.POInvoiceAuthorizationRequest
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.api.model.response.*
import com.processout.sdk.api.model.response.POBillingAddressCollectionMode.*
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.CardConfiguration
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.Display
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.Flow.express
import com.processout.sdk.api.model.response.POTransaction.Status.*
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.api.service.proxy3ds.POProxy3DSService
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
import com.processout.sdk.ui.shared.extension.orElse
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

internal class DynamicCheckoutInteractor(
    private val app: Application,
    private var configuration: PODynamicCheckoutConfiguration,
    private val invoicesService: POInvoicesService,
    private val threeDSService: POProxy3DSService,
    private val cardTokenization: CardTokenizationViewModel,
    private val cardTokenizationEventDispatcher: PODefaultCardTokenizationEventDispatcher,
    private val nativeAlternativePayment: NativeAlternativePaymentViewModel,
    private val nativeAlternativePaymentEventDispatcher: PODefaultNativeAlternativePaymentMethodEventDispatcher,
    private val eventDispatcher: POEventDispatcher = POEventDispatcher
) : BaseInteractor() {

    private val _completion = MutableStateFlow<DynamicCheckoutCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    val cardTokenizationState = cardTokenization.state
    val nativeAlternativePaymentState = nativeAlternativePayment.state

    private val _submitEvents = Channel<DynamicCheckoutSubmitEvent>()
    val submitEvents = _submitEvents.receiveAsFlow()

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

    private fun restart(
        invoiceRequest: POInvoiceRequest,
        reason: PODynamicCheckoutInvoiceInvalidationReason
    ) {
        configuration = configuration.copy(invoiceRequest = invoiceRequest)
        val selectedPaymentMethodId = when (reason) {
            is PODynamicCheckoutInvoiceInvalidationReason.Failure ->
                if (reason.failure.code == Cancelled) _state.value.selectedPaymentMethodId else null
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
                invoice = POInvoice(id = configuration.invoiceRequest.invoiceId),
                selectedPaymentMethodId = selectedPaymentMethodId,
                processingPaymentMethodId = null,
                errorMessage = errorMessage
            )
        )
        start()
    }

    private fun reset(state: DynamicCheckoutInteractorState) {
        interactorScope.coroutineContext.cancelChildren()
        latestInvoiceRequest = null
        cardTokenization.reset()
        nativeAlternativePayment.reset()
        _completion.update { Awaiting }
        _state.update { state }
    }

    private fun initState() = DynamicCheckoutInteractorState(
        loading = true,
        invoice = POInvoice(id = configuration.invoiceRequest.invoiceId),
        isInvoiceValid = false,
        paymentMethods = emptyList(),
        submitActionId = ActionId.SUBMIT,
        cancelActionId = ActionId.CANCEL
    )

    private fun fetchConfiguration() {
        interactorScope.launch {
            invoicesService.invoice(configuration.invoiceRequest)
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
            _state.value.pendingSubmitPaymentMethodId?.let { id ->
                _state.update { it.copy(pendingSubmitPaymentMethodId = null) }
                paymentMethod(id)?.let { submit(it) }
                    .orElse {
                        _state.update {
                            it.copy(errorMessage = app.getString(R.string.po_dynamic_checkout_error_method_unavailable))
                        }
                    }
            }
        }
    }

    private fun List<PODynamicCheckoutPaymentMethod>.map(): List<PaymentMethod> =
        mapNotNull { paymentMethod ->
            when (paymentMethod) {
                is PODynamicCheckoutPaymentMethod.Card -> Card(
                    id = "card",
                    configuration = paymentMethod.configuration,
                    display = paymentMethod.display
                )
                is PODynamicCheckoutPaymentMethod.GooglePay -> GooglePay(
                    id = paymentMethod.configuration.gatewayMerchantId,
                    configuration = paymentMethod.configuration
                )
                is PODynamicCheckoutPaymentMethod.AlternativePayment -> {
                    val redirectUrl = paymentMethod.configuration.redirectUrl
                    if (redirectUrl != null) {
                        AlternativePayment(
                            id = paymentMethod.configuration.gatewayConfigurationId,
                            redirectUrl = redirectUrl,
                            display = paymentMethod.display,
                            isExpress = paymentMethod.flow == express
                        )
                    } else {
                        NativeAlternativePayment(
                            id = paymentMethod.configuration.gatewayConfigurationId,
                            gatewayConfigurationId = paymentMethod.configuration.gatewayConfigurationId,
                            display = paymentMethod.display
                        )
                    }
                }
                is PODynamicCheckoutPaymentMethod.CardCustomerToken -> CustomerToken(
                    id = paymentMethod.configuration.customerTokenId,
                    configuration = paymentMethod.configuration,
                    display = paymentMethod.display,
                    isExpress = paymentMethod.flow == express
                )
                is PODynamicCheckoutPaymentMethod.AlternativePaymentCustomerToken -> CustomerToken(
                    id = paymentMethod.configuration.customerTokenId,
                    configuration = paymentMethod.configuration,
                    display = paymentMethod.display,
                    isExpress = paymentMethod.flow == express
                )
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
                is CustomerToken -> logoUrls.addAll(it.display.logoUrls())
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
        if (event.id == _state.value.selectedPaymentMethodId) {
            return
        }
        paymentMethod(event.id)?.let { paymentMethod ->
            if (_state.value.processingPaymentMethodId != null) {
                invalidateInvoice(
                    reason = PODynamicCheckoutInvoiceInvalidationReason.PaymentMethodChanged
                )
            }
            cardTokenization.reset()
            nativeAlternativePayment.reset()
            if (_state.value.isInvoiceValid) {
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

    private fun start(paymentMethod: PaymentMethod) {
        when (paymentMethod) {
            is Card -> cardTokenization.start(
                configuration = cardTokenization.configuration
                    .apply(paymentMethod.configuration)
            )
            is NativeAlternativePayment -> nativeAlternativePayment.start(
                invoiceId = configuration.invoiceRequest.invoiceId,
                gatewayConfigurationId = paymentMethod.gatewayConfigurationId
            )
            else -> {}
        }
    }

    private fun POCardTokenizationConfiguration.apply(
        configuration: CardConfiguration
    ) = copy(
        cvcRequired = configuration.cvcRequired,
        isCardholderNameFieldVisible = configuration.cardholderNameRequired,
        billingAddress = billingAddress.copy(
            mode = configuration.billingAddress.collectionMode.map(),
            countryCodes = configuration.billingAddress.restrictToCountryCodes
        ),
        savingAllowed = configuration.savingAllowed
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
        val paymentMethod = event.paymentMethodId?.let { paymentMethod(it) }
        when (event.actionId) {
            ActionId.SUBMIT -> paymentMethod?.let { submit(it) }
            ActionId.CANCEL -> cancel()
            else -> when (paymentMethod) {
                is Card -> cardTokenization.onEvent(
                    CardTokenizationEvent.Action(event.actionId)
                )
                is NativeAlternativePayment -> nativeAlternativePayment.onEvent(
                    NativeAlternativePaymentEvent.Action(event.actionId)
                )
                else -> {}
            }
        }
    }

    private fun PaymentMethod.isExpress(): Boolean =
        when (this) {
            is Card, is NativeAlternativePayment -> false
            is GooglePay -> true
            is AlternativePayment -> isExpress
            is CustomerToken -> isExpress
        }

    private fun submit(paymentMethod: PaymentMethod) {
        if (paymentMethod.id == _state.value.processingPaymentMethodId) {
            return
        }
        if (paymentMethod.isExpress()) {
            cardTokenization.reset()
            nativeAlternativePayment.reset()
            _state.update {
                it.copy(
                    selectedPaymentMethodId = null,
                    errorMessage = null
                )
            }
        }
        if (_state.value.processingPaymentMethodId != null) {
            _state.update { it.copy(pendingSubmitPaymentMethodId = paymentMethod.id) }
            invalidateInvoice(
                reason = PODynamicCheckoutInvoiceInvalidationReason.PaymentMethodChanged
            )
            return
        }
        when (paymentMethod) {
            is GooglePay -> {
                interactorScope.launch {
                    _state.update { it.copy(processingPaymentMethodId = paymentMethod.id) }
                    _submitEvents.send(
                        DynamicCheckoutSubmitEvent.GooglePay(
                            configuration = paymentMethod.configuration
                        )
                    )
                }
            }
            is AlternativePayment -> submitAlternativePayment(
                id = paymentMethod.id,
                redirectUrl = paymentMethod.redirectUrl
            )
            is CustomerToken -> {
                val redirectUrl = paymentMethod.configuration.redirectUrl
                if (redirectUrl != null) {
                    submitAlternativePayment(
                        id = paymentMethod.id,
                        redirectUrl = redirectUrl
                    )
                } else {
                    _state.update { it.copy(processingPaymentMethodId = paymentMethod.id) }
                    invoicesService.authorizeInvoice(
                        request = POInvoiceAuthorizationRequest(
                            invoiceId = _state.value.invoice.id,
                            source = paymentMethod.configuration.customerTokenId
                        ),
                        threeDSService = threeDSService
                    )
                }
            }
            else -> {}
        }
    }

    private fun submitAlternativePayment(
        id: String,
        redirectUrl: String
    ) {
        val returnUrl = configuration.alternativePayment.returnUrl
        if (returnUrl.isNullOrBlank()) {
            handleAlternativePayment(
                ProcessOutResult.Failure(
                    code = Generic(),
                    message = "Missing return URL in alternative payment configuration."
                )
            )
            return
        }
        interactorScope.launch {
            _state.update { it.copy(processingPaymentMethodId = id) }
            _submitEvents.send(
                DynamicCheckoutSubmitEvent.AlternativePayment(
                    redirectUrl = redirectUrl,
                    returnUrl = returnUrl
                )
            )
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
                    currentInvoice = _state.value.invoice,
                    invalidationReason = reason
                )
                latestInvoiceRequest = request
                eventDispatcher.send(request)
            }
        }
    }

    private fun collectInvoice() {
        eventDispatcher.subscribeForResponse<PODynamicCheckoutInvoiceResponse>(
            coroutineScope = interactorScope
        ) { response ->
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
                    restart(invoiceRequest, reason)
                }
            }
        }
    }

    private fun dispatchEvents() {
        interactorScope.launch {
            cardTokenizationEventDispatcher.events.collect { eventDispatcher.send(it) }
        }
        interactorScope.launch {
            nativeAlternativePaymentEventDispatcher.events.collect { event ->
                if (event is WillSubmitParameters) {
                    _state.update { it.copy(processingPaymentMethodId = selectedPaymentMethod()?.id) }
                }
                eventDispatcher.send(event)
            }
        }
    }

    private fun collectTokenizedCard() {
        interactorScope.launch {
            cardTokenizationEventDispatcher.processTokenizedCardRequest.collect { request ->
                _state.update { it.copy(processingPaymentMethodId = selectedPaymentMethod()?.id) }
                invoicesService.authorizeInvoice(
                    request = POInvoiceAuthorizationRequest(
                        invoiceId = _state.value.invoice.id,
                        source = request.card.id,
                        saveSource = request.saveCard,
                        clientSecret = configuration.invoiceRequest.clientSecret
                    ),
                    threeDSService = threeDSService
                )
            }
        }
    }

    private fun collectAuthorizeInvoiceResult() {
        interactorScope.launch {
            invoicesService.authorizeInvoiceResult.collect { result ->
                if (selectedPaymentMethod() is Card) {
                    cardTokenizationEventDispatcher.complete(result)
                } else {
                    result.onSuccess {
                        _completion.update { Success }
                    }.onFailure { failure ->
                        invalidateInvoice(
                            reason = PODynamicCheckoutInvoiceInvalidationReason.Failure(failure)
                        )
                    }
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
                        reason = PODynamicCheckoutInvoiceInvalidationReason.Failure(completion.failure)
                    )
                    else -> {}
                }
            }
        }
    }

    fun handleAlternativePayment(result: ProcessOutResult<POAlternativePaymentMethodResponse>) {
        result.onSuccess { response ->
            invoicesService.authorizeInvoice(
                request = POInvoiceAuthorizationRequest(
                    invoiceId = _state.value.invoice.id,
                    source = response.gatewayToken,
                    authorizeOnly = true,
                    allowFallbackToSale = true
                ),
                threeDSService = threeDSService
            )
        }.onFailure { failure ->
            invalidateInvoice(
                reason = PODynamicCheckoutInvoiceInvalidationReason.Failure(failure)
            )
        }
    }

    fun onCleared() {
        threeDSService.close()
    }
}
