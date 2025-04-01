package com.processout.sdk.ui.card.scanner

import com.processout.sdk.ui.card.scanner.recognition.POScannedCard

internal data class CardScannerInteractorState(
    val isCameraPermissionGranted: Boolean,
    val isTorchEnabled: Boolean,
    val currentCard: POScannedCard?
)
