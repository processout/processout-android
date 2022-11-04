package com.processout.sdk.api.model.request;

import android.net.Uri
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass;

sealed class POCustomerActionResponse {
    data class UriData(val value: Uri) : POCustomerActionResponse()
    data class AuthenticationChallengeData(
        val value: POAuthenticationChallengeData
    ) : POCustomerActionResponse()
    data class AuthenticationFingerprintData(
        val value: POAuthenticationFingerprintData
    ) : POCustomerActionResponse()
}

data class POInvoiceAuthorizeSuccess(
    val customerAction: POCustomerActionResponse? = null
)

@JsonClass(generateAdapter = true)
internal data class POAuthorizationResponse(
    @Json(name = "customer_action")
    val customerAction: POCustomerAction? = null
)

@JsonClass(generateAdapter = true)
internal data class POCustomerAction(
    val type: String,
    val value: String
)

@JsonClass(generateAdapter = true)
data class POAuthenticationChallengeData(
    val acsTransID: String,
    val acsReferenceNumber: String,
    val acsSignedContent: String,
    val threeDSServerTransID: String
)

@JsonClass(generateAdapter = true)
data class POAuthenticationFingerprintData(
    val directoryServerID: String,
    val directoryServerPublicKey: String,
    val threeDSServerTransactionID: String,
    val messageVersion: String
)

enum class CustomerActionType(val value: String) {
    FINGERPRINT_MOBILE("fingerprint-mobile"),
    CHALLENGE_MOBILE("challenge-mobile"),
    URL("url"),
    REDIRECT("redirect"),
    FINGERPRINT("fingerprint")
}
