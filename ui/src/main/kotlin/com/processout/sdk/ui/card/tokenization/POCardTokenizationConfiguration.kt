package com.processout.sdk.ui.card.tokenization

import android.os.Parcelable
import androidx.annotation.ColorRes
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.style.POActionsContainerStyle
import com.processout.sdk.ui.core.style.PODropdownMenuStyle
import com.processout.sdk.ui.core.style.POFieldStyle
import com.processout.sdk.ui.core.style.POTextStyle
import com.processout.sdk.ui.shared.configuration.POCancellationConfiguration
import kotlinx.parcelize.Parcelize

/** @suppress */
@ProcessOutInternalApi
@Parcelize
data class POCardTokenizationConfiguration(
    val title: String? = null,
    val isCardholderNameFieldVisible: Boolean = true,
    val primaryActionText: String? = null,
    val secondaryActionText: String? = null,
    val cancellation: POCancellationConfiguration = POCancellationConfiguration(),
    val metadata: Map<String, String>? = null,
    val restore: RestoreConfiguration? = null,
    val style: Style? = null
) : Parcelable {

    @Parcelize
    data class RestoreConfiguration(
        val formData: POCardTokenizationFormData,
        val failureCode: POFailure.Code? = null
    ) : Parcelable

    @Parcelize
    data class Style(
        val title: POTextStyle? = null,
        val sectionTitle: POTextStyle? = null,
        val field: POFieldStyle? = null,
        val dropdownMenu: PODropdownMenuStyle? = null,
        val errorMessage: POTextStyle? = null,
        val actionsContainer: POActionsContainerStyle? = null,
        @ColorRes
        val backgroundColorResId: Int? = null,
        @ColorRes
        val dividerColorResId: Int? = null,
        @ColorRes
        val dragHandleColorResId: Int? = null
    ) : Parcelable
}
