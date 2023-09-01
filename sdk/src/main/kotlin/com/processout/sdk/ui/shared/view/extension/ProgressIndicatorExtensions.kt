package com.processout.sdk.ui.shared.view.extension

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import com.processout.sdk.R

internal fun indeterminateCircularProgressDrawable(
    context: Context,
    indicatorSizePx: Int,
    @ColorInt indicatorColor: Int
): IndeterminateDrawable<CircularProgressIndicatorSpec> {
    val spec = CircularProgressIndicatorSpec(context, null, 0, 0)
    spec.indicatorSize = indicatorSizePx
    spec.indicatorColors = intArrayOf(indicatorColor)
    spec.indicatorInset = 0
    spec.trackColor = ContextCompat.getColor(context, android.R.color.transparent)
    spec.trackThickness = 1.dpToPx(context)
    return IndeterminateDrawable.createCircularDrawable(context, spec)
}

internal fun buttonCircularProgressDrawable(
    context: Context,
    @ColorInt indicatorColor: Int
): IndeterminateDrawable<CircularProgressIndicatorSpec> =
    indeterminateCircularProgressDrawable(
        context,
        context.resources.getDimensionPixelSize(R.dimen.po_button_circularProgressIndicator_size),
        indicatorColor
    )
