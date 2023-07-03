package com.processout.sdk.utils

private const val escapeChar = '\\'
private const val markdownSpecialChars = "\\`*_{}[]()#+-.!"

internal fun escapedMarkdown(text: String?): String? =
    text?.let {
        StringBuilder().let {
            text.forEach { char ->
                if (markdownSpecialChars.contains(char)) {
                    it.append(escapeChar)
                }
                it.append(char)
            }
            it.toString()
        }
    }
