package com.processout.sdk.ui.shared.component

import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.processout.sdk.ui.shared.view.extension.POTextViewExtensions

@Composable
internal fun TextAndroidView(
    text: String,
    modifier: Modifier = Modifier,
    selectable: Boolean = false
) {
    AndroidView(
        factory = { context ->
            TextView(context).apply {
                setTextIsSelectable(selectable)
                POTextViewExtensions.setMarkdown(
                    textView = this,
                    markdown = text
                )
            }
        },
        modifier = modifier
    )
}
