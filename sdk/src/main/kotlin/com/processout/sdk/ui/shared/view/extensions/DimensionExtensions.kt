package com.processout.sdk.ui.shared.view.extensions

import android.content.Context
import android.util.TypedValue
import kotlin.math.roundToInt

internal fun Number.dpToPx(context: Context) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
).roundToInt()

internal fun Number.spToPx(context: Context) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP, this.toFloat(), context.resources.displayMetrics
).roundToInt()
