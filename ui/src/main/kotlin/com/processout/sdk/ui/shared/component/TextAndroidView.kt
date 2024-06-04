@file:Suppress("MayBeConstant")

package com.processout.sdk.ui.shared.component

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.TypefaceCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.TextViewCompat
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.ui.core.R
import com.processout.sdk.ui.core.style.POTextStyle
import com.processout.sdk.ui.core.style.POTextType
import com.processout.sdk.ui.core.style.POTextType.Weight
import com.processout.sdk.ui.core.style.POTextType.Weight.*
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.shared.component.TextAndroidView.apply
import com.processout.sdk.ui.shared.extension.spToPx
import com.processout.sdk.ui.shared.view.extension.POTextViewExtensions.setMarkdown

@Composable
internal fun TextAndroidView(
    text: String,
    modifier: Modifier = Modifier,
    style: TextAndroidView.Style = TextAndroidView.default,
    gravity: Int = Gravity.START,
    selectable: Boolean = false,
    linksClickable: Boolean = false
) {
    AndroidView(
        factory = { context ->
            TextView(context).apply {
                this.gravity = gravity
                if (selectable) {
                    isEnabled = true
                    isFocusable = true
                    isFocusableInTouchMode = true
                    isClickable = true
                    isLongClickable = true
                    setTextIsSelectable(true)
                }
                if (linksClickable) {
                    movementMethod = LinkMovementMethod.getInstance()
                }
                apply(style)
                setMarkdown(
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
                type = with(typography.fixed.body) {
                    POTextType(
                        textSizeSp = fontSize.value.toInt(),
                        lineHeightSp = lineHeight.value.toInt(),
                        fontResId = R.font.work_sans_regular,
                        weight = NORMAL,
                        italic = false
                    )
                },
                color = colors.text.primary,
                controlsTintColor = colors.text.primary
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
        applyControlsTintColor(style.controlsTintColor.toArgb())
    }

    private fun TextView.apply(type: POTextType) = with(type) {
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
        includeFontPadding = false
    }

    private val Weight.value: Int
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

    private val HighlightColorAlpha = 95

    private fun TextView.applyControlsTintColor(@ColorInt color: Int) {
        highlightColor = ColorUtils.setAlphaComponent(color, HighlightColorAlpha)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            textSelectHandle?.also { setTextSelectHandle(it.tinted(color)) }
            textSelectHandleLeft?.also { setTextSelectHandleLeft(it.tinted(color)) }
            textSelectHandleRight?.also { setTextSelectHandleRight(it.tinted(color)) }
        } else {
            setTextSelectHandleColorCompat(color)
        }
    }

    private fun Drawable.tinted(@ColorInt color: Int) =
        mutate().also { DrawableCompat.setTint(DrawableCompat.wrap(it), color) }

    @SuppressLint("PrivateApi")
    private fun TextView.setTextSelectHandleColorCompat(@ColorInt color: Int) {
        try {
            val editorField = TextView::class.java.getAccessibleField("mEditor")
            val editor = editorField?.get(this) ?: this
            val editorClass: Class<*> = if (editorField != null) {
                runCatching {
                    Class.forName("android.widget.Editor")
                }.getOrNull() ?: editorField.javaClass
            } else {
                TextView::class.java
            }

            val handles = listOf(
                "mSelectHandleCenter" to "mTextSelectHandleRes",
                "mSelectHandleLeft" to "mTextSelectHandleLeftRes",
                "mSelectHandleRight" to "mTextSelectHandleRightRes"
            )

            for (i in handles.indices) {
                editorClass.getAccessibleField(handles[i].first)?.let { field ->
                    val drawable = field.get(editor) as? Drawable
                        ?: TextView::class.java.getAccessibleField(handles[i].second)?.getInt(this)
                            ?.let { ContextCompat.getDrawable(context, it) }
                    if (drawable != null) field.set(editor, drawable.tinted(color))
                }
            }
        } catch (e: Throwable) {
            POLogger.info("Failed to set text select handle color. %s", e.message)
        }
    }

    private fun Class<*>.getAccessibleField(name: String) =
        runCatching {
            getDeclaredField(name).apply { isAccessible = true }
        }.getOrNull()
}
