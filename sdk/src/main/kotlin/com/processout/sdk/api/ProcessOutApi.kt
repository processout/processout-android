package com.processout.sdk.api

import com.processout.sdk.BuildConfig
import com.processout.sdk.api.network.ApiConstants
import com.processout.sdk.api.network.NetworkConfiguration
import com.processout.sdk.api.repository.GatewayConfigurationsRepository
import com.processout.sdk.core.exception.ProcessOutException
import com.processout.sdk.di.ApiGraph
import com.processout.sdk.di.NetworkGraphImpl
import com.processout.sdk.di.RepositoryGraphImpl

class ProcessOutApi private constructor(
    val gatewayConfigurationsRepository: GatewayConfigurationsRepository
) {

    companion object {
        const val VERSION = BuildConfig.LIBRARY_VERSION

        lateinit var instance: ProcessOutApi
            private set

        fun configure(configuration: Configuration) {
            if (::instance.isInitialized)
                throw ProcessOutException("Already configured.")

            val apiGraph = ApiGraph(
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

            apiGraph.repositoryGraph.let {
                instance = lazy {
                    ProcessOutApi(
                        it.gatewayConfigurationsRepository
                    )
                }.value
            }
        }
    }

    data class Configuration(val projectId: String)
}
