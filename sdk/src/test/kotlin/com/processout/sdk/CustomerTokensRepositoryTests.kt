package com.processout.sdk

import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.repository.CustomerTokensRepository
import com.processout.sdk.api.repository.InvoicesRepository
import com.processout.sdk.api.repository.POCardsRepository
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
class CustomerTokensRepositoryTests {

    @Rule
    @JvmField
    val setupRule = SetupRule()

    private lateinit var customerTokens: CustomerTokensRepository
    private lateinit var invoices: InvoicesRepository
    private lateinit var cards: POCardsRepository

    @Before
    fun setUp() {
        customerTokens = ProcessOut.instance.apiGraph.repositoryGraph.customerTokensRepository
        invoices = ProcessOut.instance.apiGraph.repositoryGraph.invoicesRepository
        cards = ProcessOut.instance.cards
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
                                customerTokens.createCustomerToken(
                                    POCreateCustomerTokenRequest(
                                        customerId = customer.id,
                                        body = POCreateCustomerTokenRequestBody()
                                    )
                                )
                                    .let { createTokenResult ->
                                        createTokenResult.assertFailure()
                                        createTokenResult.handleSuccess { token ->
                                            token.id.let {
                                                customerTokens.assignCustomerToken(
                                                    request = POAssignCustomerTokenRequest(
                                                        customerId = customer.id,
                                                        tokenId = it,
                                                        source = card.id,
                                                        enableThreeDS2 = false,
                                                        verify = true
                                                    )
                                                ).let { assignTokenResult ->
                                                    assignTokenResult.assertFailure()
                                                    assignTokenResult.handleSuccess { assignToken ->
                                                        assert(assignToken.token?.id.isNullOrBlank().not())
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
