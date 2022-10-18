package com.processout.sdk.api

import com.processout.sdk.BuildConfig
import com.processout.sdk.api.network.ApiConstants
import com.processout.sdk.api.network.NetworkConfiguration
import com.processout.sdk.core.exception.ProcessOutException
import com.processout.sdk.di.ApiGraph
import com.processout.sdk.di.NetworkGraphImpl
import com.processout.sdk.di.RepositoryGraphImpl

class ProcessOutApi private constructor(
    private val configuration: Configuration
) {

    private val apiGraph: ApiGraph by lazy {
        ApiGraph(
            repositoryGraph = RepositoryGraphImpl(
                networkGraph = NetworkGraphImpl(
                    NetworkConfiguration(
                        ApiConstants.BASE_URL,
                        configuration.projectId,
                        VERSION
                    )
                )
            )
        )
    }

    private val gatewayConfigurationsRepository by lazy {
        apiGraph.repositoryGraph.gatewayConfigurationsRepository
    }

    init {
        apiGraph // touch the graph to init dependencies
    }

    companion object {
        const val VERSION = BuildConfig.LIBRARY_VERSION

        lateinit var instance: ProcessOutApi
            private set

        fun configure(configuration: Configuration) {
            if (::instance.isInitialized)
                throw ProcessOutException("Already configured.")
            instance = lazy { ProcessOutApi(configuration) }.value
        }
    }

    data class Configuration(val projectId: String)
}
