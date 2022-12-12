package com.processout.sdk

import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.network.exception.ValidationException
import com.processout.sdk.api.repository.CardsRepository
import com.processout.sdk.api.repository.InvoicesRepository
import com.processout.sdk.config.SetupRule
import com.processout.sdk.config.TestApplication
import com.processout.sdk.config.assertFailure
import com.processout.sdk.core.handleFailure
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
class InvoicesRepositoryUnitTests {

    @Rule
    @JvmField
    val setupRule = SetupRule()

    private lateinit var invoices: InvoicesRepository
    private lateinit var cards: CardsRepository

    @Before
    fun setUp() {
        invoices = ProcessOutApi.instance.invoices
        cards = ProcessOutApi.instance.cards
    }

    @Test
    fun initiatePayment() = runBlocking {
        invoices.createInvoice(
            POCreateInvoiceRequest("sandbox", "100", "USD")
        ).let { result ->
            result.assertFailure()
            result.handleSuccess { invoice ->
                val request = PONativeAlternativePaymentMethodRequest(
                    invoice.id,
                    "gway_conf_tuZdFarVHkUJyD1zmUEV1I3TJ1DcVdtf.sandbox",
                    mapOf("email" to "test@processout.com")
                )
                invoices.initiatePayment(request).assertFailure()
            }
        }
    }

    @Test
    fun authorize() = runBlocking {
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
            result.handleSuccess { invoice ->
                cards.tokenize(request).let {
                    it.handleSuccess { card ->
                        invoices.authorize(
                            invoice.id,
                            POInvoiceAuthorizationRequest(card.id)
                        ).let { authResult ->
                            authResult.assertFailure()
                            authResult.handleSuccess { authSuccess ->
                                assert(authSuccess.customerAction == null)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun authorizeWith3DS() = runBlocking {
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
            result.handleSuccess { invoice ->
                cards.tokenize(request).let {
                    it.handleSuccess { card ->
                        invoices.authorize(
                            invoice.id,
                            POInvoiceAuthorizationRequest(card.id)
                        ).let { authResult ->
                            authResult.assertFailure()
                            authResult.handleSuccess { authSuccess ->
                                assert(authSuccess.customerAction is POCustomerActionResponse.UriData)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun fetchNativeAlternativePaymentMethodTransactionDetails() = runBlocking {
        invoices.createInvoice(
            POCreateInvoiceRequest("sandbox", "199", "USD")
        ).let { invoiceResult ->
            invoiceResult.assertFailure()
            invoiceResult.handleSuccess { invoice ->
                invoices.fetchNativeAlternativePaymentMethodTransactionDetails(
                    invoice.id,
                    "gway_conf_ux3ye8vh2c78c89s8ozp1f1ujixkl11k.adyenblik"
                ).assertFailure()
            }
        }
    }

    @Test
    fun captureWithValidationFailure() = runBlocking {
        invoices.createInvoice(
            POCreateInvoiceRequest("sandbox", "95", "PLN")
        ).let { invoiceResult ->
            invoiceResult.assertFailure()
            invoiceResult.handleSuccess { invoice ->
                invoices.capture(invoice.id).handleFailure { _, cause ->
                    assert(cause is ValidationException)
                }
            }
        }
    }
}
