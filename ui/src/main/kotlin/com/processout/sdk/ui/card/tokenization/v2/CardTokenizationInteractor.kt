package com.processout.sdk.ui.card.tokenization.v2

import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.api.dispatcher.card.tokenization.PODefaultCardTokenizationEventDispatcher
import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.event.POCardTokenizationEvent.*
import com.processout.sdk.api.model.request.POCardTokenizationPreferredSchemeRequest
import com.processout.sdk.api.model.request.POCardTokenizationShouldContinueRequest
import com.processout.sdk.api.model.response.POCardIssuerInformation
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.getOrNull
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.onFailure
import com.processout.sdk.ui.base.BaseInteractor
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration
import com.processout.sdk.ui.card.tokenization.v2.CardTokenizationCompletion.Awaiting
import com.processout.sdk.ui.card.tokenization.v2.CardTokenizationEvent.*
import com.processout.sdk.ui.card.tokenization.v2.CardTokenizationInteractorState.*
import com.processout.sdk.ui.shared.provider.CardSchemeProvider
import com.processout.sdk.ui.shared.provider.address.AddressSpecificationProvider
import kotlinx.coroutines.Job
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

    private var latestPreferredSchemeRequest: POCardTokenizationPreferredSchemeRequest? = null
    private var latestShouldContinueRequest: POCardTokenizationShouldContinueRequest? = null

    private var issuerInformationJob: Job? = null

    init {
        POLogger.info("Starting card tokenization.")
        dispatch(WillStart)
        collectPreferredScheme()
        POLogger.info("Card tokenization is started: waiting for user input.")
        dispatch(DidStart)
    }

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
        var previousValue = TextFieldValue()
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
                        errorMessage = null
                    )
                }
            }
            when (id) {
                CardFieldId.NUMBER -> updateIssuerInformation(
                    cardNumber = value.text,
                    previousCardNumber = previousValue.text
                )
            }
        }
    }

    private fun updateFieldFocus(id: String, isFocused: Boolean) {
        if (isFocused) {
            _state.update { it.copy(focusedFieldId = id) }
        }
    }

    private fun updateIssuerInformation(cardNumber: String, previousCardNumber: String) {
        val iin = iin(cardNumber)
        if (iin == iin(previousCardNumber)) {
            return
        }
        updateState(
            issuerInformation = localIssuerInformation(iin),
            preferredScheme = null
        )
        if (iin.length == IIN_LENGTH) {
            updateIssuerInformation(iin)
        }
    }

    private fun iin(cardNumber: String) = cardNumber.take(IIN_LENGTH)

    private fun localIssuerInformation(iin: String) =
        cardSchemeProvider.scheme(iin)?.let { scheme ->
            POCardIssuerInformation(scheme = scheme)
        }

    private fun updateIssuerInformation(iin: String) {
        issuerInformationJob?.cancel()
        issuerInformationJob = interactorScope.launch {
            fetchIssuerInformation(iin)?.let { issuerInformation ->
                if (eventDispatcher.subscribedForPreferredSchemeRequest()) {
                    requestPreferredScheme(issuerInformation)
                } else {
                    updateState(
                        issuerInformation = issuerInformation,
                        preferredScheme = null
                    )
                }
            }
        }
    }

    private suspend fun fetchIssuerInformation(iin: String) =
        cardsRepository.fetchIssuerInformation(iin)
            .onFailure {
                POLogger.info(
                    message = "Failed to fetch issuer information: %s", it,
                    attributes = mapOf(LOG_ATTRIBUTE_IIN to iin)
                )
            }.getOrNull()

    private suspend fun requestPreferredScheme(issuerInformation: POCardIssuerInformation) {
        val request = POCardTokenizationPreferredSchemeRequest(issuerInformation)
        latestPreferredSchemeRequest = request
        eventDispatcher.send(request)
        POLogger.info("Requested to choose preferred scheme by issuer information: %s", issuerInformation)
    }

    private fun collectPreferredScheme() {
        interactorScope.launch {
            eventDispatcher.preferredSchemeResponse.collect { response ->
                if (response.uuid == latestPreferredSchemeRequest?.uuid) {
                    latestPreferredSchemeRequest = null
                    updateState(
                        issuerInformation = response.issuerInformation,
                        preferredScheme = response.preferredScheme
                    )
                }
            }
        }
    }

    private fun updateState(
        issuerInformation: POCardIssuerInformation?,
        preferredScheme: String?
    ) {
        _state.update {
            it.copy(
                issuerInformation = issuerInformation,
                preferredScheme = preferredScheme
            )
        }
        POLogger.info("State updated: [issuerInformation=%s] [preferredScheme=%s]", issuerInformation, preferredScheme)
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
