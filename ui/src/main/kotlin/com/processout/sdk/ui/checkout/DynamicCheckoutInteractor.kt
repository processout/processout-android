package com.processout.sdk.ui.checkout

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.core.os.postDelayed
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.ImageResult
import com.processout.sdk.R
import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.dispatcher.card.tokenization.PODefaultCardTokenizationEventDispatcher
import com.processout.sdk.api.dispatcher.napm.PODefaultNativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.model.event.PODynamicCheckoutEvent
import com.processout.sdk.api.model.event.PODynamicCheckoutEvent.DidFail
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent.WillSubmitParameters
import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.response.*
import com.processout.sdk.api.model.response.POBillingAddressCollectionMode.*
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.*
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.Flow.express
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.Unknown
import com.processout.sdk.api.model.response.POTransaction.Status.*
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.api.service.googlepay.POGooglePayConfiguration
import com.processout.sdk.api.service.googlepay.POGooglePayConfiguration.CardConfiguration.BillingAddressParameters
import com.processout.sdk.api.service.googlepay.POGooglePayConfiguration.PaymentDataConfiguration.ShippingAddressParameters
import com.processout.sdk.api.service.googlepay.POGooglePayConfiguration.PaymentDataConfiguration.TransactionInfo
import com.processout.sdk.api.service.googlepay.POGooglePayRequestBuilder
import com.processout.sdk.api.service.googlepay.POGooglePayService
import com.processout.sdk.api.service.proxy3ds.POProxy3DSService
import com.processout.sdk.core.POFailure.Code.*
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogAttribute
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
import com.processout.sdk.ui.checkout.DynamicCheckoutInteractorState.*
import com.processout.sdk.ui.checkout.DynamicCheckoutInteractorState.PaymentMethod.*
import com.processout.sdk.ui.checkout.DynamicCheckoutInteractorState.PaymentMethod.AlternativePayment
import com.processout.sdk.ui.checkout.DynamicCheckoutInteractorState.PaymentMethod.Card
import com.processout.sdk.ui.checkout.DynamicCheckoutInteractorState.PaymentMethod.GooglePay
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration.GooglePayConfiguration
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration.GooglePayConfiguration.BillingAddressConfiguration.Format.FULL
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration.GooglePayConfiguration.BillingAddressConfiguration.Format.MIN
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration.GooglePayConfiguration.CheckoutOption.COMPLETE_IMMEDIATE_PURCHASE
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration.GooglePayConfiguration.CheckoutOption.DEFAULT
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration.GooglePayConfiguration.TotalPriceStatus.ESTIMATED
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration.GooglePayConfiguration.TotalPriceStatus.FINAL
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
import java.util.UUID

internal class DynamicCheckoutInteractor(
    private val app: Application,
    private var configuration: PODynamicCheckoutConfiguration,
    private val invoicesService: POInvoicesService,
    private val threeDSService: POProxy3DSService,
    private val googlePayService: POGooglePayService,
    private val cardTokenization: CardTokenizationViewModel,
    private val cardTokenizationEventDispatcher: PODefaultCardTokenizationEventDispatcher,
    private val nativeAlternativePayment: NativeAlternativePaymentViewModel,
    private val nativeAlternativePaymentEventDispatcher: PODefaultNativeAlternativePaymentMethodEventDispatcher,
    private val eventDispatcher: POEventDispatcher = POEventDispatcher,
    private var logAttributes: Map<String, String> = logAttributes(
        invoiceId = configuration.invoiceRequest.invoiceId
    )
) : BaseInteractor() {

    private companion object {
        fun logAttributes(invoiceId: String): Map<String, String> =
            mapOf(POLogAttribute.INVOICE_ID to invoiceId)
    }

    private val _completion = MutableStateFlow<DynamicCheckoutCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    val cardTokenizationState = cardTokenization.state
    val nativeAlternativePaymentState = nativeAlternativePayment.state

    private val _submitEvents = Channel<DynamicCheckoutSubmitEvent>()
    val submitEvents = _submitEvents.receiveAsFlow()

    private val handler = Handler(Looper.getMainLooper())

    private var latestInvoiceRequest: PODynamicCheckoutInvoiceRequest? = null

    init {
        start()
    }

    private fun start() {
        handleCompletions()
        dispatchEvents()
        dispatchFailure()
        collectInvoice()
        collectInvoiceAuthorizationRequest()
        collectAuthorizeInvoiceResult()
        collectTokenizedCard()
        fetchConfiguration()
    }

    private fun restart(
        invoiceRequest: POInvoiceRequest,
        reason: PODynamicCheckoutInvoiceInvalidationReason
    ) {
        configuration = configuration.copy(invoiceRequest = invoiceRequest)
        logAttributes = logAttributes(invoiceId = invoiceRequest.invoiceId)
        val selectedPaymentMethodId = when (reason) {
            is PODynamicCheckoutInvoiceInvalidationReason.Failure ->
                if (reason.failure.code == Cancelled)
                    _state.value.selectedPaymentMethodId else null
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
                        AUTHORIZED, COMPLETED -> handleSuccess()
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
            val mappedPaymentMethods = paymentMethods.map(
                amount = invoice.amount,
                currency = invoice.currency
            )
            preloadAllImages(paymentMethods = mappedPaymentMethods)
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

    private suspend fun List<PODynamicCheckoutPaymentMethod>.map(
        amount: String,
        currency: String
    ): List<PaymentMethod> = mapNotNull { paymentMethod ->
        when (paymentMethod) {
            is PODynamicCheckoutPaymentMethod.Card -> Card(
                id = PaymentMethodId.CARD,
                configuration = paymentMethod.configuration,
                display = paymentMethod.display
            )
            is PODynamicCheckoutPaymentMethod.GooglePay -> {
                val configuration = configuration.googlePay.map(
                    amount = amount,
                    currency = currency,
                    configuration = paymentMethod.configuration
                )
                val isReadyToPayRequest = POGooglePayRequestBuilder.isReadyToPayRequest(configuration.card)
                if (googlePayService.isReadyToPay(isReadyToPayRequest))
                    GooglePay(
                        id = paymentMethod.configuration.gatewayMerchantId,
                        allowedPaymentMethods = POGooglePayRequestBuilder
                            .allowedPaymentMethods(configuration.card)
                            .toString(),
                        paymentDataRequest = POGooglePayRequestBuilder.paymentDataRequest(configuration)
                    ) else null
            }
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
            is CardCustomerToken -> CustomerToken(
                id = paymentMethod.configuration.customerTokenId,
                configuration = paymentMethod.configuration,
                display = paymentMethod.display,
                isExpress = paymentMethod.flow == express
            )
            is AlternativePaymentCustomerToken -> CustomerToken(
                id = paymentMethod.configuration.customerTokenId,
                configuration = paymentMethod.configuration,
                display = paymentMethod.display,
                isExpress = paymentMethod.flow == express
            )
            else -> null
        }
    }

    private fun GooglePayConfiguration.map(
        amount: String,
        currency: String,
        configuration: PODynamicCheckoutPaymentMethod.GooglePayConfiguration
    ) = POGooglePayConfiguration(
        gateway = configuration.gateway,
        gatewayMerchantId = configuration.gatewayMerchantId,
        card = POGooglePayConfiguration.CardConfiguration(
            allowedAuthMethods = configuration.allowedAuthMethods,
            allowedCardNetworks = configuration.allowedCardNetworks,
            allowPrepaidCards = configuration.allowPrepaidCards,
            allowCreditCards = configuration.allowCreditCards,
            assuranceDetailsRequired = true,
            billingAddressRequired = billingAddress != null,
            billingAddressParameters = billingAddress?.let {
                BillingAddressParameters(
                    format = when (it.format) {
                        MIN -> BillingAddressParameters.Format.MIN
                        FULL -> BillingAddressParameters.Format.FULL
                    },
                    phoneNumberRequired = it.phoneNumberRequired
                )
            }
        ),
        paymentData = POGooglePayConfiguration.PaymentDataConfiguration(
            transactionInfo = TransactionInfo(
                currencyCode = currency,
                countryCode = null, // TODO: get from dashboard configuration when ready
                transactionId = UUID.randomUUID().toString(),
                totalPrice = amount,
                totalPriceLabel = totalPriceLabel,
                totalPriceStatus = when (totalPriceStatus) {
                    FINAL -> TransactionInfo.TotalPriceStatus.FINAL
                    ESTIMATED -> TransactionInfo.TotalPriceStatus.ESTIMATED
                },
                checkoutOption = when (checkoutOption) {
                    DEFAULT -> TransactionInfo.CheckoutOption.DEFAULT
                    COMPLETE_IMMEDIATE_PURCHASE -> TransactionInfo.CheckoutOption.COMPLETE_IMMEDIATE_PURCHASE
                }
            ),
            merchantName = merchantName,
            emailRequired = emailRequired,
            shippingAddressRequired = shippingAddress != null,
            shippingAddressParameters = shippingAddress?.let {
                ShippingAddressParameters(
                    allowedCountryCodes = it.allowedCountryCodes,
                    phoneNumberRequired = it.phoneNumberRequired
                )
            }
        )
    )

    //region Images

    private suspend fun preloadAllImages(paymentMethods: List<PaymentMethod>) {
        coroutineScope {
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
                async { preloadImage(url) }
            }
            deferredResults.awaitAll()
        }
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

    private fun originalPaymentMethod(id: String): PODynamicCheckoutPaymentMethod? =
        _state.value.invoice.paymentMethods?.find {
            when (it) {
                is PODynamicCheckoutPaymentMethod.Card -> PaymentMethodId.CARD == id
                is PODynamicCheckoutPaymentMethod.GooglePay -> it.configuration.gatewayMerchantId == id
                is PODynamicCheckoutPaymentMethod.AlternativePayment -> it.configuration.gatewayConfigurationId == id
                is AlternativePaymentCustomerToken -> it.configuration.customerTokenId == id
                is CardCustomerToken -> it.configuration.customerTokenId == id
                Unknown -> false
            }
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
                if (_state.value.delayedSuccess) {
                    _completion.update { Success }
                } else {
                    POLogger.warn("Dismissed: %s", event.failure)
                    _completion.update { Failure(event.failure) }
                }
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

    private fun POCardTokenizationConfiguration.apply(configuration: CardConfiguration) =
        copy(
            cvcRequired = configuration.cvcRequired,
            isCardholderNameFieldVisible = configuration.cardholderNameRequired,
            billingAddress = billingAddress.copy(
                mode = configuration.billingAddress.collectionMode.map(),
                countryCodes = configuration.billingAddress.restrictToCountryCodes
            ),
            savingAllowed = configuration.savingAllowed
        )

    private fun POBillingAddressCollectionMode.map(): CollectionMode =
        when (this) {
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
                            paymentDataRequest = paymentMethod.paymentDataRequest
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
                    authorizeInvoice(source = paymentMethod.configuration.customerTokenId)
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

    fun handleGooglePay(result: ProcessOutResult<POGooglePayCardTokenizationData>) {
        result.onSuccess { response ->
            authorizeInvoice(source = response.card.id)
        }.onFailure { failure ->
            invalidateInvoice(
                reason = PODynamicCheckoutInvoiceInvalidationReason.Failure(failure)
            )
        }
    }

    fun handleAlternativePayment(result: ProcessOutResult<POAlternativePaymentMethodResponse>) {
        result.onSuccess { response ->
            authorizeInvoice(
                source = response.gatewayToken,
                allowFallbackToSale = true
            )
        }.onFailure { failure ->
            invalidateInvoice(
                reason = PODynamicCheckoutInvoiceInvalidationReason.Failure(failure)
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

    private fun collectTokenizedCard() {
        interactorScope.launch {
            cardTokenizationEventDispatcher.processTokenizedCardRequest.collect { request ->
                _state.update { it.copy(processingPaymentMethodId = selectedPaymentMethod()?.id) }
                authorizeInvoice(
                    source = request.card.id,
                    saveSource = request.saveCard,
                    clientSecret = configuration.invoiceRequest.clientSecret
                )
            }
        }
    }

    private fun authorizeInvoice(
        source: String,
        saveSource: Boolean = false,
        allowFallbackToSale: Boolean = false,
        clientSecret: String? = null
    ) {
        val paymentMethodId = _state.value.processingPaymentMethodId
        if (paymentMethodId == null) {
            invalidateInvoice(
                reason = PODynamicCheckoutInvoiceInvalidationReason.Failure(
                    failure = ProcessOutResult.Failure(
                        code = Internal(),
                        message = "Failed to authorize invoice: payment method ID is null."
                    )
                )
            )
            return
        }
        val paymentMethod = originalPaymentMethod(paymentMethodId)
        if (paymentMethod == null) {
            invalidateInvoice(
                reason = PODynamicCheckoutInvoiceInvalidationReason.Failure(
                    failure = ProcessOutResult.Failure(
                        code = Internal(),
                        message = "Failed to authorize invoice: payment method is null."
                    )
                )
            )
            return
        }
        interactorScope.launch {
            val request = PODynamicCheckoutInvoiceAuthorizationRequest(
                request = POInvoiceAuthorizationRequest(
                    invoiceId = _state.value.invoice.id,
                    source = source,
                    saveSource = saveSource,
                    allowFallbackToSale = allowFallbackToSale,
                    clientSecret = clientSecret
                ),
                paymentMethod = paymentMethod
            )
            eventDispatcher.send(request)
        }
    }

    private fun collectInvoiceAuthorizationRequest() {
        eventDispatcher.subscribeForResponse<PODynamicCheckoutInvoiceAuthorizationResponse>(
            coroutineScope = interactorScope
        ) { response ->
            invoicesService.authorizeInvoice(
                request = response.request,
                threeDSService = threeDSService
            )
        }
    }

    private fun collectAuthorizeInvoiceResult() {
        interactorScope.launch {
            invoicesService.authorizeInvoiceResult.collect { result ->
                if (selectedPaymentMethod() is Card) {
                    cardTokenizationEventDispatcher.complete(result)
                } else {
                    result.onSuccess {
                        handleSuccess()
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
                    is CardTokenizationCompletion.Success -> handleSuccess()
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
                    NativeAlternativePaymentCompletion.Success -> handleSuccess()
                    is NativeAlternativePaymentCompletion.Failure -> invalidateInvoice(
                        reason = PODynamicCheckoutInvoiceInvalidationReason.Failure(completion.failure)
                    )
                    else -> {}
                }
            }
        }
    }

    private fun handleSuccess() {
        configuration.paymentSuccess?.let { paymentSuccess ->
            _state.update { it.copy(delayedSuccess = true) }
            handler.postDelayed(delayInMillis = paymentSuccess.durationSeconds * 1000L) {
                _completion.update { Success }
            }
        } ?: _completion.update { Success }
    }

    private fun dispatch(event: PODynamicCheckoutEvent) {
        interactorScope.launch {
            eventDispatcher.send(event)
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

    private fun dispatchFailure() {
        interactorScope.launch {
            _completion.collect {
                if (it is Failure) {
                    dispatch(DidFail(it.failure))
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

    fun onCleared() {
        threeDSService.close()
        handler.removeCallbacksAndMessages(null)
    }
}
