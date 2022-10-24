package com.processout.example

import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.model.request.POAllGatewayConfigurationsRequest
import com.processout.sdk.api.repository.GatewayConfigurationsRepository
import com.processout.sdk.core.ProcessOutResult
import kotlinx.coroutines.runBlocking
import org.junit.BeforeClass
import org.junit.Test

class GatewayConfigurationsRepositoryRunner {

    companion object {
        @JvmStatic
        @BeforeClass
        fun configure() {
            ProcessOutApiConfiguration.configure()
        }
    }

    private val repository: GatewayConfigurationsRepository =
        ProcessOutApi.instance.gatewayConfigurationsRepository

    @Test
    fun fetch() = runBlocking {
        val request = POAllGatewayConfigurationsRequest(
            POAllGatewayConfigurationsRequest.Filter.NATIVE_ALTERNATIVE_PAYMENT_METHODS
        )
        repository.fetch(request).let { result ->
            println(result)
            if (result is ProcessOutResult.Failure) throw AssertionError()
        }
    }

    @Test
    fun find() = runBlocking {
        repository.find("gway_conf_kby9pyzlmwycjxipdowupf77xaxyaj4c").let { result ->
            println(result)
            if (result is ProcessOutResult.Failure) throw AssertionError()
        }
    }
}
