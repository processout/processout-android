package com.processout.sdk.core.util

private const val ESCAPE_CHAR = '\\'
private const val MARKDOWN_SPECIAL_CHARS = "\\`*_{}[]()#+-.!"

internal fun escapedMarkdown(text: String?): String? =
    text?.let {
        StringBuilder().let {
            text.forEach { char ->
                if (MARKDOWN_SPECIAL_CHARS.contains(char)) {
                    it.append(ESCAPE_CHAR)
                }
                it.append(char)
            }
            it.toString()
        }
    }
