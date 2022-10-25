package com.processout.example

import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.model.request.POCardTokenizationRequest
import com.processout.sdk.api.model.request.POCardUpdateCVCRequest
import com.processout.sdk.api.model.response.POCardResponse
import com.processout.sdk.api.repository.CardsRepository
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.handleSuccess
import kotlinx.coroutines.runBlocking
import org.junit.BeforeClass
import org.junit.Test

class CardRepositoryRunner {
    companion object {
        @JvmStatic
        @BeforeClass
        fun configure() {
            ProcessOutApiConfiguration.configure()
        }
    }

    private val cardRepository: CardsRepository = ProcessOutApi.instance.cardsRepository

    @Test
    fun tokenize() = runBlocking {
        val request = POCardTokenizationRequest(
            name = "John Doe",
            number = "4242424242424242",
            expMonth = 10,
            expYear = 2030,
            cvc = "123"
        )

        cardRepository.tokenize(request).let { result ->
            println(result)
            if (result is ProcessOutResult.Failure) throw AssertionError()
        }
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

        cardRepository.tokenize(request).let { result ->
            println(result)
            if (result is ProcessOutResult.Failure) throw AssertionError()

            val cvcUpdateRequest = POCardUpdateCVCRequest(cvc = "321")
            result.handleSuccess { resp ->
                cardRepository.updateCVC(resp.id, cvcUpdateRequest).let { updateResult ->
                    println(updateResult)
                    if (updateResult is ProcessOutResult.Failure) throw AssertionError()
                }
            }
        }
    }
}