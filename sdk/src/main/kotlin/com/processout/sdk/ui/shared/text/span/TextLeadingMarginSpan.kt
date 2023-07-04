package com.processout.sdk.ui.shared.text.span

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.Spanned
import android.text.style.LeadingMarginSpan

/**
 * A version of [LeadingMarginSpan] that draws text within the margin.
 *
 * @param margin Leading margin in pixels.
 * @param indentation Zero-based indentation level.
 * @param textWithinMargin Text to draw within the margin.
 */
internal class TextLeadingMarginSpan(
    private val margin: Int,
    private val indentation: Int,
    private val textWithinMargin: String
) : LeadingMarginSpan {

    override fun getLeadingMargin(first: Boolean) = margin

    override fun drawLeadingMargin(
        c: Canvas,
        p: Paint,
        x: Int,
        dir: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        first: Boolean,
        layout: Layout
    ) {
        if (!firstLine(text, start))
            return

        val textWidth = p.measureText(textWithinMargin)
        val textStartX = (margin - textWidth) - (margin / 8)
        var absoluteTextStartX = textStartX
        for (i in 1..indentation) {
            absoluteTextStartX += margin
        }
        c.drawText(textWithinMargin, absoluteTextStartX, baseline.toFloat(), p)
    }

    private fun firstLine(text: CharSequence, start: Int) =
        (text as Spanned).getSpanStart(this) == start
}
