package com.processout.sdk.ui.shared.component

import android.content.res.Resources
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.TypefaceCompat
import androidx.core.widget.TextViewCompat
import com.processout.sdk.ui.core.R
import com.processout.sdk.ui.core.style.POTextStyle
import com.processout.sdk.ui.core.style.POTextType
import com.processout.sdk.ui.core.style.POTextType.Weight
import com.processout.sdk.ui.core.style.POTextType.Weight.*
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.shared.component.TextAndroidView.apply
import com.processout.sdk.ui.shared.extension.spToPx
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
                apply(style)
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
                        weight = NORMAL,
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

    internal fun TextView.apply(style: Style) {
        apply(style.type)
        setTextColor(style.color.toArgb())
    }

    internal fun TextView.apply(type: POTextType) = with(type) {
        textSize = textSizeSp.toFloat()
        TextViewCompat.setLineHeight(this@apply, lineHeightSp.spToPx(context))
        val customTypeface = fontResId?.let { fontResId ->
            try {
                ResourcesCompat.getFont(context, fontResId)
            } catch (_: Resources.NotFoundException) {
                null
            }
        }
        typeface = TypefaceCompat.create(
            context, customTypeface ?: typeface, weight.value, italic
        )
    }

    internal val Weight.value: Int
        get() = when (this) {
            THIN -> 100
            EXTRA_LIGHT -> 200
            LIGHT -> 300
            NORMAL -> 400
            MEDIUM -> 500
            SEMI_BOLD -> 600
            BOLD -> 700
            EXTRA_BOLD -> 800
            BLACK -> 900
        }
}
