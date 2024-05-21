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
    private val confirmActionText: String,
    private val dismissActionText: String?,
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

    private fun PODialogStyle.apply() = with(binding) {
        root.setBackgroundColor(backgroundColor)
        poTitle.applyStyle(title)
        poMessage.applyStyle(message)
        poConfirmButton.applyStyle(confirmButton)
        poDismissButton.applyStyle(dismissButton)
    }

    private fun initContent() {
        binding.poTitle.text = title
        with(binding.poMessage) {
            if (message.isNullOrBlank()) {
                visibility = View.GONE
            } else {
                text = message
            }
        }
        binding.poConfirmButton.text = confirmActionText
        with(binding.poDismissButton) {
            if (dismissActionText.isNullOrBlank()) {
                visibility = View.GONE
            } else {
                text = dismissActionText
            }
        }
    }

    fun onConfirmButtonClick(action: (DialogInterface) -> Unit): POAlertDialog {
        binding.poConfirmButton.setOnClickListener {
            action(this)
        }
        return this
    }

    fun onDismissButtonClick(action: (DialogInterface) -> Unit): POAlertDialog {
        binding.poDismissButton.setOnClickListener {
            action(this)
        }
        return this
    }
}
