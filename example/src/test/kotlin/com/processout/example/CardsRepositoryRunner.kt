package com.processout.example

import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.model.request.POCardTokenizationRequest
import com.processout.sdk.api.model.request.POCardUpdateCVCRequest
import com.processout.sdk.core.handleSuccess
import kotlinx.coroutines.runBlocking
import org.junit.BeforeClass
import org.junit.Test

class CardsRepositoryRunner {

    companion object {
        @JvmStatic
        @BeforeClass
        fun configure() {
            ProcessOutApiConfiguration.configure()
        }
    }

    private val cards = ProcessOutApi.instance.cards

    @Test
    fun tokenize() = runBlocking {
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
    fun updateCVC() = runBlocking {
        val request = POCardTokenizationRequest(
            name = "John Doe",
            number = "4242424242424242",
            expMonth = 10,
            expYear = 2030,
            cvc = "123"
        )

        cards.tokenize(request).let { result ->
            result.assertFailure()
            result.handleSuccess { card ->
                val cvcUpdateRequest = POCardUpdateCVCRequest(cvc = "321")
                cards.updateCVC(card.id, cvcUpdateRequest).assertFailure()
            }
        }
    }
}
