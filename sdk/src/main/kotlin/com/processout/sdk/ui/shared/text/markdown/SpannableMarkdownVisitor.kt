package com.processout.sdk.ui.shared.text.markdown

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.URLSpan
import com.processout.sdk.ui.shared.text.span.TextLeadingMarginSpan
import org.commonmark.node.*
import kotlin.math.roundToInt

internal class SpannableMarkdownVisitor(textSize: Float) : AbstractVisitor() {

    companion object {
        private const val NEW_LINE = '\n'
        private const val PARAGRAPH = "$NEW_LINE$NEW_LINE"
        private const val LIST_ITEM_DELIMITER = "."
        private const val LIST_ITEM_BULLET = "•"
    }

    private val spannableBuilder = SpannableStringBuilder()
    val spanned: Spanned
        get() = spannableBuilder.trimEndLines()

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

    override fun visit(link: Link) {
        val url = link.destination
        val start = spannableBuilder.length
        visitChildren(link)
        val end = spannableBuilder.length
        spannableBuilder.setSpan(URLSpan(url), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    override fun visit(paragraph: Paragraph) {
        visitChildren(paragraph)
        spannableBuilder.append(PARAGRAPH)
    }

    override fun visit(softLineBreak: SoftLineBreak) {
        visitChildren(softLineBreak)
        spannableBuilder.append(NEW_LINE)
    }

    override fun visit(hardLineBreak: HardLineBreak) {
        visitChildren(hardLineBreak)
        spannableBuilder.append(NEW_LINE)
    }

    override fun visit(listItem: ListItem) {
        val start = spannableBuilder.length
        visitChildren(listItem)
        val end = spannableBuilder.length

        val marker = when (val parent = listItem.parent) {
            is OrderedList -> {
                val startNumber = parent.markerStartNumber
                parent.markerStartNumber += 1
                "$startNumber$LIST_ITEM_DELIMITER"
            }
            else -> LIST_ITEM_BULLET
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

    private fun SpannableStringBuilder.trimEndLines(): SpannableStringBuilder {
        while (endsWith(NEW_LINE)) {
            delete(lastIndexOf(NEW_LINE), length)
        }
        return this
    }
}
