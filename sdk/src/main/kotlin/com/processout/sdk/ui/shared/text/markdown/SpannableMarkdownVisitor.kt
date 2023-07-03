package com.processout.sdk.ui.shared.text.markdown

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import com.processout.sdk.ui.shared.text.span.TextLeadingMarginSpan
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Emphasis
import org.commonmark.node.HardLineBreak
import org.commonmark.node.ListItem
import org.commonmark.node.OrderedList
import org.commonmark.node.Paragraph
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Text
import kotlin.math.roundToInt

internal class SpannableMarkdownVisitor(textSize: Float) : AbstractVisitor() {

    companion object {
        private const val DELIMITER = "."
        private const val BULLET = "•"
    }

    private val spannableBuilder = SpannableStringBuilder()
    val spanned: Spanned = spannableBuilder

    private val listItemMargin = (textSize * 2.5).roundToInt()

    override fun visit(text: Text) {
        spannableBuilder.append(text.literal)
    }

    override fun visit(emphasis: Emphasis) {
        val start = spannableBuilder.length
        visitChildren(emphasis)
        val end = spannableBuilder.length
        spannableBuilder.setSpan(StyleSpan(Typeface.ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    override fun visit(strongEmphasis: StrongEmphasis) {
        val start = spannableBuilder.length
        visitChildren(strongEmphasis)
        val end = spannableBuilder.length
        spannableBuilder.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    override fun visit(paragraph: Paragraph) {
        visitChildren(paragraph)
        spannableBuilder.append("\n\n")
    }

    override fun visit(softLineBreak: SoftLineBreak) {
        visitChildren(softLineBreak)
        spannableBuilder.appendLine()
    }

    override fun visit(hardLineBreak: HardLineBreak) {
        visitChildren(hardLineBreak)
        spannableBuilder.appendLine()
    }

    override fun visit(listItem: ListItem) {
        val start = spannableBuilder.length
        visitChildren(listItem)
        val end = spannableBuilder.length

        val marker = when (val parent = listItem.parent) {
            is OrderedList -> {
                val startNumber = parent.startNumber
                parent.startNumber += 9
                "$startNumber$DELIMITER"
            }
            else -> BULLET
        }

        spannableBuilder.setSpan(
            TextLeadingMarginSpan(
                margin = listItemMargin,
                indentation = listLevel(listItem),
                textWithinMargin = marker
            ), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun listLevel(listItem: ListItem): Int {
        var level = 0
        var parent = listItem.parent
        while (parent != null) {
            if (parent is ListItem) {
                level += 1
            }
            parent = parent.parent
        }
        return level
    }
}
