package com.processout.sdk.ui.card.tokenization.v2

import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.api.dispatcher.card.tokenization.PODefaultCardTokenizationEventDispatcher
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration
import com.processout.sdk.ui.card.tokenization.v2.CardTokenizationCompletion.Awaiting
import com.processout.sdk.ui.card.tokenization.v2.CardTokenizationEvent.*
import com.processout.sdk.ui.shared.provider.CardSchemeProvider
import com.processout.sdk.ui.shared.provider.address.AddressSpecificationProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class CardTokenizationInteractor(
    private val configuration: POCardTokenizationConfiguration,
    private val cardsRepository: POCardsRepository,
    private val cardSchemeProvider: CardSchemeProvider,
    private val addressSpecificationProvider: AddressSpecificationProvider,
    private val eventDispatcher: PODefaultCardTokenizationEventDispatcher
) {

    private companion object {
        const val IIN_LENGTH = 6
        const val EXPIRATION_DATE_PART_LENGTH = 2
        const val LOG_ATTRIBUTE_IIN = "IIN"
        const val LOG_ATTRIBUTE_CARD_ID = "CardId"
    }

    private object SectionId {
        const val CARD_INFORMATION = "card-information"
        const val BILLING_ADDRESS = "billing-address"
    }

    private object CardFieldId {
        const val NUMBER = "card-number"
        const val EXPIRATION = "card-expiration"
        const val CVC = "card-cvc"
        const val CARDHOLDER = "cardholder-name"
    }

    private object AddressFieldId {
        const val COUNTRY = "country-code"
        const val ADDRESS_1 = "address-1"
        const val ADDRESS_2 = "address-2"
        const val CITY = "city"
        const val STATE = "state"
        const val POSTAL_CODE = "postal-code"
    }

    private object ActionId {
        const val SUBMIT = "submit"
        const val CANCEL = "cancel"
    }

    private data class Expiration(
        val month: Int,
        val year: Int
    )

    private val _completion = MutableStateFlow<CardTokenizationCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow(CardTokenizationInteractorState())
    val state = _state.asStateFlow()

    fun onEvent(event: CardTokenizationEvent) {
        when (event) {
            is FieldValueChanged -> updateFieldValue(event.id, event.value)
            is FieldFocusChanged -> updateFieldFocus(event.id, event.isFocused)
            is Action -> when (event.id) {
                ActionId.SUBMIT -> submit()
                ActionId.CANCEL -> cancel()
            }
            is Dismiss -> POLogger.info("Dismissed: %s", event.failure)
        }
    }

    private fun updateFieldValue(id: String, value: TextFieldValue) {

    }

    private fun updateFieldFocus(id: String, isFocused: Boolean) {

    }

    private fun submit() {

    }

    private fun cancel() {
        _completion.update {
            CardTokenizationCompletion.Failure(
                ProcessOutResult.Failure(
                    code = POFailure.Code.Cancelled,
                    message = "Cancelled by the user with secondary cancel action."
                ).also { POLogger.info("Cancelled: %s", it) }
            )
        }
    }
}
