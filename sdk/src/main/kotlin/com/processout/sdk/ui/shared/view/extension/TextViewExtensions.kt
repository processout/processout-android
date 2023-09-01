package com.processout.sdk.ui.shared.view.extension

import android.widget.TextView
import com.processout.sdk.ui.shared.text.markdown.SpannableMarkdownVisitor
import org.commonmark.parser.Parser

internal fun TextView.setMarkdown(markdown: String) {
    val parser = Parser.builder().build()
    val document = parser.parse(markdown)
    val visitor = SpannableMarkdownVisitor(textSize)
    document.accept(visitor)
    text = visitor.spanned
}
