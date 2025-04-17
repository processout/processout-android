package com.processout.sdk.ui.shared.provider.address

import com.processout.sdk.ui.shared.provider.address.AddressSpecification.*
import com.squareup.moshi.FromJson

internal class AddressSpecificationAdapter {

    @FromJson
    fun fromJson(specification: AddressSpecification) = specification.copy(
        units = specification.units ?: AddressUnit.entries,
        cityUnit = specification.cityUnit ?: CityUnit.city,
        stateUnit = specification.stateUnit ?: StateUnit.province,
        postcodeUnit = specification.postcodeUnit ?: PostcodeUnit.postcode
    )
}
