package com.processout.sdk.ui.shared.provider.address

import com.processout.sdk.ui.shared.provider.address.AddressSpecification.*
import com.squareup.moshi.FromJson

internal class AddressSpecificationAdapter {

    @FromJson
    fun fromJson(specification: PlainAddressSpecification): AddressSpecification {
        val units = specification.units
        return AddressSpecification(
            units = units.mapNotNull { addressUnit(rawValue = it) },
            cityUnit = CityUnit.entries.find { it.toString() in units },
            stateUnit = StateUnit.entries.find { it.toString() in units },
            postcodeUnit = PostcodeUnit.entries.find { it.toString() in units }
        )
    }

    private fun addressUnit(rawValue: String): AddressUnit? =
        if (rawValue == "street")
            AddressUnit.street
        else if (CityUnit.entries.any { it.toString() == rawValue })
            AddressUnit.city
        else if (StateUnit.entries.any { it.toString() == rawValue })
            AddressUnit.state
        else if (PostcodeUnit.entries.any { it.toString() == rawValue })
            AddressUnit.postcode
        else null
}
