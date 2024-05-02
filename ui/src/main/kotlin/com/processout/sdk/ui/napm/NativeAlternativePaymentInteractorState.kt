package com.processout.sdk.ui.napm

internal data class NativeAlternativePaymentInteractorState(
    val focusedFieldId: String?,
    val primaryActionId: String,
    val secondaryActionId: String
) {

    object ActionId {
        const val SUBMIT = "submit"
        const val CANCEL = "cancel"
    }
}
