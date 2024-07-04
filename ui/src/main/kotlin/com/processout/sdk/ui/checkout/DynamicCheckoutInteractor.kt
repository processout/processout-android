package com.processout.sdk.ui.checkout

import android.app.Application
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.core.POFailure.Code.Generic
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import com.processout.sdk.ui.base.BaseInteractor
import com.processout.sdk.ui.checkout.DynamicCheckoutCompletion.Awaiting
import com.processout.sdk.ui.checkout.DynamicCheckoutCompletion.Failure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class DynamicCheckoutInteractor(
    private val app: Application,
    private val invoiceId: String,
    private val invoicesService: POInvoicesService
) : BaseInteractor() {

    private val _completion = MutableStateFlow<DynamicCheckoutCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    init {
        fetchConfiguration()
    }

    private fun initState() = DynamicCheckoutInteractorState(
        loading = true,
        paymentMethods = emptyList()
    )

    private fun fetchConfiguration() {
        interactorScope.launch {
            invoicesService.invoice(invoiceId)
                .onSuccess { invoice ->
                    val paymentMethods = invoice.paymentMethods
                    if (paymentMethods.isNullOrEmpty()) {
                        _completion.update {
                            Failure(
                                ProcessOutResult.Failure(
                                    code = Generic(),
                                    message = "Missing configuration."
                                )
                            )
                        }
                        return@launch
                    }
                    _state.update {
                        it.copy(
                            loading = false,
                            paymentMethods = paymentMethods
                        )
                    }
                }.onFailure { failure ->
                    _completion.update { Failure(failure) }
                }
        }
    }

    fun onEvent(event: DynamicCheckoutEvent) {
        // TODO
    }
}
