package com.processout.sdk.ui.nativeapm

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Resources.NotFoundException
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.TypefaceCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.setPadding
import androidx.core.widget.TextViewCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.radiobutton.MaterialRadioButton
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.databinding.PoBottomSheetCaptureBinding
import com.processout.sdk.databinding.PoBottomSheetNativeApmBinding
import com.processout.sdk.ui.shared.style.POBorderStyle
import com.processout.sdk.ui.shared.style.POTextStyle
import com.processout.sdk.ui.shared.style.POTypography
import com.processout.sdk.ui.shared.style.StyleConstants
import com.processout.sdk.ui.shared.style.button.POButtonStateStyle
import com.processout.sdk.ui.shared.style.button.POButtonStyle
import com.processout.sdk.ui.shared.style.radio.PORadioButtonStyle
import com.processout.sdk.ui.shared.view.extension.dpToPx
import com.processout.sdk.ui.shared.view.extension.spToPx

@Suppress("DEPRECATION")
internal fun PoBottomSheetNativeApmBinding.applyStyle(
    style: PONativeAlternativePaymentMethodConfiguration.Style
) {
    (style.background?.normal ?: style.backgroundColor)?.let {
        root.setBackgroundColor(it)
    }
    style.progressIndicatorColor?.let { poCircularProgressIndicator.setIndicatorColor(it) }
    style.title?.let { poTitle.applyStyle(it) }
    style.primaryButton?.let { poPrimaryButton.applyStyle(it) }
    style.secondaryButton?.let { poSecondaryButton.applyStyle(it) }
}

internal fun PoBottomSheetCaptureBinding.applyStyle(
    style: PONativeAlternativePaymentMethodConfiguration.Style
) {
    style.progressIndicatorColor?.let {
        poCircularProgressIndicator.setIndicatorColor(it)
        poCaptureCircularProgressIndicator.setIndicatorColor(it)
    }
    style.successImageResId?.let { poSuccessImage.setImageResource(it) }
    style.message?.let { poMessage.applyStyle(it) }
    style.primaryButton?.let { poPrimaryButton.applyStyle(it) }
    style.secondaryButton?.let { poSecondaryButton.applyStyle(it) }
    style.controlsTintColor?.let {
        poMessage.applyControlsTintColor(it)
        poMessage.highlightColor = ColorUtils.setAlphaComponent(
            it, StyleConstants.HIGHLIGHT_COLOR_ALPHA
        )
    }
}

internal fun TextView.applyStyle(style: POTextStyle) {
    setTextColor(style.color)
    applyStyle(style.typography)
}

internal fun TextView.applyStyle(typography: POTypography) {
    typography.let {
        textSize = it.textSizeSp.toFloat()
        TextViewCompat.setLineHeight(this, it.lineHeightSp.spToPx(context))
        val customTypeface = it.fontResId?.let { fontResId ->
            try {
                ResourcesCompat.getFont(context, fontResId)
            } catch (_: NotFoundException) {
                null
            }
        }
        typeface = TypefaceCompat.create(
            context, customTypeface ?: typeface, it.weight.value, it.italic
        )
    }
}

internal fun MaterialButton.applyButtonStatesStyle(style: POButtonStyle) {
    ViewCompat.setBackgroundTintList(
        this, createButtonColorStateList(
            style.normal.backgroundColor,
            style.disabled.backgroundColor,
            style.highlighted.backgroundColor
        )
    )
    setTextColor(
        createButtonColorStateList(
            style.normal.text.color,
            style.disabled.text.color,
            style.highlighted.textColor
        )
    )
    strokeColor = createButtonColorStateList(
        style.normal.border.color,
        style.disabled.border.color,
        style.highlighted.borderColor
    )
}

internal fun createButtonColorStateList(
    @ColorInt
    enabledColor: Int,
    @ColorInt
    disabledColor: Int,
    @ColorInt
    pressedColor: Int
) = ColorStateList(
    arrayOf(
        intArrayOf(android.R.attr.state_pressed),
        intArrayOf(-android.R.attr.state_enabled),
        intArrayOf()
    ),
    intArrayOf(
        pressedColor,
        disabledColor,
        enabledColor
    )
)

internal fun MaterialButton.applyStyle(style: POButtonStateStyle) {
    applyStyle(style.text.typography)
    applyStyle(style.border)
    setPadding(style.paddingDp.dpToPx(context))
}

internal fun MaterialButton.applyStyle(style: POBorderStyle) {
    cornerRadius = style.radiusDp.dpToPx(context)
    strokeWidth = style.widthDp.dpToPx(context)
}

internal fun MaterialRadioButton.applyStatesStyle(
    style: PORadioButtonStyle?,
    defaultButtonTintList: ColorStateList
) {
    style?.let {
        buttonTintList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf()
            ),
            intArrayOf(
                it.selected.knobColor,
                it.normal.knobColor
            )
        )
        if (isChecked) applyStyle(it.selected.text)
        else applyStyle(it.normal.text)
    } ?: run { buttonTintList = defaultButtonTintList }
}

internal fun MaterialRadioButton.applyErrorStateStyle(
    style: PORadioButtonStyle?,
    defaultButtonTintList: ColorStateList
) {
    style?.let {
        with(it.error) {
            buttonTintList = ColorStateList.valueOf(knobColor)
            applyStyle(text)
        }
    } ?: run { buttonTintList = defaultButtonTintList }
}

internal fun TextView.applyControlsTintColor(@ColorInt tintColor: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        textCursorDrawable?.also { textCursorDrawable = it.tinted(tintColor) }
        textSelectHandle?.also { setTextSelectHandle(it.tinted(tintColor)) }
        textSelectHandleLeft?.also { setTextSelectHandleLeft(it.tinted(tintColor)) }
        textSelectHandleRight?.also { setTextSelectHandleRight(it.tinted(tintColor)) }
    } else {
        setTextCursorColorCompat(tintColor)
        setTextSelectHandleColorCompat(tintColor)
    }
}

private fun Drawable.tinted(@ColorInt color: Int) =
    mutate().also { DrawableCompat.setTint(DrawableCompat.wrap(it), color) }

@SuppressLint("PrivateApi")
private fun TextView.setTextCursorColorCompat(@ColorInt tintColor: Int) {
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

        val cursorRes = TextView::class.java.getAccessibleField("mCursorDrawableRes")
            ?.get(this) as? Int ?: return
        val tintedCursorDrawable = ContextCompat.getDrawable(context, cursorRes)
            ?.tinted(tintColor) ?: return

        val cursorField = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            editorClass.getAccessibleField("mDrawableForCursor") else null

        if (cursorField != null) {
            cursorField.set(editor, tintedCursorDrawable)
        } else {
            editorClass.getAccessibleField("mCursorDrawable")
                ?.set(editor, arrayOf(tintedCursorDrawable, tintedCursorDrawable))
            editorClass.getAccessibleField("mDrawableForCursor")
                ?.set(editor, arrayOf(tintedCursorDrawable, tintedCursorDrawable))
        }
    } catch (e: Throwable) {
        POLogger.info("Failed to set text cursor color. %s", e.message)
    }
}

@SuppressLint("PrivateApi")
private fun TextView.setTextSelectHandleColorCompat(@ColorInt tintColor: Int) {
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
                if (drawable != null) field.set(editor, drawable.tinted(tintColor))
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
