package com.processout.sdk

import android.net.Uri
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.api.network.ApiConstants
import com.processout.sdk.api.service.POAlternativePaymentMethodsService
import com.processout.sdk.config.SetupRule
import com.processout.sdk.config.TestApplication
import com.processout.sdk.config.assertFailure
import com.processout.sdk.core.onSuccess
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class AlternativePaymentMethodsServiceTests {

    @Rule
    @JvmField
    val setupRule = SetupRule()

    private lateinit var apmService: POAlternativePaymentMethodsService

    @Before
    fun setUp() {
        apmService = ProcessOut.instance.alternativePaymentMethods
    }

    @Test
    fun alternativePaymentMethodURL() {
        val request = POAlternativePaymentMethodRequest(
            invoiceId = "iv_test",
            gatewayConfigurationId = "gway_conf_test",
            additionalData = mapOf("field1" to "test", "field2" to "test2")
        )

        val expectedUrl = "${ApiConstants.CHECKOUT_URL}/${BuildConfig.PROJECT_ID}/" +
                "iv_test/redirect/gway_conf_test?additional_data%5Bfield1%5D=test&additional_data%5Bfield2%5D=test2"

        apmService.alternativePaymentMethodUri(request).let { result ->
            result.assertFailure()
            result.onSuccess { response ->
                assert(response.toString() == expectedUrl)
            }
        }
    }

    @Test
    fun alternativePaymentMethodURLWithCustomerToken() {
        val request = POAlternativePaymentMethodRequest(
            invoiceId = "iv_test",
            gatewayConfigurationId = "gway_conf_test",
            customerId = "cust_test",
            tokenId = "tok_test"
        )

        val expectedUrl = "${ApiConstants.CHECKOUT_URL}/${BuildConfig.PROJECT_ID}/" +
                "cust_test/tok_test/redirect/gway_conf_test"

        apmService.alternativePaymentMethodUri(request).let { result ->
            result.assertFailure()
            result.onSuccess { response ->
                assert(response.toString() == expectedUrl)
            }
        }
    }

    @Test
    fun alternativePaymentMethodResponse() {
        val returnUrl = "https://processout.return?token=gway_req_test"

        apmService.alternativePaymentMethodResponse(Uri.parse(returnUrl)).let { result ->
            result.assertFailure()
            result.onSuccess { response ->
                assert(response.gatewayToken.isNotBlank())
                assert(response.returnType == POAlternativePaymentMethodResponse.APMReturnType.AUTHORIZATION)
            }
        }
    }

    @Test
    fun alternativePaymentMethodResponseWithoutGatewayToken() {
        val returnUrl = "https://processout.return"

        apmService.alternativePaymentMethodResponse(Uri.parse(returnUrl)).let { result ->
            result.assertFailure()
            result.onSuccess { response ->
                assert(response.gatewayToken.isEmpty())
                assert(response.returnType == POAlternativePaymentMethodResponse.APMReturnType.AUTHORIZATION)
            }
        }
    }

    @Test
    fun alternativePaymentMethodResponseWithCustomerToken() {
        val returnUrl = "https://processout.return?token=gway_req_test" +
                "&token_id=tok_test&customer_id=cust_test"

        apmService.alternativePaymentMethodResponse(Uri.parse(returnUrl)).let { result ->
            result.assertFailure()
            result.onSuccess { response ->
                assert(response.gatewayToken.isNotBlank())
                assert(response.customerId != null)
                assert(response.tokenId != null)
                assert(response.returnType == POAlternativePaymentMethodResponse.APMReturnType.CREATE_TOKEN)
            }
        }
    }
}
