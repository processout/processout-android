package com.processout.sdk.ui.card.tokenization

import android.app.Application
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.R
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.response.POCardIssuerInformation
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.getOrNull
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.onFailure
import com.processout.sdk.ui.card.tokenization.CardTokenizationCompletion.Awaiting
import com.processout.sdk.ui.card.tokenization.CardTokenizationCompletion.Failure
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent.*
import com.processout.sdk.ui.card.tokenization.CardTokenizationSection.Item
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POMutableFieldState
import com.processout.sdk.ui.core.state.POStableList
import com.processout.sdk.ui.shared.filter.CardExpirationInputFilter
import com.processout.sdk.ui.shared.filter.CardNumberInputFilter
import com.processout.sdk.ui.shared.filter.CardSecurityCodeInputFilter
import com.processout.sdk.ui.shared.provider.CardSchemeProvider
import com.processout.sdk.ui.shared.provider.cardSchemeDrawableResId
import com.processout.sdk.ui.shared.transformation.CardExpirationVisualTransformation
import com.processout.sdk.ui.shared.transformation.CardNumberVisualTransformation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class CardTokenizationViewModel(
    private val app: Application,
    private val configuration: POCardTokenizationConfiguration,
    private val cardsRepository: POCardsRepository,
    private val cardSchemeProvider: CardSchemeProvider
) : ViewModel() {

    class Factory(
        private val app: Application,
        private val configuration: POCardTokenizationConfiguration
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CardTokenizationViewModel(
                app = app,
                configuration = configuration,
                cardsRepository = ProcessOut.instance.cards,
                cardSchemeProvider = CardSchemeProvider()
            ) as T
    }

    private companion object {
        const val LOG_ATTRIBUTE_IIN = "IIN"
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

    private object ActionId {
        const val SUBMIT = "submit"
        const val CANCEL = "cancel"
    }

    private data class FieldValue(
        val id: String,
        val value: String,
        val isValid: Boolean
    )

    private val _completion = MutableStateFlow<CardTokenizationCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    private val _sections = mutableStateListOf(cardInformationSection())
    val sections = POStableList(_sections)

    init {
        setLastFieldImeAction()
    }

    private fun initState() = with(configuration) {
        CardTokenizationState(
            title = title ?: app.getString(R.string.po_card_tokenization_title),
            primaryAction = POActionState(
                id = ActionId.SUBMIT,
                text = primaryActionText ?: app.getString(R.string.po_card_tokenization_button_submit),
                primary = true
            ),
            secondaryAction = if (cancellation.secondaryAction) POActionState(
                id = ActionId.CANCEL,
                text = secondaryActionText ?: app.getString(R.string.po_card_tokenization_button_cancel),
                primary = false
            ) else null,
            focusedFieldId = CardFieldId.NUMBER,
            draggable = cancellation.dragDown
        )
    }

    private fun cardInformationSection(): CardTokenizationSection {
        val items = mutableListOf(
            cardNumberField(),
            Item.Group(
                items = POStableList(
                    listOf(
                        cardExpirationField(),
                        cvcField()
                    )
                )
            )
        )
        if (configuration.isCardholderNameFieldVisible) {
            items.add(cardholderField())
        }
        return CardTokenizationSection(
            id = SectionId.CARD_INFORMATION,
            items = POStableList(items)
        )
    }

    private fun cardNumberField() = Item.TextField(
        POMutableFieldState(
            id = CardFieldId.NUMBER,
            placeholder = app.getString(R.string.po_card_tokenization_card_details_number_placeholder),
            forceTextDirectionLtr = true,
            inputFilter = CardNumberInputFilter(),
            visualTransformation = CardNumberVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )
    )

    private fun cardExpirationField() = Item.TextField(
        POMutableFieldState(
            id = CardFieldId.EXPIRATION,
            placeholder = app.getString(R.string.po_card_tokenization_card_details_expiration_placeholder),
            forceTextDirectionLtr = true,
            inputFilter = CardExpirationInputFilter(),
            visualTransformation = CardExpirationVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )
    )

    private fun cvcField() = Item.TextField(
        POMutableFieldState(
            id = CardFieldId.CVC,
            placeholder = app.getString(R.string.po_card_tokenization_card_details_cvc_placeholder),
            forceTextDirectionLtr = true,
            iconResId = com.processout.sdk.ui.R.drawable.po_card_back,
            inputFilter = CardSecurityCodeInputFilter(scheme = null),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            )
        )
    )

    private fun cardholderField() = Item.TextField(
        POMutableFieldState(
            id = CardFieldId.CARDHOLDER,
            placeholder = app.getString(R.string.po_card_tokenization_card_details_cardholder_placeholder),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                autoCorrect = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )
    )

    private fun setLastFieldImeAction() {
        lastField()?.apply {
            keyboardOptions = keyboardOptions.copy(imeAction = ImeAction.Done)
            keyboardActionId = ActionId.SUBMIT
        }
    }

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
        field(id)?.apply {
            this.value = value
            isError = false
        }
        if (id == CardFieldId.NUMBER) {
            updateIssuerInformation(cardNumber = value.text)
        }
    }

    private fun updateFieldFocus(id: String, isFocused: Boolean) {
        if (isFocused) {
            _state.update { it.copy(focusedFieldId = id) }
        }
    }

    private fun updateIssuerInformation(cardNumber: String) {
        when (cardNumber.length) {
            in 0..6 -> localIssuerInformation(cardNumber).let { issuerInformation ->
                _state.update { it.copy(issuerInformation = issuerInformation) }
                updateFields(issuerInformation)
            }
        }
        when (cardNumber.length) {
            6, 8 -> viewModelScope.launch {
                fetchIssuerInformation(cardNumber)?.let { issuerInformation ->
                    _state.update { it.copy(issuerInformation = issuerInformation) }
                    updateFields(issuerInformation)
                }
            }
        }
    }

    private fun localIssuerInformation(cardNumber: String) =
        cardSchemeProvider.scheme(cardNumber)?.let { scheme ->
            POCardIssuerInformation(scheme = scheme)
        }

    private suspend fun fetchIssuerInformation(iin: String) =
        cardsRepository.fetchIssuerInformation(iin)
            .onFailure {
                POLogger.info(
                    message = "Failed to fetch issuer information: %s", it,
                    attributes = mapOf(LOG_ATTRIBUTE_IIN to iin)
                )
            }.getOrNull()

    private fun updateFields(issuerInformation: POCardIssuerInformation?) {
        val scheme = issuerInformation?.coScheme ?: issuerInformation?.scheme
        field(CardFieldId.NUMBER)?.apply {
            iconResId = scheme?.let { cardSchemeDrawableResId(it) }
        }
        field(CardFieldId.CVC)?.apply {
            val inputFilter = CardSecurityCodeInputFilter(scheme = scheme)
            value = inputFilter.filter(value)
            this.inputFilter = inputFilter
        }
    }

    private fun submit() {
        // TODO
    }

    private fun cancel() {
        _completion.update {
            Failure(
                ProcessOutResult.Failure(
                    code = POFailure.Code.Cancelled,
                    message = "Cancelled by the user with secondary cancel action."
                ).also { POLogger.info("Cancelled: %s", it) }
            )
        }
    }

    private fun field(id: String): POMutableFieldState? {
        _sections.forEach { section ->
            section.items.elements.forEach { item ->
                field(id, item)
                    ?.let { return it }
            }
        }
        return null
    }

    private fun field(id: String, item: Item): POMutableFieldState? {
        when (item) {
            is Item.TextField ->
                if (item.state.id == id) {
                    return item.state
                }
            is Item.Group -> item.items.elements.forEach { groupItem ->
                field(id, groupItem)
                    ?.let { return it }
            }
        }
        return null
    }

    private fun lastField(): POMutableFieldState? {
        _sections.lastOrNull()?.let { section ->
            section.items.elements.lastOrNull()?.let { item ->
                return lastField(item)
            }
        }
        return null
    }

    private fun lastField(item: Item): POMutableFieldState? {
        when (item) {
            is Item.TextField -> return item.state
            is Item.Group -> item.items.elements.lastOrNull()?.let { groupItem ->
                return lastField(groupItem)
            }
        }
        return null
    }

    private fun SnapshotStateList<CardTokenizationSection>.fieldValues(): List<FieldValue> {
        val fieldValues = mutableListOf<FieldValue>()
        forEach { section ->
            section.items.elements.forEach { item ->
                fieldValues(item, fieldValues)
            }
        }
        return fieldValues
    }

    private fun fieldValues(item: Item, fieldValues: MutableList<FieldValue>) {
        when (item) {
            is Item.TextField -> with(item.state) {
                fieldValues.add(
                    FieldValue(
                        id = id,
                        value = value.text,
                        isValid = !isError
                    )
                )
            }
            is Item.Group -> item.items.elements.forEach { groupItem ->
                fieldValues(groupItem, fieldValues)
            }
        }
    }
}
