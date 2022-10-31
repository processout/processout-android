package com.processout.example

import com.processout.example.config.SetupRule
import com.processout.example.config.TestApplication
import com.processout.example.config.assertFailure
import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.model.request.POAllGatewayConfigurationsRequest
import com.processout.sdk.api.model.request.POGatewayConfigurationRequest
import com.processout.sdk.api.repository.GatewayConfigurationsRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class GatewayConfigurationsRepositoryRunner {

    @Rule
    @JvmField
    val setupRule = SetupRule()

    private lateinit var gatewayConfigurations: GatewayConfigurationsRepository

    @Before
    fun setUp() {
        gatewayConfigurations = ProcessOutApi.instance.gatewayConfigurations
    }

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
