package com.processout.sdk.ui.card.scanner

import android.app.Application
import com.processout.sdk.core.POFailure.Code.Cancelled
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.ui.base.BaseInteractor
import com.processout.sdk.ui.card.scanner.CardScannerCompletion.Awaiting
import com.processout.sdk.ui.card.scanner.CardScannerCompletion.Failure
import com.processout.sdk.ui.card.scanner.CardScannerEvent.Action
import com.processout.sdk.ui.card.scanner.CardScannerEvent.Dismiss
import com.processout.sdk.ui.card.scanner.CardScannerInteractorState.ActionId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class CardScannerInteractor(
    private val app: Application,
    private val configuration: POCardScannerConfiguration
) : BaseInteractor() {

    private val _completion = MutableStateFlow<CardScannerCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    private fun initState() = CardScannerInteractorState(
        card = null
    )

    fun onEvent(event: CardScannerEvent) {
        when (event) {
            is Action -> when (event.id) {
                ActionId.CANCEL -> cancel()
            }
            is Dismiss -> POLogger.info("Dismissed: %s", event.failure)
        }
    }

    private fun cancel() {
        _completion.update {
            Failure(
                ProcessOutResult.Failure(
                    code = Cancelled,
                    message = "Cancelled by the user with cancel action."
                ).also { POLogger.info("Cancelled: %s", it) }
            )
        }
    }
}
