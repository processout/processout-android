package com.processout.sdk.ui.core.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POButton.ProgressIndicatorSize.Medium
import com.processout.sdk.ui.core.shared.image.PODrawableImage
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POButtonToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    style: POButton.Style = POButton.ghostEqualPadding,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: PODrawableImage? = null,
    iconSize: Dp = dimensions.iconSizeMedium,
    progressIndicatorSize: POButton.ProgressIndicatorSize = Medium,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    POButton(
        text = text ?: String(),
        onClick = {},
        modifier = modifier,
        style = style,
        enabled = enabled,
        loading = loading,
        checked = checked,
        onCheckedChange = onCheckedChange,
        icon = icon,
        iconSize = iconSize,
        progressIndicatorSize = progressIndicatorSize,
        interactionSource = interactionSource
    )
}
