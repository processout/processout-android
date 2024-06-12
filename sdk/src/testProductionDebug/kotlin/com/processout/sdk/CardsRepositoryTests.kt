package com.processout.sdk

import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POCardTokenizationRequest
import com.processout.sdk.api.model.request.POCardUpdateCVCRequest
import com.processout.sdk.api.model.request.POCardUpdateRequest
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.config.SetupRule
import com.processout.sdk.config.TestApplication
import com.processout.sdk.config.assertFailure
import com.processout.sdk.core.onSuccess
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class CardsRepositoryTests {

    @Rule
    @JvmField
    val setupRule = SetupRule()

    private lateinit var cards: POCardsRepository

    @Before
    fun setUp() {
        cards = ProcessOut.instance.cards
    }

    @Test
    fun tokenize(): Unit = runBlocking {
        val request = POCardTokenizationRequest(
            name = "John Doe",
            number = "4242424242424242",
            expMonth = 10,
            expYear = 2030,
            cvc = "123"
        )
        cards.tokenize(request).assertFailure()
    }

    @Test
    fun updateCard(): Unit = runBlocking {
        val tokenizationRequest = POCardTokenizationRequest(
            name = "John Doe",
            number = "4242424242424242",
            expMonth = 10,
            expYear = 2030,
            cvc = "123"
        )
        cards.tokenize(tokenizationRequest).let { result ->
            result.assertFailure()
            result.onSuccess { card ->
                val cardUpdateRequest = POCardUpdateRequest(
                    cardId = card.id,
                    cvc = "321"
                )
                cards.updateCard(cardUpdateRequest).assertFailure()
            }
        }
    }

    @Test
    fun updateCVC(): Unit = runBlocking {
        val tokenizationRequest = POCardTokenizationRequest(
            name = "John Doe",
            number = "4242424242424242",
            expMonth = 10,
            expYear = 2030,
            cvc = "123"
        )
        cards.tokenize(tokenizationRequest).let { result ->
            result.assertFailure()
            result.onSuccess { card ->
                val cvcUpdateRequest = POCardUpdateCVCRequest(cvc = "321")
                cards.updateCVC(card.id, cvcUpdateRequest).assertFailure()
            }
        }
    }

    @Test
    fun fetchIssuerInformation(): Unit = runBlocking {
        cards.fetchIssuerInformation(iin = "40000000").let { result ->
            result.assertFailure()
            result.onSuccess {
                assert(it.scheme == "visa")
            }
        }
    }
}
