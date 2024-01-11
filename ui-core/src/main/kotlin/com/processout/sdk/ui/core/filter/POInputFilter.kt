package com.processout.sdk.ui.core.filter

import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
interface POInputFilter {

    fun filter(value: TextFieldValue): TextFieldValue
}
