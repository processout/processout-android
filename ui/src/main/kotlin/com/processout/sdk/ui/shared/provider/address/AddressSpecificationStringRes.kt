package com.processout.sdk.ui.shared.provider.address

import androidx.annotation.StringRes
import com.processout.sdk.R
import com.processout.sdk.ui.shared.provider.address.AddressSpecification.*
import com.processout.sdk.ui.shared.provider.address.AddressSpecification.CityUnit.*
import com.processout.sdk.ui.shared.provider.address.AddressSpecification.PostcodeUnit.*
import com.processout.sdk.ui.shared.provider.address.AddressSpecification.StateUnit.*

@StringRes
internal fun CityUnit.stringResId(): Int = when (this) {
    city -> R.string.po_address_spec_city
    district -> R.string.po_address_spec_district
    postTown -> R.string.po_address_spec_post_town
    suburb -> R.string.po_address_spec_suburb
}

@StringRes
internal fun StateUnit.stringResId(): Int = when (this) {
    area -> R.string.po_address_spec_area
    county -> R.string.po_address_spec_county
    department -> R.string.po_address_spec_department
    doSi -> R.string.po_address_spec_do_si
    emirate -> R.string.po_address_spec_emirate
    island -> R.string.po_address_spec_island
    oblast -> R.string.po_address_spec_oblast
    parish -> R.string.po_address_spec_parish
    prefecture -> R.string.po_address_spec_prefecture
    province -> R.string.po_address_spec_province
    state -> R.string.po_address_spec_state
}

@StringRes
internal fun PostcodeUnit.stringResId(): Int = when (this) {
    postcode -> R.string.po_address_spec_postcode
    eircode -> R.string.po_address_spec_eircode
    pin -> R.string.po_address_spec_pin
    zip -> R.string.po_address_spec_zip
}
