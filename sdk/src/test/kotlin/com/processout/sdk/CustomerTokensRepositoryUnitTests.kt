package com.processout.sdk

import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.model.request.*
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
        customerTokens.createCustomer(POCreateCustomerRequest(
            "John",
             "Doe",
        )).let { createCustomerResult ->
            createCustomerResult.assertFailure()
            createCustomerResult.handleSuccess { customer ->
                invoices.createInvoice(
                    POCreateInvoiceRequest("sandbox", "1", "USD")
                ).let { result ->
                    result.assertFailure()
                    result.handleSuccess { invoice ->
                        cards.tokenize(request).let { cardRes ->
                            cardRes.assertFailure()
                            cardRes.handleSuccess { card ->
                                customerTokens.createCustomerToken(customer.id).let { createTokenResult ->
                                    createTokenResult.assertFailure()
                                    createTokenResult.handleSuccess { createTokenResp ->
                                        createTokenResp.customerToken?.id?.let {
                                            customerTokens.assignCustomerToken(customer.id, it, POCustomerTokenRequest(
                                                source = card.id, verify = true,
                                            )).let { assingTokenRes ->
                                                assingTokenRes.assertFailure()
                                                assingTokenRes.handleSuccess { tokenResp ->
                                                    tokenResp.customerToken?.let { token ->
                                                        assert(token.id != "")
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
}
