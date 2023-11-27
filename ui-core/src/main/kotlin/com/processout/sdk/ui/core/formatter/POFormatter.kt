package com.processout.sdk.ui.core.formatter

import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
interface POFormatter {
    fun format(string: String): String
}
