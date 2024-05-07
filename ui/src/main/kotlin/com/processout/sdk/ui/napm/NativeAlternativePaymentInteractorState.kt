package com.processout.sdk.ui.napm

internal data class NativeAlternativePaymentInteractorState(
    val primaryActionId: String,
    val secondaryActionId: String,
    val loading: Boolean = false,
    val captured: Boolean = false
) {

    object ActionId {
        const val SUBMIT = "submit"
        const val CANCEL = "cancel"
    }
}
