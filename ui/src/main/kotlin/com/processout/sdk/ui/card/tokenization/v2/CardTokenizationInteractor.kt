package com.processout.sdk.ui.card.tokenization.v2

import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.api.dispatcher.card.tokenization.PODefaultCardTokenizationEventDispatcher
import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.event.POCardTokenizationEvent.ParametersChanged
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.ui.base.BaseInteractor
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration
import com.processout.sdk.ui.card.tokenization.v2.CardTokenizationCompletion.Awaiting
import com.processout.sdk.ui.card.tokenization.v2.CardTokenizationEvent.*
import com.processout.sdk.ui.card.tokenization.v2.CardTokenizationInteractorState.*
import com.processout.sdk.ui.shared.provider.CardSchemeProvider
import com.processout.sdk.ui.shared.provider.address.AddressSpecificationProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class CardTokenizationInteractor(
    private val configuration: POCardTokenizationConfiguration,
    private val cardsRepository: POCardsRepository,
    private val cardSchemeProvider: CardSchemeProvider,
    private val addressSpecificationProvider: AddressSpecificationProvider,
    private val eventDispatcher: PODefaultCardTokenizationEventDispatcher
) : BaseInteractor() {

    private companion object {
        const val IIN_LENGTH = 6
        const val EXPIRATION_DATE_PART_LENGTH = 2
        const val LOG_ATTRIBUTE_IIN = "IIN"
        const val LOG_ATTRIBUTE_CARD_ID = "CardId"
    }

    private data class Expiration(
        val month: Int,
        val year: Int
    )

    private val _completion = MutableStateFlow<CardTokenizationCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow(init())
    val state = _state.asStateFlow()

    private fun init() = CardTokenizationInteractorState(
        cardFields = cardFields(),
        addressFields = emptyList(),
        focusedFieldId = CardFieldId.NUMBER,
        primaryActionId = ActionId.SUBMIT,
        secondaryActionId = ActionId.CANCEL
    )

    private fun cardFields(): List<Field> = mutableListOf(
        Field(id = CardFieldId.NUMBER),
        Field(id = CardFieldId.EXPIRATION),
        Field(id = CardFieldId.CVC),
        Field(
            id = CardFieldId.CARDHOLDER,
            shouldCollect = configuration.isCardholderNameFieldVisible
        )
    )

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
        var isTextChanged = false
        var previousValue: TextFieldValue? = null
        _state.update {
            it.copy(
                cardFields = it.cardFields.map { field ->
                    if (field.id == id) {
                        previousValue = field.value
                        isTextChanged = value.text != field.value.text
                        if (isTextChanged)
                            field.copy(value = value, isValid = true)
                        else field.copy(value = value)
                    } else {
                        field.copy()
                    }
                },
                addressFields = it.addressFields.map { field ->
                    if (field.id == id) {
                        previousValue = field.value
                        isTextChanged = value.text != field.value.text
                        if (isTextChanged)
                            field.copy(value = value, isValid = true)
                        else field.copy(value = value)
                    } else {
                        field.copy()
                    }
                }
            )
        }
        if (isTextChanged) {
            POLogger.debug(message = "Field is edited by the user: %s", id)
            dispatch(ParametersChanged)
            if (areAllFieldsValid()) {
                _state.update {
                    it.copy(
                        submitAllowed = true,
                        submitting = _state.value.submitting,
                        errorMessage = null
                    )
                }
            }
        }
    }

    private fun updateFieldFocus(id: String, isFocused: Boolean) {
        if (isFocused) {
            _state.update { it.copy(focusedFieldId = id) }
        }
    }

    private fun submit() {

    }

    private fun areAllFieldsValid() = with(_state.value) {
        cardFields.plus(addressFields).all { it.isValid }
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

    private fun dispatch(event: POCardTokenizationEvent) {
        interactorScope.launch {
            eventDispatcher.send(event)
        }
    }
}
