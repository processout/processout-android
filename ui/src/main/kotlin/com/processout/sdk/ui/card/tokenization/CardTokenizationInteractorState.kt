package com.processout.sdk.ui.card.tokenization

import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.api.model.response.POCardIssuerInformation
import com.processout.sdk.ui.card.tokenization.delegate.POCardTokenizationEligibility
import com.processout.sdk.ui.card.tokenization.delegate.POCardTokenizationEligibility.Eligible
import com.processout.sdk.ui.core.state.POAvailableValue
import com.processout.sdk.ui.shared.provider.address.AddressSpecification

internal data class CardTokenizationInteractorState(
    val started: Boolean = false,
    val cardFields: List<Field>,
    val addressFields: List<Field>,
    val preferredSchemeField: Field,
    val saveCardField: Field,
    val focusedFieldId: String?,
    val pendingFocusedFieldId: String?,
    val primaryActionId: String,
    val secondaryActionId: String,
    val cardScannerActionId: String,
    val submitAllowed: Boolean = true,
    val submitting: Boolean = false,
    val errorMessage: String? = null,
    val addressSpecification: AddressSpecification? = null,
    val issuerInformation: POCardIssuerInformation? = null,
    val eligibility: POCardTokenizationEligibility = Eligible(),
    val tokenizedCard: POCard? = null
) {

    data class Field(
        val id: String,
        val value: TextFieldValue = TextFieldValue(),
        val availableValues: List<POAvailableValue>? = null,
        val isValid: Boolean = true,
        val shouldCollect: Boolean = true
    )

    object CardFieldId {
        const val NUMBER = "card-number"
        const val EXPIRATION = "card-expiration"
        const val CVC = "card-cvc"
        const val CARDHOLDER = "cardholder-name"
    }

    object AddressFieldId {
        const val COUNTRY = "country"
        const val ADDRESS_1 = "address-1"
        const val ADDRESS_2 = "address-2"
        const val CITY = "city"
        const val STATE = "state"
        const val POSTAL_CODE = "postal-code"
    }

    object FieldId {
        const val PREFERRED_SCHEME = "preferred-scheme"
        const val SAVE_CARD = "save-card"
    }

    object ActionId {
        const val SUBMIT = "submit"
        const val CANCEL = "cancel"
        const val CARD_SCANNER = "card-scanner"
    }
}
