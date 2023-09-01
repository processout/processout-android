package com.processout.sdk.ui.shared.view.extension

import android.content.Context
import android.graphics.Outline
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import com.processout.sdk.R
import com.processout.sdk.ui.shared.style.input.POInputFieldStyle

internal class TopRoundedCornersOutlineProvider(
    @DimenRes
    private val cornerRadiusRes: Int
) : ViewOutlineProvider() {

    override fun getOutline(view: View, outline: Outline) {
        val cornerRadius = view.resources.getDimensionPixelSize(cornerRadiusRes)
        outline.setRoundRect(
            0, 0, view.width, view.height + cornerRadius,
            cornerRadius.toFloat()
        )
    }
}

internal fun outlineBackground(
    cornerRadiusPx: Float,
    borderWidthPx: Int,
    @ColorInt borderColor: Int,
    @ColorInt backgroundColor: Int
): Drawable = GradientDrawable().apply {
    shape = GradientDrawable.RECTANGLE
    cornerRadius = cornerRadiusPx
    setStroke(borderWidthPx, borderColor)
    setColor(backgroundColor)
}

internal fun outlineBackground(
    context: Context,
    style: POInputFieldStyle
): Drawable = outlineBackground(
    style.border.radiusDp.dpToPx(context).toFloat(),
    style.border.widthDp.dpToPx(context),
    style.border.color,
    style.backgroundColor
)

internal fun defaultOutlineBackground(
    context: Context,
    @ColorRes borderColorRes: Int
): Drawable = outlineBackground(
    context.resources.getDimensionPixelSize(R.dimen.po_cornerRadius).toFloat(),
    context.resources.getDimensionPixelSize(R.dimen.po_borderWidth),
    ContextCompat.getColor(context, borderColorRes),
    ContextCompat.getColor(context, R.color.po_surface_background)
)
