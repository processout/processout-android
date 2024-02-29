@file:Suppress("EnumEntryName")

package com.processout.sdk.ui.shared.provider.address

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class AddressSpecification(
    val units: List<Unit>?,
    val cityUnit: CityUnit?,
    val stateUnit: StateUnit?,
    val states: List<State>?,
    val postcodeUnit: PostcodeUnit?
) {

    @JsonClass(generateAdapter = false)
    enum class Unit {
        street, city, state, postcode
    }

    @JsonClass(generateAdapter = false)
    enum class CityUnit {
        city, district, postTown, suburb
    }

    @JsonClass(generateAdapter = false)
    enum class StateUnit {
        area, county, department, doSi, emirate, island, oblast, parish, prefecture, province, state
    }

    @JsonClass(generateAdapter = false)
    enum class PostcodeUnit {
        postcode, eircode, pin, zip
    }

    @JsonClass(generateAdapter = true)
    data class State(
        val abbreviation: String,
        val name: String
    )
}
