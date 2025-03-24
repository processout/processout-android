package com.processout.sdk.ui.card.scanner

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.R
import com.processout.sdk.ui.card.scanner.POCardScannerConfiguration.CancelButton
import com.processout.sdk.ui.card.scanner.recognition.CardExpirationDetector
import com.processout.sdk.ui.card.scanner.recognition.CardNumberDetector
import com.processout.sdk.ui.card.scanner.recognition.CardRecognitionSession
import com.processout.sdk.ui.card.scanner.recognition.CardholderNameDetector
import com.processout.sdk.ui.core.shared.image.PODrawableImage
import com.processout.sdk.ui.core.shared.image.POImageRenderingMode
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POActionState.Confirmation
import com.processout.sdk.ui.shared.extension.map

internal class CardScannerViewModel(
    private val app: Application,
    private val configuration: POCardScannerConfiguration,
    private val interactor: CardScannerInteractor
) : ViewModel() {

    class Factory(
        private val app: Application,
        private val configuration: POCardScannerConfiguration
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CardScannerViewModel(
                app = app,
                configuration = configuration,
                interactor = CardScannerInteractor(
                    cardRecognitionSession = CardRecognitionSession(
                        numberDetector = CardNumberDetector(),
                        expirationDetector = CardExpirationDetector(),
                        cardholderNameDetector = CardholderNameDetector(),
                        shouldScanExpiredCard = configuration.shouldScanExpiredCard
                    )
                )
            ) as T
    }

    val completion = interactor.completion

    val state = interactor.state.map(viewModelScope, ::map)

    val sideEffects = interactor.sideEffects

    init {
        addCloseable(interactor.interactorScope)
    }

    fun onEvent(event: CardScannerEvent) = interactor.onEvent(event)

    private fun map(state: CardScannerInteractorState) =
        with(configuration) {
            CardScannerViewModelState(
                title = title ?: app.getString(R.string.po_card_scanner_title),
                description = description ?: app.getString(R.string.po_card_scanner_description),
                currentCard = state.currentCard,
                torchAction = torchAction(state.isTorchEnabled),
                cancelAction = cancelButton?.toAction()
            )
        }

    private fun torchAction(isTorchEnabled: Boolean) =
        POActionState(
            id = "torch",
            text = String(),
            primary = false,
            checked = isTorchEnabled,
            icon = PODrawableImage(
                resId = if (isTorchEnabled)
                    com.processout.sdk.ui.R.drawable.po_icon_lightning_slash else
                    com.processout.sdk.ui.R.drawable.po_icon_lightning,
                renderingMode = POImageRenderingMode.ORIGINAL
            )
        )

    private fun CancelButton.toAction() =
        POActionState(
            id = "cancel",
            text = text ?: app.getString(R.string.po_card_scanner_button_cancel),
            primary = false,
            icon = icon,
            confirmation = confirmation?.run {
                Confirmation(
                    title = title ?: app.getString(R.string.po_cancel_confirmation_title),
                    message = message,
                    confirmActionText = confirmActionText
                        ?: app.getString(R.string.po_cancel_confirmation_confirm),
                    dismissActionText = dismissActionText
                        ?: app.getString(R.string.po_cancel_confirmation_dismiss)
                )
            }
        )
}
