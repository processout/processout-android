package com.processout.sdk

import android.os.Build
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POAllGatewayConfigurationsRequest
import com.processout.sdk.api.model.request.POGatewayConfigurationRequest
import com.processout.sdk.api.repository.POGatewayConfigurationsRepository
import com.processout.sdk.configuration.PROCESSOUT_GATEWAY_CONFIGURATION_ID
import com.processout.sdk.configuration.TestApplication
import com.processout.sdk.configuration.TestSetupRule
import com.processout.sdk.configuration.assertFailure
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
class GatewayConfigurationsRepositoryTests {

    @Rule
    @JvmField
    val setupRule = TestSetupRule()

    private lateinit var gatewayConfigurations: POGatewayConfigurationsRepository

    @Before
    fun setUp() {
        gatewayConfigurations = ProcessOut.instance.gatewayConfigurations
    }

    @Test
    fun fetch(): Unit = runBlocking {
        val request = POAllGatewayConfigurationsRequest(
            POAllGatewayConfigurationsRequest.Filter.NATIVE_ALTERNATIVE_PAYMENT_METHODS
        )
        gatewayConfigurations.fetch(request).assertFailure()
    }

    @Test
    fun find(): Unit = runBlocking {
        val request = POGatewayConfigurationRequest(
            PROCESSOUT_GATEWAY_CONFIGURATION_ID,
            withGateway = true
        )
        gatewayConfigurations.find(request).assertFailure()
    }
}
