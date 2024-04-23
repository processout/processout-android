package com.processout.sdk.ui.core.component.texttoolbar

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem

internal class PrimaryTextActionModeCallback(
    private val callback: TextActionModeCallback
) : ActionMode.Callback {

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return callback.onCreateActionMode(mode, menu)
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return callback.onPrepareActionMode(mode, menu)
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        return callback.onActionItemClicked(mode, item)
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        callback.onDestroyActionMode()
    }
}
