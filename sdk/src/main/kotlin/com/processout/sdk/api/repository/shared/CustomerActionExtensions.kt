package com.processout.sdk.api.repository.shared

import android.net.Uri
import android.util.Base64
import com.processout.sdk.api.model.request.*
import com.squareup.moshi.Moshi

internal fun POCustomerAction?.parseResponse(moshi: Moshi) =
    this?.let {
        when (it.type) {
            CustomerActionType.FINGERPRINT_MOBILE.value -> {
                val fingerprintData = moshi.adapter(PO3DS2Configuration::class.java)
                    .fromJson(String(Base64.decode(it.value, Base64.NO_WRAP)))
                fingerprintData?.let {
                    POCustomerActionResponse.AuthenticationFingerprintData(fingerprintData)
                }
            }
            CustomerActionType.CHALLENGE_MOBILE.value -> {
                val challengeData = moshi.adapter(POAuthenticationChallengeData::class.java)
                    .fromJson(String(Base64.decode(it.value, Base64.NO_WRAP)))
                challengeData?.let {
                    POCustomerActionResponse.AuthenticationChallengeData(challengeData)
                }
            }
            CustomerActionType.URL.value -> {
                POCustomerActionResponse.UriData(Uri.parse(it.value))
            }
            CustomerActionType.REDIRECT.value -> {
                POCustomerActionResponse.UriData(Uri.parse(it.value))
            }
            CustomerActionType.FINGERPRINT.value -> {
                POCustomerActionResponse.UriData(Uri.parse(it.value))
            }
            else -> null
        }
    }
