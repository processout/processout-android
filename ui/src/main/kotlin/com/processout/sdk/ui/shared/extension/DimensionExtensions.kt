package com.processout.sdk.ui.shared.extension

import android.content.Context
import android.util.TypedValue
import kotlin.math.roundToInt

internal fun Number.dpToPx(context: Context) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
).roundToInt()
