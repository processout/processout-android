package com.processout.sdk.ui.core.state

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
@Immutable
interface POInputFilter {

    fun filter(value: TextFieldValue): TextFieldValue
}
