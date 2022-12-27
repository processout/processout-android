package com.processout.sdk.ui.shared.view.extensions

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.processout.sdk.R

internal fun outline(
    context: Context,
    @ColorRes borderColorRes: Int
): Drawable = GradientDrawable().apply {
    shape = GradientDrawable.RECTANGLE
    cornerRadius = context.resources.getDimensionPixelSize(R.dimen.po_cornerRadius).toFloat()
    setStroke(
        context.resources.getDimensionPixelSize(R.dimen.po_borderWidth),
        ContextCompat.getColor(context, borderColorRes)
    )
    setColor(Color.TRANSPARENT)
}
