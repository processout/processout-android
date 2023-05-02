package com.processout.sdk.ui.shared.view.extensions

import android.content.Context
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT

internal fun View.requestFocusAndShowKeyboard() {
    /**
     * Intended to be called when the window already has focus.
     */
    fun View.showKeyboard() {
        if (isFocused) {
            // Post to the message queue to handle case when the window focus changed
            // but InputMethodManager is not ready yet.
            post {
                with(context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager) {
                    showSoftInput(this@showKeyboard, SHOW_IMPLICIT)
                }
            }
        }
    }

    requestFocus()
    if (hasWindowFocus()) {
        showKeyboard()
    } else {
        viewTreeObserver.addOnWindowFocusChangeListener(
            object : ViewTreeObserver.OnWindowFocusChangeListener {
                override fun onWindowFocusChanged(hasFocus: Boolean) {
                    if (hasFocus) {
                        this@requestFocusAndShowKeyboard.showKeyboard()
                        viewTreeObserver.removeOnWindowFocusChangeListener(this)
                    }
                }
            })
    }
}

internal fun View.hideKeyboard() {
    with(context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager) {
        hideSoftInputFromWindow(windowToken, 0)
    }
}
