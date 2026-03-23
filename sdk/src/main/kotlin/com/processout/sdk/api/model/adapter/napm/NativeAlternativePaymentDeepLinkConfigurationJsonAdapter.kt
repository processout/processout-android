package com.processout.sdk.api.model.adapter.napm

import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentRedirect
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson

internal class NativeAlternativePaymentDeepLinkConfigurationJsonAdapter {

    @FromJson
    fun fromJson(configuration: DeepLinkConfiguration) =
        PONativeAlternativePaymentRedirect.DeepLinkConfiguration(
            packageNames = configuration.android.packageNames
        )

    @ToJson
    fun toJson(configuration: PONativeAlternativePaymentRedirect.DeepLinkConfiguration) =
        DeepLinkConfiguration(
            android = DeepLinkConfiguration.Android(
                packageNames = configuration.packageNames
            )
        )

    @JsonClass(generateAdapter = true)
    data class DeepLinkConfiguration(
        val android: Android
    ) {
        @JsonClass(generateAdapter = true)
        data class Android(
            @Json(name = "package_names")
            val packageNames: Set<String>
        )
    }
}
