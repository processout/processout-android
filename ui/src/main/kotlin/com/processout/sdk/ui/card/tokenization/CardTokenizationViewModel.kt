package com.processout.sdk.ui.card.tokenization

import android.app.Application
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.processout.sdk.R
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.ui.card.tokenization.CardTokenizationCompletion.Awaiting
import com.processout.sdk.ui.card.tokenization.CardTokenizationCompletion.Failure
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent.*
import com.processout.sdk.ui.card.tokenization.CardTokenizationState.Item
import com.processout.sdk.ui.card.tokenization.CardTokenizationState.Section
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POFieldState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.shared.filter.CardExpirationInputFilter
import com.processout.sdk.ui.shared.filter.CardNumberInputFilter
import com.processout.sdk.ui.shared.filter.CardSecurityCodeInputFilter
import com.processout.sdk.ui.shared.transformation.CardExpirationVisualTransformation
import com.processout.sdk.ui.shared.transformation.CardNumberVisualTransformation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class CardTokenizationViewModel(
    private val app: Application,
    private val configuration: POCardTokenizationConfiguration,
    private val cardsRepository: POCardsRepository
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
                cardsRepository = ProcessOut.instance.cards
            ) as T
    }

    private enum class CardField(val key: String) {
        Number("card-number"),
        Expiration("card-expiration"),
        CVC("card-cvc"),
        Cardholder("cardholder-name")
    }

    private val _completion = MutableStateFlow<CardTokenizationCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    private fun initState() = with(configuration) {
        CardTokenizationState(
            title = title ?: app.getString(R.string.po_card_tokenization_title),
            sections = POImmutableList(listOf(cardInformationSection())),
            primaryAction = POActionState(
                text = primaryActionText ?: app.getString(R.string.po_card_tokenization_button_submit),
                primary = true
            ),
            secondaryAction = if (cancellation.secondaryAction) POActionState(
                text = secondaryActionText ?: app.getString(R.string.po_card_tokenization_button_cancel),
                primary = false
            ) else null,
            draggable = cancellation.dragDown
        )
    }

    private fun cardInformationSection(): Section {
        val items = mutableListOf(
            cardNumberField(),
            Item.Group(
                items = POImmutableList(
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
        return Section(POImmutableList(items))
    }

    private fun cardNumberField() = Item.TextField(
        POFieldState(
            key = CardField.Number.key,
            placeholder = app.getString(R.string.po_card_tokenization_card_details_number_placeholder),
            inputFilter = CardNumberInputFilter(),
            visualTransformation = CardNumberVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            ),
            forceTextDirectionLtr = true
        )
    )

    private fun cardExpirationField() = Item.TextField(
        POFieldState(
            key = CardField.Expiration.key,
            placeholder = app.getString(R.string.po_card_tokenization_card_details_expiration_placeholder),
            inputFilter = CardExpirationInputFilter(),
            visualTransformation = CardExpirationVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            ),
            forceTextDirectionLtr = true
        )
    )

    private fun cvcField() = Item.TextField(
        POFieldState(
            key = CardField.CVC.key,
            placeholder = app.getString(R.string.po_card_tokenization_card_details_cvc_placeholder),
            iconResId = com.processout.sdk.ui.R.drawable.po_card_back,
            inputFilter = CardSecurityCodeInputFilter(scheme = null),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = if (configuration.isCardholderNameFieldVisible)
                    ImeAction.Next else ImeAction.Done
            ),
            forceTextDirectionLtr = true
        )
    )

    private fun cardholderField() = Item.TextField(
        POFieldState(
            key = CardField.Cardholder.key,
            placeholder = app.getString(R.string.po_card_tokenization_card_details_cardholder_placeholder),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                autoCorrect = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        )
    )

    fun onEvent(event: CardTokenizationEvent) = when (event) {
        is FieldValueChanged -> updateFieldValue(event.key, event.value)
        Submit -> submit()
        Cancel -> cancel()
        is Dismiss -> POLogger.info("Dismissed: %s", event.failure)
    }

    private fun updateFieldValue(key: String, value: TextFieldValue) {
        _state.update { state ->
            state.copy(
                sections = POImmutableList(
                    state.sections.elements.map { section ->
                        section.copy(
                            items = POImmutableList(
                                section.items.elements.map { item ->
                                    updateFieldValue(key, value, item)
                                }
                            )
                        )
                    }
                )
            )
        }
    }

    private fun updateFieldValue(
        key: String,
        value: TextFieldValue,
        item: Item
    ): Item = when (item) {
        is Item.TextField -> when (item.state.key) {
            key -> item.copy(
                state = item.state.copy(
                    value = value.copy()
                )
            )
            else -> item.copy()
        }
        is Item.Group -> item.copy(
            items = POImmutableList(
                item.items.elements.map { groupItem ->
                    updateFieldValue(key, value, groupItem)
                }
            )
        )
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
}
