package com.processout.sdk.ui.shared.mapper

import androidx.annotation.DrawableRes
import com.processout.sdk.ui.R

@DrawableRes
internal fun cardSchemeDrawableResId(scheme: String): Int? =
    when (scheme.lowercase()) {
        "american express" -> R.drawable.po_scheme_amex
        "carte bancaire" -> R.drawable.po_scheme_carte_bancaire
        "dinacard" -> R.drawable.po_scheme_dinacard
        "diners club" -> R.drawable.po_scheme_diners
        "diners club carte blanche" -> R.drawable.po_scheme_diners
        "diners club international" -> R.drawable.po_scheme_diners
        "diners club united states & canada" -> R.drawable.po_scheme_diners
        "discover" -> R.drawable.po_scheme_discover
        "elo" -> R.drawable.po_scheme_elo
        "giropay" -> R.drawable.po_scheme_giropay
        "jcb" -> R.drawable.po_scheme_jcb
        "mada" -> R.drawable.po_scheme_mada
        "maestro" -> R.drawable.po_scheme_maestro
        "mastercard" -> R.drawable.po_scheme_mastercard
        "rupay" -> R.drawable.po_scheme_rupay
        "sodexo" -> R.drawable.po_scheme_sodexo
        "china union pay" -> R.drawable.po_scheme_union_pay
        "verve" -> R.drawable.po_scheme_verve
        "visa" -> R.drawable.po_scheme_visa
        "vpay" -> R.drawable.po_scheme_vpay
        else -> null
    }
