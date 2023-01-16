package com.processout.sdk.ui.shared.view.extensions

import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import com.processout.sdk.R

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
