package com.processout.sdk.ui.shared.view.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import com.processout.sdk.R
import com.processout.sdk.databinding.PoAlertDialogBinding

internal class POAlertDialog(
    context: Context
) : AlertDialog(
    context,
    R.style.ThemeOverlay_ProcessOut_AlertDialog
) {

    private val binding: PoAlertDialogBinding = PoAlertDialogBinding.inflate(
        LayoutInflater.from(ContextThemeWrapper(context, R.style.Theme_ProcessOut_Default))
    )

    init {
        setView(binding.root)
    }
}