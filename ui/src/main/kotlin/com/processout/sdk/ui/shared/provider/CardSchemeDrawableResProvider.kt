package com.processout.sdk.ui.shared.provider

import androidx.annotation.DrawableRes
import com.processout.sdk.api.model.response.POCardScheme
import com.processout.sdk.api.model.response.POCardScheme.*
import com.processout.sdk.ui.R
import com.processout.sdk.ui.shared.extension.findBy

@DrawableRes
internal fun cardSchemeDrawableResId(scheme: String): Int? =
    when (POCardScheme::rawValue.findBy(scheme)) {
        AMEX -> R.drawable.po_scheme_amex
        CARTE_BANCAIRE -> R.drawable.po_scheme_carte_bancaire
        DINA_CARD -> R.drawable.po_scheme_dinacard
        DINERS_CLUB,
        DINERS_CLUB_CARTE_BLANCHE,
        DINERS_CLUB_INTERNATIONAL,
        DINERS_CLUB_UNITED_STATES_AND_CANADA -> R.drawable.po_scheme_diners
        DISCOVER -> R.drawable.po_scheme_discover
        ELO -> R.drawable.po_scheme_elo
        GIROPAY -> R.drawable.po_scheme_giropay
        JCB -> R.drawable.po_scheme_jcb
        MADA -> R.drawable.po_scheme_mada
        MAESTRO -> R.drawable.po_scheme_maestro
        MASTERCARD -> R.drawable.po_scheme_mastercard
        RUPAY -> R.drawable.po_scheme_rupay
        SODEXO -> R.drawable.po_scheme_sodexo
        UNION_PAY -> R.drawable.po_scheme_union_pay
        VERVE -> R.drawable.po_scheme_verve
        VISA -> R.drawable.po_scheme_visa
        V_PAY -> R.drawable.po_scheme_vpay
        else -> null
    }
