package com.processout.sdk

import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POCardTokenizationRequest
import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.model.request.POInvoiceAuthorizationRequest
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.CustomerAction
import com.processout.sdk.api.repository.InvoicesRepository
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.configuration.PROCESSOUT_GATEWAY_CONFIGURATION_ID
import com.processout.sdk.configuration.TestApplication
import com.processout.sdk.configuration.TestSetupRule
import com.processout.sdk.configuration.assertFailure
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class InvoicesRepositoryTests {

    @Rule
    @JvmField
    val setupRule = TestSetupRule()

    private lateinit var invoices: InvoicesRepository
    private lateinit var cards: POCardsRepository

    @Before
    fun setUp() {
        invoices = ProcessOut.instance.apiGraph.repositoryGraph.invoicesRepository
        cards = ProcessOut.instance.cards
    }

    @Test
    @Ignore("Investigate why fails.")
    fun initiatePayment(): Unit = runBlocking {
        invoices.createInvoice(
            POCreateInvoiceRequest("sandbox", "100", "USD")
        ).let { result ->
            result.assertFailure()
            result.onSuccess { invoice ->
                val request = PONativeAlternativePaymentMethodRequest(
                    invoice.id,
                    "$PROCESSOUT_GATEWAY_CONFIGURATION_ID.sandbox",
                    mapOf("email" to "test@processout.com")
                )
                invoices.initiatePayment(request).assertFailure()
            }
        }
    }

    @Test
    fun authorize(): Unit = runBlocking {
        val request = POCardTokenizationRequest(
            name = "John Doe",
            number = "4242424242424242",
            expMonth = 10,
            expYear = 2030,
            cvc = "123"
        )

        invoices.createInvoice(
            POCreateInvoiceRequest("sandbox", "1", "USD")
        ).let { result ->
            result.assertFailure()
            result.onSuccess { invoice ->
                cards.tokenize(request).let {
                    it.onSuccess { card ->
                        invoices.authorizeInvoice(
                            POInvoiceAuthorizationRequest(invoice.id, card.id)
                        ).let { authResult ->
                            authResult.assertFailure()
                            authResult.onSuccess { authSuccess ->
                                assert(authSuccess.customerAction != null)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun authorizeWith3DS(): Unit = runBlocking {
        val request = POCardTokenizationRequest(
            name = "John Doe",
            number = "4242424242424242",
            expMonth = 10,
            expYear = 2030,
            cvc = "123"
        )

        invoices.createInvoice(
            POCreateInvoiceRequest("sandbox", "26", "USD")
        ).let { result ->
            result.assertFailure()
            result.onSuccess { invoice ->
                cards.tokenize(request).let {
                    it.onSuccess { card ->
                        invoices.authorizeInvoice(
                            POInvoiceAuthorizationRequest(invoice.id, card.id)
                        ).let { authResult ->
                            authResult.assertFailure()
                            authResult.onSuccess { authSuccess ->
                                assert(authSuccess.customerAction?.type() == CustomerAction.Type.URL)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    @Ignore("Investigate why fails.")
    fun fetchNativeAlternativePaymentMethodTransactionDetails(): Unit = runBlocking {
        invoices.createInvoice(
            POCreateInvoiceRequest("sandbox", "199", "USD")
        ).let { invoiceResult ->
            invoiceResult.assertFailure()
            invoiceResult.onSuccess { invoice ->
                invoices.fetchNativeAlternativePaymentMethodTransactionDetails(
                    invoice.id,
                    "$PROCESSOUT_GATEWAY_CONFIGURATION_ID.sandbox"
                ).assertFailure()
            }
        }
    }

    @Test
    fun captureWithGenericFailure(): Unit = runBlocking {
        invoices.createInvoice(
            POCreateInvoiceRequest("sandbox", "95", "PLN")
        ).let { invoiceResult ->
            invoiceResult.assertFailure()
            invoiceResult.onSuccess { invoice ->
                invoices.captureNativeAlternativePayment(
                    invoice.id,
                    "$PROCESSOUT_GATEWAY_CONFIGURATION_ID.sandbox"
                ).onFailure {
                    assert(it.code is POFailure.Code.Generic)
                }
            }
        }
    }
}
