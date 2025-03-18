package com.processout.sdk.ui.card.scanner

import com.processout.sdk.ui.card.scanner.recognition.POScannedCard

internal data class CardScannerInteractorState(
    val currentCard: POScannedCard?,
    val cancelActionId: String
) {

    object ActionId {
        const val CANCEL = "cancel"
    }
}
