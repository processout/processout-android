package com.processout.sdk.ui.nativeapm

import android.content.res.ColorStateList
import android.content.res.Resources.NotFoundException
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.TypefaceCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.button.MaterialButton
import com.processout.sdk.R
import com.processout.sdk.databinding.PoBottomSheetCaptureBinding
import com.processout.sdk.databinding.PoBottomSheetNativeApmBinding
import com.processout.sdk.ui.shared.style.POBorderStyle
import com.processout.sdk.ui.shared.style.POTextStyle
import com.processout.sdk.ui.shared.style.background.POBackgroundDecorationStateStyle
import com.processout.sdk.ui.shared.view.extensions.dpToPx
import com.processout.sdk.ui.shared.view.extensions.spToPx

internal fun PoBottomSheetNativeApmBinding.applyStyle(
    style: PONativeAlternativePaymentMethodConfiguration.Style
) {
    style.backgroundColor?.let { root.setBackgroundColor(it) }
    style.backgroundDecoration?.let { poLoading.poBackgroundDecoration.applyStyle(it.normal) }
    style.progressIndicatorColor?.let { poLoading.poCircularProgressIndicator.setIndicatorColor(it) }
    style.title?.let { poTitle.applyStyle(it) }
    style.submitButton?.let { poSubmitButton.applyStyle(it) }
}

internal fun PoBottomSheetCaptureBinding.applyStyle(
    style: PONativeAlternativePaymentMethodConfiguration.Style
) {
    style.backgroundDecoration?.let { poBackgroundDecoration.applyStyle(it.normal) }
    style.successImageResId?.let { poSuccessImage.setImageResource(it) }
    style.message?.let { poMessage.applyStyle(it) }
}

internal fun TextView.applyStyle(style: POTextStyle) {
    setTextColor(style.color)
    style.typography.let {
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

internal fun MaterialButton.applyStyle(style: POBorderStyle) {
    cornerRadius = style.radiusDp.dpToPx(context)
    strokeWidth = style.widthDp.dpToPx(context)
    strokeColor = ColorStateList.valueOf(style.color)
}

internal fun View.applyStyle(style: POBackgroundDecorationStateStyle) {
    when (style) {
        is POBackgroundDecorationStateStyle.Visible -> setBackgroundDecoration(
            style.primaryColor, style.secondaryColor
        )
        POBackgroundDecorationStateStyle.Hidden -> background = null
    }
}

internal fun View.setBackgroundDecoration(
    @ColorInt
    innerColor: Int,
    @ColorInt
    outerColor: Int
) {
    (ContextCompat.getDrawable(context, R.drawable.po_background_decoration) as LayerDrawable).apply {
        mutate()
        (findDrawableByLayerId(R.id.po_background_layer_inner) as GradientDrawable).apply {
            mutate()
            setColor(innerColor)
        }
        (findDrawableByLayerId(R.id.po_background_layer_outer) as GradientDrawable).apply {
            mutate()
            setColor(outerColor)
        }
    }.also {
        background = it
    }
}
