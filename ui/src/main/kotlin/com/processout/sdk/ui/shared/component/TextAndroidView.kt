package com.processout.sdk.ui.shared.component

import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.viewinterop.AndroidView
import com.processout.sdk.ui.core.R
import com.processout.sdk.ui.core.style.POTextStyle
import com.processout.sdk.ui.core.style.POTextType
import com.processout.sdk.ui.core.style.POTextType.Weight
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.shared.view.extension.POTextViewExtensions

@Composable
internal fun TextAndroidView(
    text: String,
    modifier: Modifier = Modifier,
    style: TextAndroidView.Style = TextAndroidView.default,
    selectable: Boolean = false
) {
    AndroidView(
        factory = { context ->
            TextView(context).apply {
                setTextIsSelectable(selectable)
                POTextViewExtensions.setMarkdown(
                    textView = this,
                    markdown = text
                )
            }
        },
        modifier = modifier
    )
}

internal object TextAndroidView {

    @Immutable
    data class Style(
        val type: POTextType,
        val color: Color,
        val controlsTintColor: Color
    )

    val default: Style
        @Composable get() = with(ProcessOutTheme) {
            Style(
                type = with(typography.fixed.bodyCompact) {
                    POTextType(
                        textSizeSp = fontSize.value.toInt(),
                        lineHeightSp = lineHeight.value.toInt(),
                        fontResId = R.font.work_sans_regular,
                        weight = Weight.NORMAL,
                        italic = false
                    )
                },
                color = colors.text.primary,
                controlsTintColor = colors.action.primaryDefault
            )
        }

    @Composable
    fun custom(
        style: POTextStyle,
        controlsTintColor: Color
    ) = Style(
        type = style.type,
        color = colorResource(id = style.colorResId),
        controlsTintColor = controlsTintColor
    )
}
