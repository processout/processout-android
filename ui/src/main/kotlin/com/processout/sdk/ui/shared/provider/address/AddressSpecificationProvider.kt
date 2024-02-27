package com.processout.sdk.ui.shared.provider.address

import android.app.Application
import com.processout.sdk.core.logger.POLogger
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException

internal class AddressSpecificationProvider(
    private val app: Application,
    private val moshi: Moshi = Moshi.Builder().build(),
    private val workDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private companion object {
        const val ASSET = "AddressSpecifications.json"
    }

    private val mutex = Mutex()
    private var specifications: Map<String, AddressSpecification>? = null

    private val default = AddressSpecification(
        units = AddressSpecification.Unit.entries,
        cityUnit = AddressSpecification.CityUnit.city,
        stateUnit = AddressSpecification.StateUnit.province,
        postcodeUnit = AddressSpecification.PostcodeUnit.postcode,
        states = null
    )

    suspend fun countryCodes(): Set<String> {
        return loadSpecifications().keys
    }

    suspend fun specification(countryCode: String): AddressSpecification {
        return loadSpecifications()[countryCode] ?: default
    }

    private suspend fun loadSpecifications(): Map<String, AddressSpecification> {
        specifications?.let {
            return it
        }
        return withContext(workDispatcher) {
            mutex.withLock {
                try {
                    val json = app.assets.open(ASSET).bufferedReader().use { it.readText() }
                    val adapter = moshi.adapter<Map<String, AddressSpecification>>(
                        Types.newParameterizedType(
                            Map::class.java, String::class.java, AddressSpecification::class.java
                        )
                    )
                    val specs = adapter.fromJson(json) ?: emptyMap()
                    specifications = specs
                    specs
                } catch (e: Exception) {
                    when (e) {
                        is CancellationException -> ensureActive()
                        is IOException -> POLogger.debug("Failed to open asset: %s", e)
                        is JsonDataException -> POLogger.debug("Failed to parse json: %s", e)
                    }
                    emptyMap()
                }
            }
        }
    }
}
