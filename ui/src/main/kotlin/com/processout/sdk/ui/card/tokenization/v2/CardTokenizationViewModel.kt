package com.processout.sdk.ui.card.tokenization.v2

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.R
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.dispatcher.card.tokenization.PODefaultCardTokenizationEventDispatcher
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration
import com.processout.sdk.ui.card.tokenization.v2.CardTokenizationInteractorState.CardFieldId
import com.processout.sdk.ui.card.tokenization.v2.CardTokenizationInteractorState.Field
import com.processout.sdk.ui.card.tokenization.v2.CardTokenizationViewModelState.*
import com.processout.sdk.ui.core.filter.POInputFilter
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POFieldState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.shared.extension.map
import com.processout.sdk.ui.shared.filter.CardExpirationInputFilter
import com.processout.sdk.ui.shared.filter.CardNumberInputFilter
import com.processout.sdk.ui.shared.filter.CardSecurityCodeInputFilter
import com.processout.sdk.ui.shared.provider.CardSchemeProvider
import com.processout.sdk.ui.shared.provider.address.AddressSpecificationProvider
import com.processout.sdk.ui.shared.transformation.CardExpirationVisualTransformation
import com.processout.sdk.ui.shared.transformation.CardNumberVisualTransformation

internal class CardTokenizationViewModel(
    private val app: Application,
    private val configuration: POCardTokenizationConfiguration,
    private val interactor: CardTokenizationInteractor
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
                interactor = CardTokenizationInteractor(
                    configuration = configuration,
                    cardsRepository = ProcessOut.instance.cards,
                    cardSchemeProvider = CardSchemeProvider(),
                    addressSpecificationProvider = AddressSpecificationProvider(app),
                    eventDispatcher = PODefaultCardTokenizationEventDispatcher
                )
            ) as T
    }

    val completion = interactor.completion

    val state = interactor.state.map(viewModelScope, ::map)

    fun onEvent(event: CardTokenizationEvent) = interactor.onEvent(event)

    private fun map(state: CardTokenizationInteractorState) = with(configuration) {
        CardTokenizationViewModelState(
            title = title ?: app.getString(R.string.po_card_tokenization_title),
            sections = sections(state),
            focusedFieldId = state.focusedFieldId,
            primaryAction = POActionState(
                id = state.primaryActionId,
                text = primaryActionText ?: app.getString(R.string.po_card_tokenization_button_submit),
                primary = true
            ),
            secondaryAction = if (cancellation.secondaryAction) POActionState(
                id = state.secondaryActionId,
                text = secondaryActionText ?: app.getString(R.string.po_card_tokenization_button_cancel),
                primary = false
            ) else null,
            draggable = cancellation.dragDown
        )
    }

    private fun sections(state: CardTokenizationInteractorState): POImmutableList<Section> {
        return POImmutableList(listOf(cardInformationSection(state)))
    }

    private fun cardInformationSection(state: CardTokenizationInteractorState): Section {
        var cardNumberField: Item? = null
        var cardholderField: Item? = null
        val trackFields = mutableListOf<Item?>()
        state.cardFields.forEach { field ->
            when (field.id) {
                CardFieldId.NUMBER -> cardNumberField = field(
                    field = field,
                    placeholder = app.getString(R.string.po_card_tokenization_card_details_number_placeholder),
                    forceTextDirectionLtr = true,
                    inputFilter = CardNumberInputFilter(),
                    visualTransformation = CardNumberVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                CardFieldId.EXPIRATION -> trackFields.add(
                    field(
                        field = field,
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
                CardFieldId.CVC -> trackFields.add(
                    field(
                        field = field,
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
                CardFieldId.CARDHOLDER -> cardholderField = field(
                    field = field,
                    placeholder = app.getString(R.string.po_card_tokenization_card_details_cardholder_placeholder),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )
            }
        }
        val items = mutableListOf<Item>()
        cardNumberField?.let { items.add(it) }
        items.add(Item.Group(POImmutableList(trackFields.filterNotNull())))
        cardholderField?.let { items.add(it) }
        return Section(
            id = SectionId.CARD_INFORMATION,
            items = POImmutableList(items),
            errorMessage = state.errorMessage
        )
    }

    private fun field(
        field: Field,
        placeholder: String? = null,
        @DrawableRes iconResId: Int? = null,
        forceTextDirectionLtr: Boolean = false,
        inputFilter: POInputFilter? = null,
        visualTransformation: VisualTransformation = VisualTransformation.None,
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default
    ): Item? {
        if (!field.shouldCollect) {
            return null
        }
        if (!field.availableValues.isNullOrEmpty()) {
            return dropdownField(field)
        }
        return textField(
            field = field,
            placeholder = placeholder,
            iconResId = iconResId,
            forceTextDirectionLtr = forceTextDirectionLtr,
            inputFilter = inputFilter,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions
        )
    }

    private fun dropdownField(field: Field): Item? {
        return null
    }

    private fun textField(
        field: Field,
        placeholder: String? = null,
        @DrawableRes iconResId: Int? = null,
        forceTextDirectionLtr: Boolean = false,
        inputFilter: POInputFilter? = null,
        visualTransformation: VisualTransformation = VisualTransformation.None,
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default
    ): Item = Item.TextField(
        POFieldState(
            id = field.id,
            value = field.value,
            placeholder = placeholder,
            iconResId = iconResId,
            isError = !field.isValid,
            forceTextDirectionLtr = forceTextDirectionLtr,
            inputFilter = inputFilter,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions
        )
    )
}
