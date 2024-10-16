package com.processout.sdk

import android.os.Build
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.repository.CustomerTokensRepository
import com.processout.sdk.api.repository.InvoicesRepository
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.configuration.TestApplication
import com.processout.sdk.configuration.TestSetupRule
import com.processout.sdk.configuration.assertFailure
import com.processout.sdk.core.onSuccess
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE],
    application = TestApplication::class
)
class CustomerTokensRepositoryTests {

    @Rule
    @JvmField
    val setupRule = TestSetupRule()

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
    fun assignCustomerToken(): Unit = runBlocking {
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
            createCustomerResult.onSuccess { customer ->
                invoices.createInvoice(
                    POCreateInvoiceRequest("sandbox", "1", "USD")
                ).let { invoiceResult ->
                    invoiceResult.assertFailure()
                    invoiceResult.onSuccess {
                        cards.tokenize(request).let { cardResult ->
                            cardResult.assertFailure()
                            cardResult.onSuccess { card ->
                                customerTokens.createCustomerToken(
                                    POCreateCustomerTokenRequest(
                                        customerId = customer.id,
                                        body = POCreateCustomerTokenRequestBody()
                                    )
                                )
                                    .let { createTokenResult ->
                                        createTokenResult.assertFailure()
                                        createTokenResult.onSuccess { token ->
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
                                                    assignTokenResult.onSuccess { assignToken ->
                                                        assert(!assignToken.token?.id.isNullOrBlank())
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
