package com.processout.sdk.ui.shared.view.extensions

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import kotlin.math.roundToInt

internal fun Number.dpToPx(displayMetrics: DisplayMetrics) =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), displayMetrics
    ).roundToInt()

internal val View.displayMetrics: DisplayMetrics
    get() = resources.displayMetrics

internal val Context.displayMetrics: DisplayMetrics
    get() = resources.displayMetrics
