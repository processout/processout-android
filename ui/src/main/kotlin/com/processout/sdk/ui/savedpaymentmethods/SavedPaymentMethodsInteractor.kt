package com.processout.sdk.ui.savedpaymentmethods

import android.app.Application
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.ImageResult
import com.processout.sdk.R
import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.event.POSavedPaymentMethodsEvent
import com.processout.sdk.api.model.event.POSavedPaymentMethodsEvent.*
import com.processout.sdk.api.model.request.PODeleteCustomerTokenRequest
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.*
import com.processout.sdk.api.model.response.POImageResource
import com.processout.sdk.api.service.POCustomerTokensService
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.core.POFailure.Code.Cancelled
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogAttribute
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import com.processout.sdk.ui.base.BaseInteractor
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsCompletion.Awaiting
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsCompletion.Failure
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsEvent.Action
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsEvent.Dismiss
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsInteractorState.ActionId
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsInteractorState.PaymentMethod
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class SavedPaymentMethodsInteractor(
    private val app: Application,
    private val configuration: POSavedPaymentMethodsConfiguration,
    private val invoicesService: POInvoicesService,
    private val customerTokensService: POCustomerTokensService,
    private val eventDispatcher: POEventDispatcher = POEventDispatcher,
    private var logAttributes: Map<String, String> = logAttributes(
        invoiceId = configuration.invoiceRequest.invoiceId
    )
) : BaseInteractor() {

    private companion object {
        fun logAttributes(invoiceId: String): Map<String, String> =
            mapOf(POLogAttribute.INVOICE_ID to invoiceId)
    }

    private val _completion = MutableStateFlow<SavedPaymentMethodsCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    init {
        interactorScope.launch {
            POLogger.info("Starting saved payment methods.")
            dispatch(WillStart)
            dispatchFailure()
            fetchPaymentMethods()
            POLogger.info("Started saved payment methods.")
            dispatch(DidStart)
        }
    }

    private fun initState() = SavedPaymentMethodsInteractorState(
        loading = true,
        customerId = null,
        paymentMethods = emptyList(),
        cancelActionId = ActionId.CANCEL
    )

    private suspend fun fetchPaymentMethods() {
        invoicesService.invoice(configuration.invoiceRequest)
            .onSuccess { invoice ->
                val mappedPaymentMethods = invoice.paymentMethods?.map() ?: emptyList()
                preloadAllImages(paymentMethods = mappedPaymentMethods)
                _state.update {
                    it.copy(
                        loading = false,
                        customerId = invoice.customerId,
                        paymentMethods = mappedPaymentMethods
                    )
                }
            }.onFailure { failure ->
                _completion.update { Failure(failure) }
            }
    }

    private fun List<PODynamicCheckoutPaymentMethod>.map(): List<PaymentMethod> =
        mapNotNull {
            when (it) {
                is CardCustomerToken -> paymentMethod(it.display, it.configuration)
                is AlternativePaymentCustomerToken -> paymentMethod(it.display, it.configuration)
                else -> null
            }
        }

    private fun paymentMethod(
        display: Display,
        configuration: CustomerTokenConfiguration
    ) = PaymentMethod(
        customerTokenId = configuration.customerTokenId,
        logo = display.logo,
        name = display.name,
        description = display.description,
        deleteAction = if (configuration.deletingAllowed)
            SavedPaymentMethodsInteractorState.Action(
                id = ActionId.DELETE,
                processing = false
            ) else null
    )

    //region Images

    private suspend fun preloadAllImages(paymentMethods: List<PaymentMethod>) {
        coroutineScope {
            val logoUrls = mutableListOf<String>()
            paymentMethods.forEach {
                logoUrls.addAll(it.logo.urls())
            }
            val deferredResults = logoUrls.map { url ->
                async { preloadImage(url) }
            }
            deferredResults.awaitAll()
        }
    }

    private fun POImageResource.urls(): List<String> {
        val urls = mutableListOf(lightUrl.raster)
        darkUrl?.raster?.let { urls.add(it) }
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

    fun onEvent(event: SavedPaymentMethodsEvent) {
        when (event) {
            is Action -> when (event.actionId) {
                ActionId.DELETE -> event.paymentMethodId?.let { delete(it) }
                ActionId.CANCEL -> cancel()
            }
            is Dismiss -> {
                POLogger.info("Dismissed: %s", event.failure)
                dispatch(DidFail(event.failure))
            }
        }
    }

    private fun delete(customerTokenId: String) {
        val customerId = _state.value.customerId ?: return
        interactorScope.launch {
            update(
                customerTokenId = customerTokenId,
                processing = true,
                errorMessage = null
            )
            customerTokensService.deleteCustomerToken(
                PODeleteCustomerTokenRequest(
                    customerId = customerId,
                    tokenId = customerTokenId,
                    clientSecret = configuration.invoiceRequest.clientSecret ?: String()
                )
            ).onSuccess {
                _state.update { state ->
                    state.copy(
                        paymentMethods = state.paymentMethods
                            .filterNot { it.customerTokenId == customerTokenId }
                    )
                }
                dispatch(
                    DidDeleteCustomerToken(
                        customerId = customerId,
                        tokenId = customerTokenId
                    )
                )
            }.onFailure {
                update(
                    customerTokenId = customerTokenId,
                    processing = false,
                    errorMessage = app.getString(R.string.po_saved_payment_methods_error_generic)
                )
            }
        }
    }

    private fun update(
        customerTokenId: String,
        processing: Boolean,
        errorMessage: String?
    ) {
        _state.update { state ->
            state.copy(
                paymentMethods = state.paymentMethods.map {
                    if (it.customerTokenId == customerTokenId)
                        it.copy(deleteAction = it.deleteAction?.copy(processing = processing))
                    else it
                },
                errorMessage = errorMessage
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

    private fun dispatch(event: POSavedPaymentMethodsEvent) {
        interactorScope.launch {
            eventDispatcher.send(event)
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
}
