package com.processout.sdk.ui.core.component.field

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POText

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
            title = POText.label1,
            description = POText.errorLabel
        )
}
