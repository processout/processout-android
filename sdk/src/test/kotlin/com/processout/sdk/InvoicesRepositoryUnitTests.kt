package com.processout.sdk

import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.repository.CardsRepository
import com.processout.sdk.api.repository.InvoicesRepository
import com.processout.sdk.config.SetupRule
import com.processout.sdk.config.TestApplication
import com.processout.sdk.config.assertFailure
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
                        val cvcUpdateRequest = POCardUpdateCVCRequest(cvc = "123")
                        cards.updateCVC(card.id, cvcUpdateRequest).let {
                            it.handleSuccess {
                                invoices.authorize(invoice.id, POInvoiceAuthorizationRequest(
                                    card.id
                                )).let {
                                    it.assertFailure()
                                    it.handleSuccess {
                                        assert(it.customerAction == null)
                                    }
                                }
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
                        val cvcUpdateRequest = POCardUpdateCVCRequest(cvc = "123")
                        cards.updateCVC(card.id, cvcUpdateRequest).let {
                            it.handleSuccess {
                                invoices.authorize(invoice.id, POInvoiceAuthorizationRequest(
                                    card.id
                                )).let {
                                    it.assertFailure()
                                    it.handleSuccess {
                                        assert(it.customerAction is POCustomerActionResponse.UriData)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
