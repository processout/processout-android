package com.processout.sdk.ui.shared.formatter

import com.processout.sdk.ui.core.formatter.POFormatter

internal class CardSecurityCodeFormatter(
    private val scheme: String?
) : POFormatter {

    override fun format(string: String): String {
        var length = 4
        scheme?.let {
            if (it != "american express")
                length = 3
        }
        return string.take(length)
    }
}
