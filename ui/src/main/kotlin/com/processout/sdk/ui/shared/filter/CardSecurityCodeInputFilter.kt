package com.processout.sdk.ui.shared.filter

import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.ui.core.filter.POInputFilter

internal class CardSecurityCodeInputFilter(
    private val scheme: String?
) : POInputFilter {

    override fun filter(value: TextFieldValue): TextFieldValue {
        var length = 4
        scheme?.let {
            if (it != "american express") {
                length = 3
            }
        }
        return value.copy(text = value.text.filter { it.isDigit() }.take(length))
    }
}
