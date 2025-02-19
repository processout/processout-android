package com.processout.sdk.ui.card.scanner

import com.processout.sdk.core.ProcessOutResult

internal sealed interface CardScannerEvent {
    data class CameraPermissionResult(val isGranted: Boolean) : CardScannerEvent
    data class Action(val id: String) : CardScannerEvent
    data class Dismiss(val failure: ProcessOutResult.Failure) : CardScannerEvent
}

internal sealed interface CardScannerSideEffect {
    data object CameraPermissionRequest : CardScannerSideEffect
}

internal sealed interface CardScannerCompletion {
    data object Awaiting : CardScannerCompletion
    data class Success(val card: POScannedCard) : CardScannerCompletion
    data class Failure(val failure: ProcessOutResult.Failure) : CardScannerCompletion
}
