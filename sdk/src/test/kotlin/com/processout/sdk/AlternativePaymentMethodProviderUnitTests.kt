package com.processout.sdk

import android.net.Uri
import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.model.request.POAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.api.provider.AlternativePaymentMethodProvider
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
class AlternativePaymentMethodProviderUnitTests {

    @Rule
    @JvmField
    val setupRule = SetupRule()

    private lateinit var apmProvider: AlternativePaymentMethodProvider

    @Before
    fun setUp() {
        apmProvider = ProcessOutApi.instance.alternativePaymentMethods
    }

    @Test
    fun alternativePaymentMethodURL() = runBlocking {
        val request = POAlternativePaymentMethodRequest(
            invoiceId = "iv_test",
            gatewayConfigurationId = "gway_conf_test",
            additionalData = mapOf("field1" to "test", "field2" to "test2")
        )

        val expectedUrl = "https://checkout.processout.ninja/test-proj/" +
                "iv_test/redirect/gway_conf_test?additional_data%5Bfield1%5D=test&additional_data%5Bfield2%5D=test2"
        apmProvider.alternativePaymentMethodURL(request).let { result ->
            result.assertFailure()
            result.handleSuccess { response ->
                assert(response.toString() == expectedUrl)
            }
        }
    }

    @Test
    fun alternativePaymentMethodURLWithCustomerToken() = runBlocking {
        val request = POAlternativePaymentMethodRequest(
            invoiceId = "iv_test",
            gatewayConfigurationId = "gway_conf_test",
            customerId = "cust_test",
            tokenId = "tok_test"
        )

        val expectedUrl = "https://checkout.processout.ninja/test-proj/cust_test/tok_test/redirect/gway_conf_test"
        apmProvider.alternativePaymentMethodURL(request).let { result ->
            result.assertFailure()
            result.handleSuccess { response ->
                assert(response.toString() == expectedUrl)
            }
        }
    }

    @Test
    fun alternativePaymentMethodResponse() = runBlocking {
        val returnUrl = "https://processout.return?token=gway_req_test"

        apmProvider.alternativePaymentMethodResponse(Uri.parse(returnUrl)).let { result ->
            result.assertFailure()
            result.handleSuccess { response ->
                assert(response.gatewayToken != null)
                assert(response.returnType == POAlternativePaymentMethodResponse.APMReturnType.AUTHORIZATION)
            }
        }
    }

    @Test
    fun alternativePaymentMethodResponseWithCustomerToken() = runBlocking {
        val returnUrl = "https://processout.return?token=gway_req_test" +
                "&token_id=tok_test&customer_id=cust_test"

        apmProvider.alternativePaymentMethodResponse(Uri.parse(returnUrl)).let { result ->
            result.assertFailure()
            result.handleSuccess { response ->
                assert(response.gatewayToken != null)
                assert(response.customerId != null)
                assert(response.tokenId != null)
                assert(response.returnType == POAlternativePaymentMethodResponse.APMReturnType.CREATE_TOKEN)
            }
        }
    }
}

