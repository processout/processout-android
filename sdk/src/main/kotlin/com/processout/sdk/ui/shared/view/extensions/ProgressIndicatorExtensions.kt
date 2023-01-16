package com.processout.sdk.ui.shared.view.extensions

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable

internal fun indeterminateCircularProgressDrawable(
    context: Context,
    indicatorSizePx: Int,
    @ColorRes indicatorColorRes: Int
): IndeterminateDrawable<CircularProgressIndicatorSpec> {
    val spec = CircularProgressIndicatorSpec(context, null, 0, 0)
    spec.indicatorSize = indicatorSizePx
    spec.indicatorColors = intArrayOf(
        ContextCompat.getColor(context, indicatorColorRes)
    )
    spec.indicatorInset = 0
    spec.trackColor = ContextCompat.getColor(context, android.R.color.transparent)
    spec.trackThickness = 1.dpToPx(context.displayMetrics)
    return IndeterminateDrawable.createCircularDrawable(context, spec)
}
