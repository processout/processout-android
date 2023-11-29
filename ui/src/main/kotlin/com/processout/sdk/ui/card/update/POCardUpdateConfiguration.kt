package com.processout.sdk.ui.card.update

import android.os.Parcelable
import androidx.annotation.ColorRes
import com.processout.sdk.ui.core.style.POActionsContainerStyle
import com.processout.sdk.ui.core.style.POTextStyle
import com.processout.sdk.ui.core.style.input.POInputStyle
import com.processout.sdk.ui.shared.configuration.POCancellationConfiguration
import kotlinx.parcelize.Parcelize

@Parcelize
data class POCardUpdateConfiguration(
    val cardId: String,
    val options: Options = Options(),
    val style: Style? = null
) : Parcelable {

    @Parcelize
    data class Options(
        val title: String? = null,
        val cardInformation: CardInformation? = null,
        val primaryActionText: String? = null,
        val secondaryActionText: String? = null,
        val cancellation: POCancellationConfiguration = POCancellationConfiguration()
    ) : Parcelable

    @Parcelize
    data class CardInformation(
        val maskedNumber: String? = null,
        val iin: String? = null,
        val scheme: String? = null,
        val preferredScheme: String? = null
    ) : Parcelable

    @Parcelize
    data class Style(
        val title: POTextStyle? = null,
        val input: POInputStyle? = null,
        val errorDescription: POTextStyle? = null,
        val actionsContainer: POActionsContainerStyle? = null,
        @ColorRes
        val backgroundColorResId: Int? = null,
        @ColorRes
        val dividerColorResId: Int? = null,
        @ColorRes
        val dragHandleColorResId: Int? = null
    ) : Parcelable
}
