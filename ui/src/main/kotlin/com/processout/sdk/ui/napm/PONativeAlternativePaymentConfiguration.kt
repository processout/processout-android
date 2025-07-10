package com.processout.sdk.ui.napm

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import com.processout.sdk.ui.core.shared.image.PODrawableImage
import com.processout.sdk.ui.core.style.*
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.*
import com.processout.sdk.ui.shared.configuration.POActionConfirmationConfiguration
import com.processout.sdk.ui.shared.configuration.POBarcodeConfiguration
import com.processout.sdk.ui.shared.configuration.POCancellationConfiguration
import kotlinx.parcelize.Parcelize

/**
 * Defines native alternative payment configuration.
 *
 * @param[flow] Payment flow configuration.
 * @param[title] Custom title.
 * @param[submitButton] Submit button configuration.
 * @param[cancelButton] Cancel button configuration. Use _null_ to hide, this is a default behaviour.
 * @param[cancellation] Specifies cancellation behaviour.
 * @param[paymentConfirmation] Specifies payment confirmation configuration.
 * @param[barcode] Specifies barcode configuration.
 * @param[inlineSingleSelectValuesLimit] Defines maximum number of options that will be
 * displayed inline for parameters where user should select single option (e.g. radio buttons).
 * Default value is _5_.
 * @param[skipSuccessScreen] Only applies when [PaymentConfirmationConfiguration.waitsConfirmation] is _true_.
 * @param[successMessage] Custom success message when payment is completed.
 * @param[style] Allows to customize the look and feel.
 */
@Parcelize
data class PONativeAlternativePaymentConfiguration(
    val flow: Flow,
    val title: String? = null,
    val submitButton: Button = Button(),
    val cancelButton: CancelButton? = null,
    val cancellation: POCancellationConfiguration = POCancellationConfiguration(),
    val paymentConfirmation: PaymentConfirmationConfiguration = PaymentConfirmationConfiguration(confirmButton = null),
    val barcode: POBarcodeConfiguration = POBarcodeConfiguration(saveButton = POBarcodeConfiguration.Button()),
    val inlineSingleSelectValuesLimit: Int = 5,
    val skipSuccessScreen: Boolean = false,
    val successMessage: String? = null,
    val style: Style? = null
) : Parcelable {

    // TODO(v2): remove
    internal val invoiceId: String
        get() = when (flow) {
            is Flow.Authorization -> flow.invoiceId
            is Flow.Tokenization -> String()
        }

    // TODO(v2): remove
    internal val gatewayConfigurationId: String
        get() = when (flow) {
            is Flow.Authorization -> flow.gatewayConfigurationId
            is Flow.Tokenization -> String()
        }

    /**
     * Native alternative payment flow configuration.
     */
    sealed class Flow : Parcelable {
        /**
         * Configuration for native alternative payment authorization.
         *
         * @param[invoiceId] Invoice identifier.
         * @param[gatewayConfigurationId] Gateway configuration identifier.
         * @param[customerTokenId] Optional customer token identifier that will be used for authorization.
         */
        @Parcelize
        data class Authorization(
            val invoiceId: String,
            val gatewayConfigurationId: String,
            val customerTokenId: String? = null
        ) : Flow()

        /**
         * Configuration for native alternative payment tokenization.
         *
         * @param[customerId] Customer identifier.
         * @param[customerTokenId] Customer token identifier.
         * @param[gatewayConfigurationId] Gateway configuration identifier.
         */
        @Parcelize
        data class Tokenization(
            val customerId: String,
            val customerTokenId: String,
            val gatewayConfigurationId: String
        ) : Flow()
    }

    /**
     * Defines native alternative payment configuration.
     *
     * @param[invoiceId] Invoice ID.
     * @param[gatewayConfigurationId] Gateway configuration ID.
     * @param[title] Custom title.
     * @param[submitButton] Submit button configuration.
     * @param[cancelButton] Cancel button configuration. Use _null_ to hide, this is a default behaviour.
     * @param[cancellation] Specifies cancellation behaviour.
     * @param[paymentConfirmation] Specifies payment confirmation configuration.
     * @param[barcode] Specifies barcode configuration.
     * @param[inlineSingleSelectValuesLimit] Defines maximum number of options that will be
     * displayed inline for parameters where user should select single option (e.g. radio buttons).
     * Default value is _5_.
     * @param[skipSuccessScreen] Only applies when [PaymentConfirmationConfiguration.waitsConfirmation] is _true_.
     * @param[successMessage] Custom success message when payment is completed.
     * @param[style] Allows to customize the look and feel.
     */
    @Deprecated(message = "Use alternative constructor.")
    constructor(
        invoiceId: String,
        gatewayConfigurationId: String,
        title: String? = null,
        submitButton: Button = Button(),
        cancelButton: CancelButton? = null,
        cancellation: POCancellationConfiguration = POCancellationConfiguration(),
        paymentConfirmation: PaymentConfirmationConfiguration = PaymentConfirmationConfiguration(confirmButton = null),
        barcode: POBarcodeConfiguration = POBarcodeConfiguration(saveButton = POBarcodeConfiguration.Button()),
        inlineSingleSelectValuesLimit: Int = 5,
        skipSuccessScreen: Boolean = false,
        successMessage: String? = null,
        style: Style? = null
    ) : this(
        flow = Flow.Authorization(
            invoiceId = invoiceId,
            gatewayConfigurationId = gatewayConfigurationId
        ),
        title = title,
        submitButton = submitButton,
        cancelButton = cancelButton,
        cancellation = cancellation,
        paymentConfirmation = paymentConfirmation,
        barcode = barcode,
        inlineSingleSelectValuesLimit = inlineSingleSelectValuesLimit,
        skipSuccessScreen = skipSuccessScreen,
        successMessage = successMessage,
        style = style
    )

    /**
     * Defines native alternative payment configuration.
     *
     * @param[invoiceId] Invoice ID.
     * @param[gatewayConfigurationId] Gateway configuration ID.
     * @param[options] Allows to customize behaviour and pre-define the values.
     * @param[style] Allows to customize the look and feel.
     */
    @Deprecated(message = "Use alternative constructor.")
    constructor(
        invoiceId: String,
        gatewayConfigurationId: String,
        options: Options = Options(),
        style: Style? = null
    ) : this(
        invoiceId = invoiceId,
        gatewayConfigurationId = gatewayConfigurationId,
        title = options.title,
        submitButton = Button(text = options.primaryActionText),
        cancelButton = options.secondaryAction?.toCancelButton(),
        cancellation = with(options.cancellation) {
            POCancellationConfiguration(
                backPressed = backPressed,
                dragDown = dragDown,
                touchOutside = touchOutside
            )
        },
        paymentConfirmation = options.paymentConfirmation,
        barcode = options.barcode,
        inlineSingleSelectValuesLimit = options.inlineSingleSelectValuesLimit,
        skipSuccessScreen = options.skipSuccessScreen,
        successMessage = options.successMessage,
        style = style
    )

    /**
     * Allows to customize behaviour and pre-define the values.
     *
     * @param[title] Custom title.
     * @param[primaryActionText] Custom primary action text (e.g. "Pay").
     * @param[secondaryAction] Secondary action (e.g. "Cancel"). Use _null_ to hide, this is a default behaviour.
     * @param[cancellation] Specifies cancellation behaviour.
     * @param[paymentConfirmation] Specifies payment confirmation configuration.
     * @param[barcode] Specifies barcode configuration.
     * @param[inlineSingleSelectValuesLimit] Defines maximum number of options that will be
     * displayed inline for parameters where user should select single option (e.g. radio buttons).
     * Default value is _5_.
     * @param[skipSuccessScreen] Only applies when [PaymentConfirmationConfiguration.waitsConfirmation] is _true_.
     * @param[successMessage] Custom success message when payment is completed.
     */
    @Parcelize
    @Deprecated(message = "Use 'PONativeAlternativePaymentConfiguration' instead.")
    data class Options(
        val title: String? = null,
        val primaryActionText: String? = null,
        val secondaryAction: SecondaryAction? = null,
        val cancellation: CancellationConfiguration = CancellationConfiguration(),
        val paymentConfirmation: PaymentConfirmationConfiguration = PaymentConfirmationConfiguration(confirmButton = null),
        val barcode: POBarcodeConfiguration = POBarcodeConfiguration(saveButton = POBarcodeConfiguration.Button()),
        val inlineSingleSelectValuesLimit: Int = 5,
        val skipSuccessScreen: Boolean = false,
        val successMessage: String? = null
    ) : Parcelable

    /**
     * Button configuration.
     *
     * @param[text] Button text. Pass _null_ to use default text.
     * @param[icon] Button icon drawable resource. Pass _null_ to hide.
     */
    @Parcelize
    data class Button(
        val text: String? = null,
        val icon: PODrawableImage? = null
    ) : Parcelable

    /**
     * Cancel button configuration.
     *
     * @param[text] Button text. Pass _null_ to use default text.
     * @param[icon] Button icon drawable resource. Pass _null_ to hide.
     * @param[disabledForSeconds] Initially disables the button for the given amount of time in seconds.
     * By default user can interact with the button immediately when it's visible.
     * @param[confirmation] Specifies action confirmation configuration (e.g. dialog).
     * Use _null_ to disable, this is a default behaviour.
     */
    @Parcelize
    data class CancelButton(
        val text: String? = null,
        val icon: PODrawableImage? = null,
        val disabledForSeconds: Int = 0,
        val confirmation: POActionConfirmationConfiguration? = null
    ) : Parcelable

    /**
     * Confirmation action.
     *
     * @param[text] Action text. Pass _null_ to use default text.
     */
    @Parcelize
    @Deprecated(message = "Use 'Button' instead.")
    data class ConfirmAction(
        val text: String? = null
    ) : Parcelable

    /**
     * Supported secondary actions.
     */
    @Deprecated(message = "Use 'CancelButton' instead.")
    sealed class SecondaryAction : Parcelable {
        /**
         * Action for cancellation.
         *
         * @param[text] Action text. Pass _null_ to use default text.
         * @param[disabledForSeconds] Initially disables the action for the given amount of time in seconds.
         * By default user can interact with the action immediately when it's visible.
         * @param[confirmation] Specifies action confirmation configuration (e.g. dialog).
         * Use _null_ to disable, this is a default behaviour.
         */
        @Parcelize
        @Deprecated(message = "Use 'CancelButton' instead.")
        data class Cancel(
            val text: String? = null,
            val disabledForSeconds: Int = 0,
            val confirmation: POActionConfirmationConfiguration? = null
        ) : SecondaryAction()
    }

    /**
     * Specifies cancellation behaviour.
     *
     * @param[backPressed] Cancel on back button press or back gesture. Default value is _true_.
     * @param[dragDown] Cancel when bottom sheet is dragged down out of the screen. Default value is _true_.
     * @param[touchOutside] Cancel on touch of the outside dimmed area of the bottom sheet. Default value is _true_.
     */
    @Parcelize
    @Deprecated(message = "Use 'POCancellationConfiguration' instead.")
    data class CancellationConfiguration(
        val backPressed: Boolean = true,
        val dragDown: Boolean = true,
        val touchOutside: Boolean = true
    ) : Parcelable

    /**
     * Specifies payment confirmation configuration.
     *
     * @param[waitsConfirmation] Specifies whether flow should wait for payment confirmation from PSP
     * or will complete right after all user input is submitted. Default value is _true_.
     * @param[timeoutSeconds] Amount of time (in seconds) to wait for final payment confirmation.
     * Default value is 3 minutes, while maximum value is 15 minutes.
     * @param[showProgressIndicatorAfterSeconds] Show progress indicator during payment confirmation after provided delay (in seconds).
     * Use _null_ to hide, this is a default behaviour.
     * @param[hideGatewayDetails] Specifies whether gateway information (such as name/logo) should be hidden during payment confirmation
     * even when specific payment provider details are not available. Default value is _false_.
     * @param[confirmButton] Confirm button configuration.
     * @param[cancelButton] Cancel button configuration.
     */
    @Parcelize
    data class PaymentConfirmationConfiguration(
        val waitsConfirmation: Boolean = true, // TODO(v2): deprecate and update KDoc for [skipSuccessScreen]
        @IntRange(from = 0, to = 15 * 60)
        val timeoutSeconds: Int = DEFAULT_TIMEOUT_SECONDS,
        val showProgressIndicatorAfterSeconds: Int? = null,
        val hideGatewayDetails: Boolean = false,
        val confirmButton: Button? = null,
        val cancelButton: CancelButton? = null
    ) : Parcelable {

        companion object {
            const val DEFAULT_TIMEOUT_SECONDS = 3 * 60
            const val MAX_TIMEOUT_SECONDS = 15 * 60
        }

        /**
         * Specifies payment confirmation configuration.
         *
         * @param[waitsConfirmation] Specifies whether flow should wait for payment confirmation from PSP
         * or will complete right after all user input is submitted. Default value is _true_.
         * @param[timeoutSeconds] Amount of time (in seconds) to wait for final payment confirmation.
         * Default value is 3 minutes, while maximum value is 15 minutes.
         * @param[showProgressIndicatorAfterSeconds] Show progress indicator during payment confirmation after provided delay (in seconds).
         * Use _null_ to hide, this is a default behaviour.
         * @param[hideGatewayDetails] Specifies whether gateway information (such as name/logo) should be hidden during payment confirmation
         * even when specific payment provider details are not available. Default value is _false_.
         * @param[primaryAction] Optional primary action for payment confirmation.
         * To hide action use _null_, this is a default behaviour.
         * @param[secondaryAction] Secondary action (e.g. "Cancel") that could be optionally presented to user during payment confirmation stage.
         * Use _null_ to hide, this is a default behaviour.
         */
        @Deprecated(message = "Use alternative constructor.")
        constructor(
            waitsConfirmation: Boolean = true,
            @IntRange(from = 0, to = 15 * 60)
            timeoutSeconds: Int = DEFAULT_TIMEOUT_SECONDS,
            showProgressIndicatorAfterSeconds: Int? = null,
            hideGatewayDetails: Boolean = false,
            primaryAction: ConfirmAction? = null,
            secondaryAction: SecondaryAction? = null
        ) : this(
            waitsConfirmation = waitsConfirmation,
            timeoutSeconds = timeoutSeconds,
            showProgressIndicatorAfterSeconds = showProgressIndicatorAfterSeconds,
            hideGatewayDetails = hideGatewayDetails,
            confirmButton = primaryAction?.let { Button(text = it.text) },
            cancelButton = secondaryAction?.toCancelButton()
        )
    }

    /**
     * Allows to customize the look and feel.
     *
     * @param[title] Title style.
     * @param[label] Field label style.
     * @param[field] Field style.
     * @param[codeField] Code field style.
     * @param[radioField] Radio field style.
     * @param[checkbox] Checkbox style.
     * @param[dropdownMenu] Dropdown menu style.
     * @param[actionsContainer] Style of action buttons and their container.
     * @param[dialog] Dialog style.
     * @param[background] Background style.
     * @param[message] Message style.
     * @param[errorMessage] Error message style.
     * @param[errorMessageBox] Error message box style.
     * @param[successMessage] Success message style.
     * @param[successImageResId] Success image drawable resource ID.
     * @param[progressIndicatorColorResId] Color resource ID for progress indicator.
     * @param[controlsTintColorResId] Color resource ID for tint that applies to generic components (e.g. selectable text).
     * @param[dividerColorResId] Color resource ID for title divider.
     * @param[dragHandleColorResId] Color resource ID for bottom sheet drag handle.
     */
    @Parcelize
    data class Style(
        val title: POTextStyle? = null,
        val label: POTextStyle? = null, // TODO(v2): remove, not used
        val field: POFieldStyle? = null,
        val codeField: POFieldStyle? = null,
        val radioField: PORadioFieldStyle? = null,
        val checkbox: POCheckboxStyle? = null,
        val dropdownMenu: PODropdownMenuStyle? = null,
        val dialog: PODialogStyle? = null,
        val stepper: POStepperStyle? = null,
        val actionsContainer: POActionsContainerStyle? = null,
        val background: POBackgroundStyle? = null,
        val message: POTextStyle? = null,
        val errorMessage: POTextStyle? = null, // TODO(v2): remove, not used
        val errorMessageBox: POMessageBoxStyle? = null,
        val successMessage: POTextStyle? = null,
        @DrawableRes
        val successImageResId: Int? = null,
        @ColorRes
        val progressIndicatorColorResId: Int? = null,
        @ColorRes
        val controlsTintColorResId: Int? = null,
        @ColorRes
        val dividerColorResId: Int? = null,
        @ColorRes
        val dragHandleColorResId: Int? = null
    ) : Parcelable {

        /**
         * Allows to customize the look and feel.
         *
         * @param[title] Title style.
         * @param[label] Field label style.
         * @param[field] Field style.
         * @param[codeField] Code field style.
         * @param[radioButton] Radio button style. __Deprecated__: not used.
         * @param[dropdownMenu] Dropdown menu style.
         * @param[actionsContainer] Style of action buttons and their container.
         * @param[dialog] Dialog style.
         * @param[background] Background style.
         * @param[message] Message style.
         * @param[errorMessage] Error message style.
         * @param[successMessage] Success message style.
         * @param[successImageResId] Success image drawable resource ID.
         * @param[progressIndicatorColorResId] Color resource ID for progress indicator.
         * @param[controlsTintColorResId] Color resource ID for tint that applies to generic components (e.g. selectable text).
         * @param[dividerColorResId] Color resource ID for title divider.
         * @param[dragHandleColorResId] Color resource ID for bottom sheet drag handle.
         */
        @Deprecated(message = "Use alternative constructor.")
        constructor(
            title: POTextStyle? = null,
            label: POTextStyle? = null,
            field: POFieldStyle? = null,
            codeField: POFieldStyle? = null,
            radioButton: PORadioButtonStyle? = null,
            dropdownMenu: PODropdownMenuStyle? = null,
            actionsContainer: POActionsContainerStyle? = null,
            dialog: PODialogStyle? = null,
            background: POBackgroundStyle? = null,
            message: POTextStyle? = null,
            errorMessage: POTextStyle? = null,
            successMessage: POTextStyle? = null,
            @DrawableRes
            successImageResId: Int? = null,
            @ColorRes
            progressIndicatorColorResId: Int? = null,
            @ColorRes
            controlsTintColorResId: Int? = null,
            @ColorRes
            dividerColorResId: Int? = null,
            @ColorRes
            dragHandleColorResId: Int? = null
        ) : this(
            title = title,
            label = label,
            field = field,
            codeField = codeField,
            radioField = null,
            checkbox = null,
            dropdownMenu = dropdownMenu,
            dialog = dialog,
            stepper = null,
            actionsContainer = actionsContainer,
            background = background,
            message = message,
            errorMessage = errorMessage,
            errorMessageBox = null,
            successMessage = successMessage,
            successImageResId = successImageResId,
            progressIndicatorColorResId = progressIndicatorColorResId,
            controlsTintColorResId = controlsTintColorResId,
            dividerColorResId = dividerColorResId,
            dragHandleColorResId = dragHandleColorResId
        )
    }
}

private fun SecondaryAction.toCancelButton(): CancelButton =
    when (this) {
        is SecondaryAction.Cancel -> CancelButton(
            text = text,
            disabledForSeconds = disabledForSeconds,
            confirmation = confirmation
        )
    }
