package com.processout.example

import com.processout.example.config.SetupRule
import com.processout.example.config.TestApplication
import com.processout.example.config.assertFailure
import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodRequest
import com.processout.sdk.api.repository.InvoicesRepository
import com.processout.sdk.core.handleSuccess
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class InvoicesRepositoryRunner {

    @Rule
    @JvmField
    val setupRule = SetupRule()

    private lateinit var invoices: InvoicesRepository

    @Before
    fun setUp() {
        invoices = ProcessOutApi.instance.invoices
    }

    @Test
    fun initiatePayment() = runBlocking {
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
