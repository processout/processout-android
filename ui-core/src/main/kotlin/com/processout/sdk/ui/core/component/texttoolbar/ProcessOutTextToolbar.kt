package com.processout.sdk.ui.core.component.texttoolbar

import android.os.Build
import android.view.ActionMode
import android.view.View
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus

internal class ProcessOutTextToolbar(
    private val view: View,
    private val onCopyRequested: (() -> Unit)? = null,
    private val onPasteRequested: (() -> Unit)? = null,
    private val onCutRequested: (() -> Unit)? = null,
    private val onSelectAllRequested: (() -> Unit)? = null,
    private val hideUnspecifiedActions: Boolean = false
) : TextToolbar {

    private var actionMode: ActionMode? = null

    private val textActionModeCallback = TextActionModeCallback(
        onActionModeDestroy = {
            actionMode = null
        }
    )

    override var status = TextToolbarStatus.Hidden
        private set

    override fun showMenu(
        rect: Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?
    ) {
        textActionModeCallback.rect = rect
        if (hideUnspecifiedActions) {
            textActionModeCallback.onCopyRequested = this.onCopyRequested
            textActionModeCallback.onPasteRequested = this.onPasteRequested
            textActionModeCallback.onCutRequested = this.onCutRequested
            textActionModeCallback.onSelectAllRequested = this.onSelectAllRequested
        } else {
            textActionModeCallback.onCopyRequested = this.onCopyRequested ?: onCopyRequested
            textActionModeCallback.onPasteRequested = this.onPasteRequested ?: onPasteRequested
            textActionModeCallback.onCutRequested = this.onCutRequested ?: onCutRequested
            textActionModeCallback.onSelectAllRequested = this.onSelectAllRequested ?: onSelectAllRequested
        }
        if (actionMode == null) {
            actionMode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.startActionMode(
                    FloatingTextActionModeCallback(textActionModeCallback),
                    ActionMode.TYPE_FLOATING
                )
            } else {
                view.startActionMode(
                    PrimaryTextActionModeCallback(textActionModeCallback)
                )
            }
            status = TextToolbarStatus.Shown
        } else {
            actionMode?.invalidate()
        }
    }

    override fun hide() {
        actionMode?.finish()
        actionMode = null
        status = TextToolbarStatus.Hidden
    }
}
