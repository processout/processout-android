package com.processout.sdk.api.repository.shared

import android.net.Uri
import android.util.Base64
import com.processout.sdk.api.model.response.*
import com.squareup.moshi.Moshi

internal fun POCustomerAction?.parseResponse(moshi: Moshi) =
    this?.let {
        when (it.type) {
            CustomerActionType.FINGERPRINT_MOBILE.value ->
                moshi.adapter(PO3DS2Configuration::class.java)
                    .fromJson(String(Base64.decode(it.value, Base64.NO_WRAP)))
                    ?.let { configuration ->
                        PO3DSCustomerAction.FingerprintMobile(configuration)
                    }
            CustomerActionType.CHALLENGE_MOBILE.value ->
                moshi.adapter(PO3DS2Challenge::class.java)
                    .fromJson(String(Base64.decode(it.value, Base64.NO_WRAP)))
                    ?.let { challenge ->
                        PO3DSCustomerAction.ChallengeMobile(challenge)
                    }
            CustomerActionType.FINGERPRINT.value ->
                PO3DSCustomerAction.Fingerprint(Uri.parse(it.value))
            CustomerActionType.REDIRECT.value,
            CustomerActionType.URL.value ->
                PO3DSCustomerAction.Redirect(Uri.parse(it.value))
            else -> null
        }
    }
