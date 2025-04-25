package com.processout.sdk.ui.card.tokenization

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
import com.processout.sdk.ui.card.tokenization.CardTokenizationInteractorState.*
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState.*
import com.processout.sdk.ui.core.shared.image.PODrawableImage
import com.processout.sdk.ui.core.shared.image.POImageRenderingMode
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POActionState.Confirmation
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.shared.extension.map
import com.processout.sdk.ui.shared.filter.CardExpirationInputFilter
import com.processout.sdk.ui.shared.filter.CardNumberInputFilter
import com.processout.sdk.ui.shared.filter.CardSecurityCodeInputFilter
import com.processout.sdk.ui.shared.filter.InputFilter
import com.processout.sdk.ui.shared.provider.CardSchemeProvider
import com.processout.sdk.ui.shared.provider.address.AddressSpecificationProvider
import com.processout.sdk.ui.shared.provider.address.stringResId
import com.processout.sdk.ui.shared.provider.cardSchemeDrawableResId
import com.processout.sdk.ui.shared.state.FieldState
import com.processout.sdk.ui.shared.transformation.CardExpirationVisualTransformation
import com.processout.sdk.ui.shared.transformation.CardNumberVisualTransformation

internal class CardTokenizationViewModel private constructor(
    private val app: Application,
    configuration: POCardTokenizationConfiguration,
    private val interactor: CardTokenizationInteractor
) : ViewModel() {

    class Factory(
        private val app: Application,
        private val configuration: POCardTokenizationConfiguration,
        private val eventDispatcher: PODefaultCardTokenizationEventDispatcher
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CardTokenizationViewModel(
                app = app,
                configuration = configuration,
                interactor = CardTokenizationInteractor(
                    app = app,
                    configuration = configuration,
                    cardsRepository = ProcessOut.instance.cards,
                    cardSchemeProvider = CardSchemeProvider(),
                    addressSpecificationProvider = AddressSpecificationProvider(app),
                    legacyEventDispatcher = eventDispatcher
                )
            ) as T
    }

    private data class KeyboardAction(
        val imeAction: ImeAction,
        val actionId: String?
    )

    var configuration = configuration
        private set

    val completion = interactor.completion

    val state = interactor.state.map(viewModelScope, ::map)

    val sideEffects = interactor.sideEffects

    init {
        addCloseable(interactor.interactorScope)
    }

    fun start() = interactor.start()

    fun start(configuration: POCardTokenizationConfiguration) {
        this.configuration = configuration
        interactor.start(configuration)
    }

    fun reset() = interactor.reset()

    fun onEvent(event: CardTokenizationEvent) = interactor.onEvent(event)

    private fun map(state: CardTokenizationInteractorState) = with(configuration) {
        CardTokenizationViewModelState(
            title = title ?: app.getString(R.string.po_card_tokenization_title),
            sections = sections(state),
            focusedFieldId = state.focusedFieldId,
            primaryAction = POActionState(
                id = state.primaryActionId,
                text = submitButton.text ?: app.getString(R.string.po_card_tokenization_button_submit),
                primary = true,
                enabled = state.submitAllowed,
                loading = state.submitting,
                icon = submitButton.icon
            ),
            secondaryAction = cancelButton?.let {
                POActionState(
                    id = state.secondaryActionId,
                    text = it.text ?: app.getString(R.string.po_card_tokenization_button_cancel),
                    primary = false,
                    enabled = !state.submitting,
                    icon = it.icon,
                    confirmation = it.confirmation?.run {
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
            },
            cardScannerAction = cardScanner?.scanButton?.let {
                POActionState(
                    id = state.cardScannerActionId,
                    text = it.text ?: app.getString(R.string.po_card_tokenization_button_scan),
                    primary = false,
                    icon = it.icon ?: PODrawableImage(
                        resId = com.processout.sdk.ui.R.drawable.po_icon_camera,
                        renderingMode = POImageRenderingMode.TEMPLATE
                    )
                )
            },
            draggable = bottomSheet.cancellation.dragDown || bottomSheet.expandable
        )
    }

    private fun sections(state: CardTokenizationInteractorState): POImmutableList<Section> {
        val lastFocusableFieldId = lastFocusableFieldId(state)
        val sections = listOf(
            cardInformationSection(state, lastFocusableFieldId),
            billingAddressSection(state, lastFocusableFieldId),
            futurePaymentsSection(state)
        )
        return POImmutableList(sections.filterNotNull())
    }

    private fun lastFocusableFieldId(state: CardTokenizationInteractorState): String? {
        with(state) {
            val allFields = cardFields + addressFields
            allFields.reversed().forEach { field ->
                if (field.shouldCollect && field.availableValues.isNullOrEmpty()) {
                    return field.id
                }
            }
        }
        return null
    }

    private fun cardInformationSection(
        state: CardTokenizationInteractorState,
        lastFocusableFieldId: String?
    ): Section {
        var cardNumberField: Item? = null
        var cardholderField: Item? = null
        val trackFields = mutableListOf<Item?>()
        state.cardFields.forEach { field ->
            val keyboardAction = keyboardAction(field.id, lastFocusableFieldId)
            when (field.id) {
                CardFieldId.NUMBER -> cardNumberField = field(
                    field = field,
                    placeholder = app.getString(R.string.po_card_tokenization_card_details_number_placeholder),
                    iconResId = cardSchemeDrawableResId(scheme = state.preferredSchemeField.value.text),
                    forceTextDirectionLtr = true,
                    inputFilter = CardNumberInputFilter(),
                    visualTransformation = CardNumberVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = keyboardAction.imeAction
                    ),
                    keyboardActionId = keyboardAction.actionId
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
                            imeAction = keyboardAction.imeAction
                        ),
                        keyboardActionId = keyboardAction.actionId
                    )
                )
                CardFieldId.CVC -> {
                    val inputFilter = CardSecurityCodeInputFilter(scheme = state.issuerInformation?.scheme)
                    trackFields.add(
                        field(
                            field = field.copy(value = inputFilter.filter(field.value)),
                            placeholder = app.getString(R.string.po_card_tokenization_card_details_cvc_placeholder),
                            forceTextDirectionLtr = true,
                            iconResId = com.processout.sdk.ui.R.drawable.po_card_back,
                            inputFilter = inputFilter,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.NumberPassword,
                                imeAction = keyboardAction.imeAction
                            ),
                            keyboardActionId = keyboardAction.actionId
                        )
                    )
                }
                CardFieldId.CARDHOLDER -> cardholderField = field(
                    field = field,
                    placeholder = app.getString(R.string.po_card_tokenization_card_details_cardholder_placeholder),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = keyboardAction.imeAction
                    ),
                    keyboardActionId = keyboardAction.actionId
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
            errorMessage = state.errorMessage,
            subsection = preferredSchemeSection(state)
        )
    }

    private fun billingAddressSection(
        state: CardTokenizationInteractorState,
        lastFocusableFieldId: String?
    ): Section? {
        if (state.addressFields.isEmpty()) {
            return null
        }
        val specification = state.addressSpecification
        val items = mutableListOf<Item>()
        state.addressFields.forEach { field ->
            val keyboardAction = keyboardAction(field.id, lastFocusableFieldId)
            when (field.id) {
                AddressFieldId.COUNTRY -> field(
                    field = field
                )?.also { items.add(it) }
                AddressFieldId.ADDRESS_1 -> field(
                    field = field,
                    placeholder = app.getString(R.string.po_card_tokenization_billing_address_street, 1),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text,
                        imeAction = keyboardAction.imeAction
                    ),
                    keyboardActionId = keyboardAction.actionId
                )?.also { items.add(it) }
                AddressFieldId.ADDRESS_2 -> field(
                    field = field,
                    placeholder = app.getString(R.string.po_card_tokenization_billing_address_street, 2),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text,
                        imeAction = keyboardAction.imeAction
                    ),
                    keyboardActionId = keyboardAction.actionId
                )?.also { items.add(it) }
                AddressFieldId.CITY -> field(
                    field = field,
                    placeholder = specification?.cityUnit?.let { app.getString(it.stringResId()) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text,
                        imeAction = keyboardAction.imeAction
                    ),
                    keyboardActionId = keyboardAction.actionId
                )?.also { items.add(it) }
                AddressFieldId.STATE -> field(
                    field = field,
                    placeholder = specification?.stateUnit?.let { app.getString(it.stringResId()) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text,
                        imeAction = keyboardAction.imeAction
                    ),
                    keyboardActionId = keyboardAction.actionId
                )?.also { items.add(it) }
                AddressFieldId.POSTAL_CODE -> field(
                    field = field,
                    placeholder = specification?.postcodeUnit?.let { app.getString(it.stringResId()) },
                    forceTextDirectionLtr = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = keyboardAction.imeAction
                    ),
                    keyboardActionId = keyboardAction.actionId
                )?.also { items.add(it) }
            }
        }
        if (items.isEmpty()) {
            return null
        }
        return Section(
            id = SectionId.BILLING_ADDRESS,
            title = app.getString(R.string.po_card_tokenization_billing_address_title),
            items = POImmutableList(items)
        )
    }

    private fun preferredSchemeSection(
        state: CardTokenizationInteractorState
    ): Section? {
        val preferredSchemeField = preferredSchemeField(state) ?: return null
        val title = configuration.preferredScheme?.title.let { title ->
            if (title == null)
                app.getString(R.string.po_card_tokenization_preferred_scheme)
            else title.ifBlank { null }
        }
        return Section(
            id = SectionId.PREFERRED_SCHEME,
            title = title,
            items = POImmutableList(listOf(preferredSchemeField))
        )
    }

    private fun preferredSchemeField(
        state: CardTokenizationInteractorState
    ): Item? {
        val field = state.preferredSchemeField
        if (!field.shouldCollect) {
            return null
        }
        val displayInline = configuration.preferredScheme?.displayInline ?: true
        return if (displayInline)
            radioField(field)
        else dropdownField(field)
    }

    private fun futurePaymentsSection(
        state: CardTokenizationInteractorState
    ): Section? {
        val saveCardField = state.saveCardField
        if (!saveCardField.shouldCollect) {
            return null
        }
        val items = listOf(
            checkboxField(
                field = saveCardField,
                title = app.getString(R.string.po_card_tokenization_save_card)
            )
        )
        return Section(
            id = SectionId.FUTURE_PAYMENTS,
            items = POImmutableList(items)
        )
    }

    private fun keyboardAction(fieldId: String, lastFocusableFieldId: String?) =
        if (fieldId == lastFocusableFieldId) {
            KeyboardAction(
                imeAction = ImeAction.Done,
                actionId = ActionId.SUBMIT
            )
        } else {
            KeyboardAction(
                imeAction = ImeAction.Next,
                actionId = null
            )
        }

    private fun field(
        field: Field,
        placeholder: String? = null,
        @DrawableRes iconResId: Int? = null,
        forceTextDirectionLtr: Boolean = false,
        inputFilter: InputFilter? = null,
        visualTransformation: VisualTransformation = VisualTransformation.None,
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
        keyboardActionId: String? = null
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
            keyboardOptions = keyboardOptions,
            keyboardActionId = keyboardActionId
        )
    }

    private fun textField(
        field: Field,
        placeholder: String? = null,
        @DrawableRes iconResId: Int? = null,
        forceTextDirectionLtr: Boolean = false,
        inputFilter: InputFilter? = null,
        visualTransformation: VisualTransformation = VisualTransformation.None,
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
        keyboardActionId: String? = null
    ): Item = Item.TextField(
        FieldState(
            id = field.id,
            value = field.value,
            placeholder = placeholder,
            iconResId = iconResId,
            isError = !field.isValid,
            forceTextDirectionLtr = forceTextDirectionLtr,
            inputFilter = inputFilter,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActionId = keyboardActionId
        )
    )

    private fun radioField(field: Field): Item =
        Item.RadioField(
            FieldState(
                id = field.id,
                value = field.value,
                availableValues = POImmutableList(field.availableValues ?: emptyList())
            )
        )

    private fun dropdownField(field: Field): Item =
        Item.DropdownField(
            FieldState(
                id = field.id,
                value = field.value,
                availableValues = POImmutableList(field.availableValues ?: emptyList())
            )
        )

    private fun checkboxField(
        field: Field,
        title: String? = null
    ): Item = Item.CheckboxField(
        FieldState(
            id = field.id,
            value = field.value,
            title = title,
            isError = !field.isValid
        )
    )
}
