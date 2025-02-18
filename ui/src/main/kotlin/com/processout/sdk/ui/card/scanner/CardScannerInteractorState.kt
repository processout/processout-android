package com.processout.sdk.ui.card.scanner

internal data class CardScannerInteractorState(
    val card: POScannedCard?
) {

    object ActionId {
        const val CANCEL = "cancel"
    }
}
