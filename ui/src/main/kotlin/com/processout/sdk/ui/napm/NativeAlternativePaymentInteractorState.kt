package com.processout.sdk.ui.napm

internal data class NativeAlternativePaymentInteractorState(
    val focusedFieldId: String?
) {

    object ActionId {
        const val SUBMIT = "submit"
        const val CANCEL = "cancel"
    }
}
