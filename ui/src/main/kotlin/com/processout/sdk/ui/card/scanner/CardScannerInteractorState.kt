package com.processout.sdk.ui.card.scanner

import com.processout.sdk.ui.card.scanner.recognition.POScannedCard

internal data class CardScannerInteractorState(
    val loading: Boolean,
    val isCameraPermissionGranted: Boolean,
    val isTorchEnabled: Boolean,
    val currentCard: POScannedCard?
)
