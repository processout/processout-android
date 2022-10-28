package com.processout.example

import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.model.request.POAllGatewayConfigurationsRequest
import com.processout.sdk.api.model.request.POGatewayConfigurationRequest
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

    private val gatewayConfigurations = ProcessOutApi.instance.gatewayConfigurations

    @Test
    fun fetch() = runBlocking {
        val request = POAllGatewayConfigurationsRequest(
            POAllGatewayConfigurationsRequest.Filter.NATIVE_ALTERNATIVE_PAYMENT_METHODS
        )
        gatewayConfigurations.fetch(request).assertFailure()
    }

    @Test
    fun find() = runBlocking {
        val request = POGatewayConfigurationRequest(
            "gway_conf_vojw6s38v89xu2aweh9z9quc7yaim5g7.adyenblik",
            withGateway = true
        )
        gatewayConfigurations.find(request).assertFailure()
    }
}
