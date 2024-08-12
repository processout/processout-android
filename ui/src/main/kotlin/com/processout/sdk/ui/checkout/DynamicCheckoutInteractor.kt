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
import com.processout.sdk.api.model.request.PODynamicCheckoutInvoiceInvalidationReason.Error
import com.processout.sdk.api.model.request.PODynamicCheckoutInvoiceInvalidationReason.PaymentMethodChanged
import com.processout.sdk.api.model.request.PODynamicCheckoutInvoiceRequest
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod
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
import com.processout.sdk.ui.checkout.DynamicCheckoutCompletion.*
import com.processout.sdk.ui.checkout.DynamicCheckoutExtendedEvent.*
import com.processout.sdk.ui.checkout.DynamicCheckoutInteractorState.ActionId
import com.processout.sdk.ui.checkout.DynamicCheckoutInteractorState.PaymentMethod
import com.processout.sdk.ui.checkout.DynamicCheckoutInteractorState.PaymentMethod.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentCompletion
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class DynamicCheckoutInteractor(
    private val app: Application,
    private var invoiceRequest: POInvoiceRequest,
    private val invoicesService: POInvoicesService,
    private val eventDispatcher: PODefaultDynamicCheckoutEventDispatcher,
    private val cardTokenizationEventDispatcher: PODefaultCardTokenizationEventDispatcher,
    private val nativeAlternativePaymentEventDispatcher: PODefaultNativeAlternativePaymentMethodEventDispatcher
) : BaseInteractor() {

    private val _completion = MutableStateFlow<DynamicCheckoutCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    private var latestInvoiceRequest: PODynamicCheckoutInvoiceRequest? = null
    var onInvoiceChanged: ((POInvoiceRequest, PODynamicCheckoutInvoiceInvalidationReason) -> Unit)? = null

    init {
        start()
    }

    private fun start() {
        collectInvoice()
        dispatchEvents()
        fetchConfiguration()
    }

    fun restart(
        invoiceRequest: POInvoiceRequest,
        reason: PODynamicCheckoutInvoiceInvalidationReason
    ) {
        this.invoiceRequest = invoiceRequest
        val errorMessage = when (reason) {
            is Error -> app.getString(R.string.po_dynamic_checkout_error_generic)
            else -> null
        }
        reset(
            state = _state.value.copy(
                invoice = POInvoice(
                    id = invoiceRequest.invoiceId,
                    amount = String(),
                    currency = String()
                ),
                selectedPaymentMethodId = null,
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
        paymentMethods = emptyList(),
        selectedPaymentMethodId = null,
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
                            paymentMethods = mappedPaymentMethods
                        )
                    }
                }.onFailure { failure ->
                    _completion.update { Failure(failure) }
                }
        }
    }

    private fun List<PODynamicCheckoutPaymentMethod>.map(): List<PaymentMethod> =
        mapNotNull {
            when (it) {
                is PODynamicCheckoutPaymentMethod.Card -> Card(
                    configuration = it.configuration,
                    display = it.display
                )
                is PODynamicCheckoutPaymentMethod.GooglePay -> GooglePay(
                    configuration = it.configuration
                )
                is PODynamicCheckoutPaymentMethod.AlternativePayment -> {
                    val redirectUrl = it.configuration.redirectUrl
                    if (redirectUrl != null) {
                        AlternativePayment(
                            redirectUrl = redirectUrl,
                            display = it.display,
                            isExpress = it.flow == express
                        )
                    } else {
                        NativeAlternativePayment(
                            gatewayConfigurationId = it.configuration.gatewayConfigurationId,
                            display = it.display
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

    fun paymentMethod(id: String): PaymentMethod? =
        _state.value.paymentMethods.find { it.id == id }

    fun onEvent(event: DynamicCheckoutExtendedEvent) {
        when (event) {
            is PaymentMethodSelected ->
                _state.update {
                    it.copy(
                        selectedPaymentMethodId = event.id,
                        errorMessage = null
                    )
                }
            is Action -> when (event.id) {
                ActionId.CANCEL -> cancel()
                else -> submit(event.id)
            }
            is ActionConfirmationRequested -> {
                // TODO
            }
            is Dismiss -> {
                POLogger.warn("Dismissed: %s", event.failure)
                _completion.update { Failure(event.failure) }
            }
        }
    }

    private fun submit(id: String) {
        val paymentMethod = paymentMethod(id)
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

    fun onCardTokenization(completion: CardTokenizationCompletion) {
        when (completion) {
            is CardTokenizationCompletion.Success -> _completion.update { Success }
            is CardTokenizationCompletion.Failure -> {
                // TODO
            }
            else -> {}
        }
    }

    fun onNativeAlternativePayment(completion: NativeAlternativePaymentCompletion) {
        when (completion) {
            NativeAlternativePaymentCompletion.Success -> _completion.update { Success }
            is NativeAlternativePaymentCompletion.Failure ->
                if (eventDispatcher.subscribedForInvoiceRequest()) {
                    requestInvoice(reason = Error(completion.failure))
                } else {
                    _completion.update { Failure(completion.failure) }
                }
            else -> {}
        }
    }

    private fun requestInvoice(reason: PODynamicCheckoutInvoiceInvalidationReason) {
        interactorScope.launch {
            val request = PODynamicCheckoutInvoiceRequest(
                invoice = _state.value.invoice,
                reason = reason
            )
            latestInvoiceRequest = request
            eventDispatcher.send(request)
        }
    }

    private fun collectInvoice() {
        interactorScope.launch {
            eventDispatcher.invoiceResponse.collect { response ->
                if (response.uuid == latestInvoiceRequest?.uuid) {
                    latestInvoiceRequest = null
                    val invoiceRequest = response.invoiceRequest
                    val reason = response.reason
                    if (invoiceRequest == null) {
                        val failure = when (reason) {
                            PaymentMethodChanged -> ProcessOutResult.Failure(
                                code = Generic(),
                                message = "Payment method has been changed by the user during processing. " +
                                        "Invoice invalidated and the new one has not been provided."
                            )
                            is Error -> reason.failure
                        }
                        _completion.update { Failure(failure) }
                    } else {
                        onInvoiceChanged?.invoke(invoiceRequest, reason)
                    }
                }
            }
        }
    }

    private fun dispatchEvents() {
        interactorScope.launch {
            cardTokenizationEventDispatcher.events.collect { eventDispatcher.send(it) }
        }
        interactorScope.launch {
            nativeAlternativePaymentEventDispatcher.events.collect { eventDispatcher.send(it) }
        }
    }
}
