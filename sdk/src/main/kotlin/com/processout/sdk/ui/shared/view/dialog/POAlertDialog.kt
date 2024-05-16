package com.processout.sdk.ui.shared.view.dialog

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import com.processout.sdk.R
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.processout.sdk.databinding.PoAlertDialogBinding
import com.processout.sdk.ui.nativeapm.applyStyle
import com.processout.sdk.ui.shared.style.dialog.PODialogStyle

@ProcessOutInternalApi
class POAlertDialog(
    context: Context,
    private val title: String,
    private val message: String?,
    private val positiveActionText: String,
    private val negativeActionText: String?,
    style: PODialogStyle? = null
) : AlertDialog(
    context,
    R.style.ThemeOverlay_ProcessOut_AlertDialog
) {

    private val binding: PoAlertDialogBinding = PoAlertDialogBinding.inflate(
        LayoutInflater.from(ContextThemeWrapper(context, R.style.Theme_ProcessOut_Default))
    )

    init {
        setCancelable(false)
        setView(binding.root)
        style?.apply()
        initContent()
    }

    private fun PODialogStyle.apply() {
        binding.root.setBackgroundColor(backgroundColor)
        binding.poTitle.applyStyle(title)
        binding.poMessage.applyStyle(message)
        binding.poPositiveButton.applyStyle(positiveButton)
        binding.poNegativeButton.applyStyle(negativeButton)
    }

    private fun initContent() {
        binding.poTitle.text = title
        with(binding.poMessage) {
            if (message != null) {
                text = message
            } else {
                visibility = View.GONE
            }
        }
        binding.poPositiveButton.text = positiveActionText
        with(binding.poNegativeButton) {
            if (negativeActionText != null) {
                text = negativeActionText
            } else {
                visibility = View.GONE
            }
        }
    }

    fun onPositiveButtonClick(action: (DialogInterface) -> Unit): POAlertDialog {
        binding.poPositiveButton.setOnClickListener {
            action(this)
        }
        return this
    }

    fun onNegativeButtonClick(action: (DialogInterface) -> Unit): POAlertDialog {
        binding.poNegativeButton.setOnClickListener {
            action(this)
        }
        return this
    }
}
