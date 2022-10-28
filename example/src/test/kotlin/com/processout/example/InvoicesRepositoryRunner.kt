package com.processout.example

import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodRequest
import com.processout.sdk.core.handleSuccess
import kotlinx.coroutines.runBlocking
import org.junit.BeforeClass
import org.junit.Test

class InvoicesRepositoryRunner {

    companion object {
        @JvmStatic
        @BeforeClass
        fun configure() {
            ProcessOutApiConfiguration.configure()
        }
    }

    private val invoices = ProcessOutApi.instance.invoices

    @Test
    fun initiateNativePayment() = runBlocking {
        invoices.createInvoice(
            POCreateInvoiceRequest("adyen", "100", "USD")
        ).let { result ->
            result.assertFailure()
            result.handleSuccess { invoice ->
                val request = PONativeAlternativePaymentMethodRequest(
                    invoice.id,
                    "gway_conf_1F5fIrgLktm5fUBzrgN4jQA5RQlONhwG.sandbox",
                    mapOf("email" to "test@processout.com")
                )
                invoices.initiatePayment(request).assertFailure()
            }
        }
    }
}
