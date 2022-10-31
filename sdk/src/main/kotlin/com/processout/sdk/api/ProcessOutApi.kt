package com.processout.sdk.api

import android.content.Context
import com.processout.sdk.BuildConfig
import com.processout.sdk.api.network.ApiConstants
import com.processout.sdk.api.network.NetworkConfiguration
import com.processout.sdk.api.repository.CardsRepository
import com.processout.sdk.api.repository.GatewayConfigurationsRepository
import com.processout.sdk.api.repository.InvoicesRepository
import com.processout.sdk.core.exception.ProcessOutException
import com.processout.sdk.di.*
import com.processout.sdk.di.ApiGraph
import com.processout.sdk.di.NetworkGraphImpl
import com.processout.sdk.di.RepositoryGraphImpl

class ProcessOutApi private constructor(
    val gatewayConfigurations: GatewayConfigurationsRepository,
    val invoices: InvoicesRepository,
    val cards: CardsRepository
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
                            sdkVersion = VERSION,
                            baseUrl = ApiConstants.BASE_URL,
                            projectId = configuration.projectId,
                            privateKey = configuration.privateKey
                        )
                    ),
                    contextGraph = ContextGraphImpl(
                        context = configuration.context.applicationContext
                    )
                )
            )

            apiGraph.repositoryGraph.let {
                instance = lazy {
                    ProcessOutApi(
                        it.gatewayConfigurationsRepository,
                        it.invoicesRepository,
                        it.cardsRepository
                    )
                }.value
            }
        }
    }

    data class Configuration(
        val context: Context,
        val projectId: String,
        /**
         * __Warning: only for testing purposes.__
         *
         * Storing private key inside application is extremely dangerous and highly discouraged.
         */
        internal val privateKey: String = String()
    )
}
