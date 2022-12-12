package com.processout.sdk

import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.model.request.POCardTokenizationRequest
import com.processout.sdk.api.model.request.POCreateCustomerRequest
import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.model.request.POCustomerTokenRequest
import com.processout.sdk.api.repository.CardsRepository
import com.processout.sdk.api.repository.CustomerTokensRepository
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
class CustomerTokensRepositoryUnitTests {

    @Rule
    @JvmField
    val setupRule = SetupRule()

    private lateinit var invoices: InvoicesRepository
    private lateinit var cards: CardsRepository
    private lateinit var customerTokens: CustomerTokensRepository

    @Before
    fun setUp() {
        invoices = ProcessOutApi.instance.invoices
        cards = ProcessOutApi.instance.cards
        customerTokens = ProcessOutApi.instance.customerTokens
    }

    @Test
    fun assignCustomerToken() = runBlocking {
        val request = POCardTokenizationRequest(
            name = "John Doe",
            number = "4242424242424242",
            expMonth = 10,
            expYear = 2030,
            cvc = "123"
        )
        customerTokens.createCustomer(
            POCreateCustomerRequest(
                "John",
                "Doe",
            )
        ).let { createCustomerResult ->
            createCustomerResult.assertFailure()
            createCustomerResult.handleSuccess { customer ->
                invoices.createInvoice(
                    POCreateInvoiceRequest("sandbox", "1", "USD")
                ).let { invoiceResult ->
                    invoiceResult.assertFailure()
                    invoiceResult.handleSuccess {
                        cards.tokenize(request).let { cardResult ->
                            cardResult.assertFailure()
                            cardResult.handleSuccess { card ->
                                customerTokens.createCustomerToken(customer.id)
                                    .let { createTokenResult ->
                                        createTokenResult.assertFailure()
                                        createTokenResult.handleSuccess { createToken ->
                                            createToken.customerToken.id.let {
                                                customerTokens.assignCustomerToken(
                                                    customerId = customer.id,
                                                    tokenId = it,
                                                    request = POCustomerTokenRequest(
                                                        source = card.id, verify = true
                                                    )
                                                ).let { assingTokenResult ->
                                                    assingTokenResult.assertFailure()
                                                    assingTokenResult.handleSuccess { assingToken ->
                                                        assert(assingToken.customerToken.id.isNotEmpty())
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
        }
    }
}
