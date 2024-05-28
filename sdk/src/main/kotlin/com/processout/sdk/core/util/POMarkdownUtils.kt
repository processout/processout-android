package com.processout.sdk.core.util

import com.processout.sdk.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
object POMarkdownUtils {

    private const val ESCAPE_CHAR = '\\'
    private const val MARKDOWN_SPECIAL_CHARS = "\\`*_{}[]()#+-.!"

    fun escapedMarkdown(text: String) = buildString {
        text.forEach { char ->
            if (MARKDOWN_SPECIAL_CHARS.contains(char)) {
                append(ESCAPE_CHAR)
            }
            append(char)
        }
    }
}
