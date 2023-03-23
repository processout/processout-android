package com.processout.sdk.api.service

import com.processout.sdk.api.model.response.PO3DSCustomerAction
import com.squareup.moshi.Moshi

internal class ThreeDSServiceImpl(moshi: Moshi) : ThreeDSService {

    override fun handle(
        action: PO3DSCustomerAction,
        threeDSHandler: PO3DSHandler,
        callback: (PO3DSResult<String>) -> Unit
    ) {
        TODO("Not yet implemented")
    }
}
