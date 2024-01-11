package com.processout.sdk.ui.shared.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

internal abstract class BaseVisualTransformation : VisualTransformation {

    abstract fun transform(text: String): String

    abstract fun isSeparator(char: Char): Boolean

    override fun filter(text: AnnotatedString): TransformedText {
        val transformed = transform(text.text)
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                transformed.mapIndexedNotNull { index, char ->
                    index.takeIf { !isSeparator(char) }?.plus(1)
                }.let {
                    // Prepend 0 offset so cursor can be placed at the start of the text.
                    // Replace the last offset with the length of transformed text
                    // in case it ends with multiple separator chars in a row.
                    // This allows to place cursor at the end of the text.
                    listOf(0) + it.dropLast(1) + transformed.length
                }.let { it[offset] }

            override fun transformedToOriginal(offset: Int): Int =
                transformed.mapIndexedNotNull { index, char ->
                    index.takeIf { isSeparator(char) }
                }.count { separatorIndex ->
                    // Count how many separators precedes the transformed offset.
                    separatorIndex < offset
                }.let { separatorCount ->
                    // Calculate the original offset by subtracting the count of separators.
                    offset - separatorCount
                }
        }
        return TransformedText(AnnotatedString(transformed), offsetMapping)
    }
}
