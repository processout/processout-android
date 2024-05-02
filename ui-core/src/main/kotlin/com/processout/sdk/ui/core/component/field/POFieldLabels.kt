package com.processout.sdk.ui.core.component.field

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.style.POTextStyle

/** @suppress */
@ProcessOutInternalApi
object POFieldLabels {

    @Immutable
    data class Style(
        val title: POText.Style,
        val description: POText.Style
    )

    val default: Style
        @Composable get() = Style(
            title = POText.labelHeading,
            description = POText.errorLabel
        )

    @Composable
    fun custom(
        titleStyle: POTextStyle? = null,
        descriptionStyle: POTextStyle? = null
    ) = Style(
        title = titleStyle?.let {
            POText.custom(style = it)
        } ?: default.title,
        description = descriptionStyle?.let {
            POText.custom(style = it)
        } ?: default.description
    )
}
