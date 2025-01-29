package com.processout.sdk.ui.checkout

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.compose.ui.text.input.TextFieldValue
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
import com.processout.sdk.api.model.event.PODynamicCheckoutEvent.*
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent.WillSubmitParameters
import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.response.*
import com.processout.sdk.api.model.response.POBillingAddressCollectionMode.*
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.*
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.Flow.express
import com.processout.sdk.api.model.response.POTransaction.Status.*
import com.processout.sdk.api.service.PO3DSService
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.api.service.googlepay.POGooglePayConfiguration
import com.processout.sdk.api.service.googlepay.POGooglePayConfiguration.CardConfiguration.BillingAddressParameters
import com.processout.sdk.api.service.googlepay.POGooglePayConfiguration.PaymentDataConfiguration.ShippingAddressParameters
import com.processout.sdk.api.service.googlepay.POGooglePayConfiguration.PaymentDataConfiguration.TransactionInfo
import com.processout.sdk.api.service.googlepay.POGooglePayRequestBuilder
import com.processout.sdk.api.service.googlepay.POGooglePayService
import com.processout.sdk.api.service.proxy3ds.PODefaultProxy3DSService
import com.processout.sdk.core.POFailure.Code.Cancelled
import com.processout.sdk.core.POFailure.Code.Generic
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
import com.processout.sdk.ui.napm.NativeAlternativePaymentSideEffect
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

    private val _sideEffects = Channel<DynamicCheckoutSideEffect>()
    val sideEffects = _sideEffects.receiveAsFlow()

    private val handler = Handler(Looper.getMainLooper())

    private var authorizeInvoiceJob: AuthorizeInvoiceJob? = null
    private var latestInvoiceRequest: PODynamicCheckoutInvoiceRequest? = null

    init {
        interactorScope.launch {
            POLogger.info("Starting dynamic checkout.")
            dispatch(WillStart)
            start()
            POLogger.info("Started: waiting for user input.")
            dispatch(DidStart)
        }
    }

    private fun initState() = DynamicCheckoutInteractorState(
        loading = true,
        invoice = null,
        paymentMethods = emptyList()
    )

    private suspend fun start() {
        handleCompletions()
        dispatchEvents()
        collectInvoice()
        collectInvoiceAuthorizationRequest()
        collectTokenizedCard()
        collectPreferredScheme()
        collectDefaultValues()
        fetchConfiguration()
    }

    private fun restart(invoiceRequest: POInvoiceRequest) {
        interactorScope.coroutineContext.cancelChildren()
        handler.removeCallbacksAndMessages(null)
        cancelWebAuthorization()
        configuration = configuration.copy(invoiceRequest = invoiceRequest)
        logAttributes = logAttributes(invoiceId = invoiceRequest.invoiceId)
        interactorScope.launch { start() }
    }

    private fun cancelWebAuthorization() {
        with(_state.value) {
            if (selectedPaymentMethod != null || pendingSubmitPaymentMethod != null) {
                interactorScope.launch {
                    _sideEffects.send(DynamicCheckoutSideEffect.CancelWebAuthorization)
                }
            }
        }
    }

    private suspend fun fetchConfiguration() {
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

    private suspend fun setStartedState(invoice: POInvoice) {
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
            return
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
                paymentMethods = mappedPaymentMethods
            )
        }
        handleSelectedPaymentMethod()
        handlePendingSubmit()
    }

    private fun handleSelectedPaymentMethod() {
        if (configuration.preselectSinglePaymentMethod) {
            _state.value.paymentMethods
                .partition { it.isExpress() }
                .let { pair ->
                    val expressPaymentMethods = pair.first
                    val regularPaymentMethods = pair.second
                    if (expressPaymentMethods.isEmpty() && regularPaymentMethods.size == 1) {
                        _state.update { it.copy(selectedPaymentMethod = regularPaymentMethods.firstOrNull()) }
                    }
                }
        }
        _state.value.selectedPaymentMethod?.id?.let { id ->
            paymentMethod(id)?.let { start(it) }
                .orElse {
                    _state.update {
                        it.copy(
                            selectedPaymentMethod = null,
                            errorMessage = app.getString(R.string.po_dynamic_checkout_error_method_unavailable)
                        )
                    }
                }
        }
    }

    private fun handlePendingSubmit() {
        _state.value.pendingSubmitPaymentMethod?.id?.let { id ->
            _state.update { it.copy(pendingSubmitPaymentMethod = null) }
            paymentMethod(id)?.let {
                submit(
                    paymentMethod = it,
                    dispatchEvents = false
                )
            }.orElse {
                _state.update {
                    it.copy(errorMessage = app.getString(R.string.po_dynamic_checkout_error_method_unavailable))
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
                original = paymentMethod,
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
                        original = paymentMethod,
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
                        original = paymentMethod,
                        gatewayConfigurationId = paymentMethod.configuration.gatewayConfigurationId,
                        redirectUrl = redirectUrl,
                        savePaymentMethodField = if (paymentMethod.configuration.savingAllowed) {
                            Field(
                                id = FieldId.SAVE_PAYMENT_METHOD,
                                value = TextFieldValue(text = "false")
                            )
                        } else null,
                        display = paymentMethod.display,
                        isExpress = paymentMethod.flow == express
                    )
                } else {
                    NativeAlternativePayment(
                        id = paymentMethod.configuration.gatewayConfigurationId,
                        original = paymentMethod,
                        gatewayConfigurationId = paymentMethod.configuration.gatewayConfigurationId,
                        display = paymentMethod.display
                    )
                }
            }
            is CardCustomerToken -> CustomerToken(
                id = paymentMethod.configuration.customerTokenId,
                original = paymentMethod,
                configuration = paymentMethod.configuration,
                display = paymentMethod.display,
                isExpress = paymentMethod.flow == express
            )
            is AlternativePaymentCustomerToken -> CustomerToken(
                id = paymentMethod.configuration.customerTokenId,
                original = paymentMethod,
                configuration = paymentMethod.configuration,
                display = paymentMethod.display,
                isExpress = paymentMethod.flow == express
            )
            Unknown -> null
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
                countryCode = configuration.countryCode,
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

    fun onEvent(event: DynamicCheckoutEvent) {
        when (event) {
            is PaymentMethodSelected -> onPaymentMethodSelected(event)
            is FieldValueChanged -> onFieldValueChanged(event)
            is FieldFocusChanged -> onFieldFocusChanged(event)
            is Action -> onAction(event)
            is ActionConfirmationRequested -> onActionConfirmationRequested(event)
            is DialogAction -> onDialogAction(event)
            is GooglePayResult -> handleGooglePay(event.paymentMethodId, event.result)
            is AlternativePaymentResult -> handleAlternativePayment(event.paymentMethodId, event.result)
            is PermissionRequestResult -> handlePermission(event)
            is CustomerTokenDeleted -> deleteLocalCustomerToken(event.tokenId)
            is Dismiss -> dismiss(event)
        }
    }

    private fun paymentMethod(id: String): PaymentMethod? =
        _state.value.paymentMethods.find { it.id == id }

    private fun activePaymentMethod(): PaymentMethod? =
        with(_state.value) {
            processingPaymentMethod ?: selectedPaymentMethod
        }

    private fun onPaymentMethodSelected(event: PaymentMethodSelected) {
        val state = _state.value
        if (event.id == state.selectedPaymentMethod?.id) {
            return
        }
        paymentMethod(event.id)?.let { paymentMethod ->
            dispatch(DidSelectPaymentMethod(paymentMethod = paymentMethod.original))
            resetPaymentMethods()
            if (state.processingPaymentMethod != null) {
                invalidateInvoice(
                    reason = PODynamicCheckoutInvoiceInvalidationReason.PaymentMethodChanged
                )
            } else if (state.invoice != null) {
                start(paymentMethod)
            }
            _state.update {
                it.copy(
                    selectedPaymentMethod = paymentMethod,
                    pendingSubmitPaymentMethod = null,
                    errorMessage = null
                )
            }
        }
    }

    private fun resetPaymentMethods() {
        cardTokenization.reset()
        nativeAlternativePayment.reset()
        authorizeInvoiceJob?.cancel()
        authorizeInvoiceJob = null
    }

    private fun start(paymentMethod: PaymentMethod) {
        when (paymentMethod) {
            is Card -> cardTokenization.start(
                configuration = cardTokenization.configuration
                    .apply(paymentMethod.configuration)
            )
            is NativeAlternativePayment -> nativeAlternativePayment.start(
                configuration = nativeAlternativePayment.configuration.copy(
                    invoiceId = configuration.invoiceRequest.invoiceId,
                    gatewayConfigurationId = paymentMethod.gatewayConfigurationId
                )
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
        when (val paymentMethod = paymentMethod(event.paymentMethodId)) {
            is Card -> cardTokenization.onEvent(
                CardTokenizationEvent.FieldValueChanged(event.fieldId, event.value)
            )
            is NativeAlternativePayment -> nativeAlternativePayment.onEvent(
                NativeAlternativePaymentEvent.FieldValueChanged(event.fieldId, event.value)
            )
            else -> _state.update { state ->
                state.copy(
                    paymentMethods = state.paymentMethods.map {
                        if (it.id == paymentMethod?.id) {
                            updatedPaymentMethod(it, event.fieldId, event.value)
                        } else it
                    }
                )
            }
        }
    }

    private fun updatedPaymentMethod(
        paymentMethod: PaymentMethod,
        fieldId: String,
        value: TextFieldValue
    ): PaymentMethod = when (paymentMethod) {
        is AlternativePayment -> when (fieldId) {
            FieldId.SAVE_PAYMENT_METHOD -> with(paymentMethod) {
                POLogger.debug("Field is edited by the user: %s = %s", fieldId, value.text)
                copy(savePaymentMethodField = savePaymentMethodField?.copy(value = value))
            }
            else -> paymentMethod
        }
        else -> paymentMethod
    }

    private fun onFieldFocusChanged(event: FieldFocusChanged) {
        when (paymentMethod(event.paymentMethodId)) {
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
            ActionId.SUBMIT -> paymentMethod?.let {
                submit(
                    paymentMethod = it,
                    dispatchEvents = true
                )
            }
            ActionId.CANCEL -> cancel()
            ActionId.SAVED_PAYMENT_METHODS -> onSavedPaymentMethodsAction()
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

    private fun onActionConfirmationRequested(event: ActionConfirmationRequested) {
        POLogger.debug("Requested the user to confirm the action: %s", event.id)
        if (event.id == ActionId.CANCEL) {
            dispatch(DidRequestCancelConfirmation)
        }
    }

    private fun onDialogAction(event: DialogAction) {
        val paymentMethod = event.paymentMethodId?.let { paymentMethod(it) }
        when (paymentMethod) {
            is NativeAlternativePayment -> nativeAlternativePayment.onEvent(
                NativeAlternativePaymentEvent.DialogAction(
                    id = event.actionId,
                    isConfirmed = event.isConfirmed
                )
            )
            else -> {}
        }
    }

    private fun onSavedPaymentMethodsAction() {
        interactorScope.launch {
            _sideEffects.send(
                DynamicCheckoutSideEffect.SavedPaymentMethods(
                    invoiceRequest = configuration.invoiceRequest
                )
            )
        }
    }

    private fun PaymentMethod.isExpress(): Boolean =
        when (this) {
            is Card, is NativeAlternativePayment -> false
            is GooglePay -> true
            is AlternativePayment -> isExpress
            is CustomerToken -> isExpress
        }

    private fun submit(
        paymentMethod: PaymentMethod,
        dispatchEvents: Boolean
    ) {
        if (paymentMethod.id == _state.value.processingPaymentMethod?.id) {
            return
        }
        if (paymentMethod.isExpress()) {
            if (dispatchEvents) {
                dispatch(DidSelectPaymentMethod(paymentMethod = paymentMethod.original))
            }
            resetPaymentMethods()
            _state.update {
                it.copy(
                    selectedPaymentMethod = null,
                    errorMessage = null
                )
            }
        }
        if (_state.value.invoice == null) {
            _state.update { it.copy(pendingSubmitPaymentMethod = paymentMethod) }
            return
        }
        if (_state.value.processingPaymentMethod != null) {
            _state.update { it.copy(pendingSubmitPaymentMethod = paymentMethod) }
            invalidateInvoice(
                reason = PODynamicCheckoutInvoiceInvalidationReason.PaymentMethodChanged
            )
            return
        }
        when (paymentMethod) {
            is GooglePay -> submitGooglePay(paymentMethod)
            is AlternativePayment -> submitAlternativePayment(paymentMethod)
            is CustomerToken -> submitCustomerToken(paymentMethod)
            else -> {}
        }
    }

    private fun submitGooglePay(paymentMethod: GooglePay) {
        interactorScope.launch {
            _state.update { it.copy(processingPaymentMethod = paymentMethod) }
            _sideEffects.send(
                DynamicCheckoutSideEffect.GooglePay(
                    paymentMethodId = paymentMethod.id,
                    paymentDataRequest = paymentMethod.paymentDataRequest
                )
            )
        }
    }

    private fun submitAlternativePayment(paymentMethod: PaymentMethod) {
        if (paymentMethod is AlternativePayment) {
            val shouldSavePaymentMethod = paymentMethod.savePaymentMethodField
                ?.value?.text?.toBooleanStrictOrNull() ?: false
            if (shouldSavePaymentMethod) {
                _state.update { it.copy(processingPaymentMethod = paymentMethod) }
                authorizeInvoice(
                    paymentMethod = paymentMethod,
                    source = paymentMethod.gatewayConfigurationId,
                    saveSource = true,
                    allowFallbackToSale = true,
                    clientSecret = configuration.invoiceRequest.clientSecret
                )
                return
            }
        }
        val redirectUrl = when (paymentMethod) {
            is AlternativePayment -> paymentMethod.redirectUrl
            is CustomerToken -> paymentMethod.configuration.redirectUrl
            else -> null
        }
        if (redirectUrl.isNullOrBlank()) {
            handleAlternativePayment(
                paymentMethodId = paymentMethod.id,
                result = ProcessOutResult.Failure(
                    code = Generic(),
                    message = "Missing redirect URL in alternative payment configuration."
                )
            )
            return
        }
        val returnUrl = configuration.alternativePayment.returnUrl
        if (returnUrl.isNullOrBlank()) {
            handleAlternativePayment(
                paymentMethodId = paymentMethod.id,
                result = ProcessOutResult.Failure(
                    code = Generic(),
                    message = "Missing return URL in alternative payment configuration."
                )
            )
            return
        }
        interactorScope.launch {
            _state.update { it.copy(processingPaymentMethod = paymentMethod) }
            _sideEffects.send(
                DynamicCheckoutSideEffect.AlternativePayment(
                    paymentMethodId = paymentMethod.id,
                    redirectUrl = redirectUrl,
                    returnUrl = returnUrl
                )
            )
        }
    }

    private fun submitCustomerToken(paymentMethod: CustomerToken) {
        if (paymentMethod.configuration.redirectUrl != null) {
            submitAlternativePayment(paymentMethod)
        } else {
            _state.update { it.copy(processingPaymentMethod = paymentMethod) }
            authorizeInvoice(
                paymentMethod = paymentMethod,
                source = paymentMethod.configuration.customerTokenId
            )
        }
    }

    private fun handleGooglePay(
        paymentMethodId: String,
        result: ProcessOutResult<POGooglePayCardTokenizationData>
    ) {
        _state.value.processingPaymentMethod?.let { paymentMethod ->
            if (paymentMethod.id != paymentMethodId) {
                return
            }
            result.onSuccess { response ->
                authorizeInvoice(
                    paymentMethod = paymentMethod,
                    source = response.card.id
                )
            }.onFailure { failure ->
                invalidateInvoice(
                    reason = PODynamicCheckoutInvoiceInvalidationReason.Failure(failure)
                )
            }
        }
    }

    private fun handleAlternativePayment(
        paymentMethodId: String,
        result: ProcessOutResult<POAlternativePaymentMethodResponse>
    ) {
        _state.value.processingPaymentMethod?.let { paymentMethod ->
            if (paymentMethod.id != paymentMethodId) {
                return
            }
            result.onSuccess { response ->
                authorizeInvoice(
                    paymentMethod = paymentMethod,
                    source = response.gatewayToken,
                    allowFallbackToSale = true
                )
            }.onFailure { failure ->
                invalidateInvoice(
                    reason = PODynamicCheckoutInvoiceInvalidationReason.Failure(failure)
                )
            }
        }
    }

    private fun invalidateInvoice(reason: PODynamicCheckoutInvoiceInvalidationReason) {
        var errorMessage: String? = null
        if (reason is PODynamicCheckoutInvoiceInvalidationReason.Failure) {
            _state.value.processingPaymentMethod?.let { paymentMethod ->
                DidFailPayment(
                    failure = reason.failure,
                    paymentMethod = paymentMethod.original
                ).also { dispatch(it) }
            }
            if (reason.failure.code !is Cancelled) {
                errorMessage = app.getString(R.string.po_dynamic_checkout_error_generic)
            }
        }
        val currentInvoice = _state.value.invoice ?: POInvoice(id = configuration.invoiceRequest.invoiceId)
        resetPaymentMethods()
        _state.update {
            it.copy(
                invoice = null,
                selectedPaymentMethod = null,
                processingPaymentMethod = null,
                errorMessage = errorMessage
            )
        }
        if (latestInvoiceRequest == null) {
            val request = PODynamicCheckoutInvoiceRequest(
                currentInvoice = currentInvoice,
                invalidationReason = reason
            )
            latestInvoiceRequest = request
            interactorScope.launch {
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
                response.invoiceRequest?.let {
                    restart(it)
                }.orElse {
                    val failure = when (val reason = response.invalidationReason) {
                        PODynamicCheckoutInvoiceInvalidationReason.PaymentMethodChanged ->
                            ProcessOutResult.Failure(
                                code = Generic(),
                                message = "Payment method has been changed by the user during processing. " +
                                        "Invoice invalidated and the new one has not been provided."
                            )
                        is PODynamicCheckoutInvoiceInvalidationReason.Failure -> reason.failure
                    }
                    _completion.update { Failure(failure) }
                }
            }
        }
    }

    private fun collectTokenizedCard() {
        interactorScope.launch {
            cardTokenizationEventDispatcher.processTokenizedCardRequest.collect { request ->
                _state.value.selectedPaymentMethod?.let { paymentMethod ->
                    _state.update { it.copy(processingPaymentMethod = paymentMethod) }
                    authorizeInvoice(
                        paymentMethod = paymentMethod,
                        source = request.card.id,
                        saveSource = request.saveCard,
                        clientSecret = configuration.invoiceRequest.clientSecret
                    )
                }
            }
        }
    }

    private fun authorizeInvoice(
        paymentMethod: PaymentMethod,
        source: String,
        saveSource: Boolean = false,
        allowFallbackToSale: Boolean = false,
        clientSecret: String? = null
    ) {
        interactorScope.launch {
            val request = PODynamicCheckoutInvoiceAuthorizationRequest(
                request = POInvoiceAuthorizationRequest(
                    invoiceId = configuration.invoiceRequest.invoiceId,
                    source = source,
                    saveSource = saveSource,
                    allowFallbackToSale = allowFallbackToSale,
                    clientSecret = clientSecret
                ),
                paymentMethod = paymentMethod.original
            )
            eventDispatcher.send(request)
        }
    }

    @Suppress("DEPRECATION")
    private fun collectInvoiceAuthorizationRequest() {
        eventDispatcher.subscribeForResponse<PODynamicCheckoutInvoiceAuthorizationResponse>(
            coroutineScope = interactorScope
        ) { response ->
            val threeDSService = PODefaultProxy3DSService()
            val job = invoicesService.authorizeInvoice(
                request = response.request,
                threeDSService = threeDSService
            ) { result ->
                handleInvoiceAuthorization(
                    state = _state.value,
                    invoiceId = response.request.invoiceId,
                    result = result
                )
            }
            authorizeInvoiceJob = AuthorizeInvoiceJob(
                job = job,
                threeDSService = threeDSService
            )
        }
    }

    private fun handleInvoiceAuthorization(
        state: DynamicCheckoutInteractorState,
        invoiceId: String,
        result: ProcessOutResult<Unit>
    ) {
        if (invoiceId != state.invoice?.id) {
            return
        }
        when (state.processingPaymentMethod) {
            is Card -> interactorScope.launch {
                cardTokenizationEventDispatcher.complete(result)
            }
            is GooglePay,
            is AlternativePayment,
            is CustomerToken ->
                result.onSuccess {
                    handleSuccess()
                }.onFailure { failure ->
                    invalidateInvoice(
                        reason = PODynamicCheckoutInvoiceInvalidationReason.Failure(failure)
                    )
                }
            else -> {}
        }
    }

    private fun handleCompletions() {
        interactorScope.launch {
            _completion.collect { completion ->
                when (completion) {
                    Success -> dispatch(DidCompletePayment)
                    is Failure -> dispatch(DidFail(completion.failure))
                    else -> {}
                }
            }
        }
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
            cardTokenizationEventDispatcher.preferredSchemeRequest.collect { request ->
                eventDispatcher.send(request)
            }
        }
        interactorScope.launch {
            nativeAlternativePaymentEventDispatcher.events.collect { event ->
                if (event is WillSubmitParameters) {
                    _state.update { it.copy(processingPaymentMethod = _state.value.selectedPaymentMethod) }
                }
                eventDispatcher.send(event)
            }
        }
        interactorScope.launch {
            nativeAlternativePaymentEventDispatcher.defaultValuesRequest.collect { request ->
                eventDispatcher.send(request)
            }
        }
        interactorScope.launch {
            nativeAlternativePayment.sideEffects.collect { sideEffect ->
                when (sideEffect) {
                    is NativeAlternativePaymentSideEffect.PermissionRequest ->
                        activePaymentMethod()?.let { paymentMethod ->
                            _sideEffects.send(
                                DynamicCheckoutSideEffect.PermissionRequest(
                                    paymentMethodId = paymentMethod.id,
                                    permission = sideEffect.permission
                                )
                            )
                        }
                }
            }
        }
    }

    private fun handlePermission(result: PermissionRequestResult) {
        when (paymentMethod(result.paymentMethodId)) {
            is NativeAlternativePayment -> nativeAlternativePayment.onEvent(
                NativeAlternativePaymentEvent.PermissionRequestResult(
                    permission = result.permission,
                    isGranted = result.isGranted
                )
            )
            else -> {}
        }
    }

    private fun deleteLocalCustomerToken(tokenId: String) {
        _state.update { state ->
            state.copy(
                paymentMethods = state.paymentMethods
                    .filterNot { it.id == tokenId })
        }
    }

    private fun collectPreferredScheme() {
        eventDispatcher.subscribeForResponse<POCardTokenizationPreferredSchemeResponse>(
            coroutineScope = interactorScope
        ) { response ->
            interactorScope.launch {
                cardTokenizationEventDispatcher.preferredScheme(response)
            }
        }
    }

    private fun collectDefaultValues() {
        eventDispatcher.subscribeForResponse<PONativeAlternativePaymentMethodDefaultValuesResponse>(
            coroutineScope = interactorScope
        ) { response ->
            interactorScope.launch {
                nativeAlternativePaymentEventDispatcher.provideDefaultValues(response)
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

    private fun dismiss(event: Dismiss) {
        if (_state.value.delayedSuccess) {
            _completion.update { Success }
        } else {
            POLogger.warn("Dismissed: %s", event.failure)
            _completion.update { Failure(event.failure) }
        }
    }

    fun onCleared() {
        handler.removeCallbacksAndMessages(null)
    }

    private class AuthorizeInvoiceJob(
        val job: Job,
        val threeDSService: PO3DSService
    ) {
        fun cancel() {
            job.cancel()
            threeDSService.cleanup()
        }
    }
}
