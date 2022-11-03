package com.processout.sdk.api

import android.content.Context
import com.processout.sdk.BuildConfig
import com.processout.sdk.api.network.ApiConstants
import com.processout.sdk.api.network.NetworkConfiguration
import com.processout.sdk.api.provider.AlternativePaymentMethodProvider
import com.processout.sdk.api.provider.AlternativePaymentMethodProviderConfiguration
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
    val cards: CardsRepository,
    val alternativePaymentMethods: AlternativePaymentMethodProvider
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
                ),
                providerGraph = ProviderGraphImpl(
                    apmConfiguration = AlternativePaymentMethodProviderConfiguration(
                        projectId = configuration.projectId,
                        checkoutURL = ApiConstants.CHECKOUT_URL
                    )
                )
            )

            apiGraph.let {
                instance = lazy {
                    ProcessOutApi(
                        it.repositoryGraph.gatewayConfigurationsRepository,
                        it.repositoryGraph.invoicesRepository,
                        it.repositoryGraph.cardsRepository,
                        it.providerGraph.alternativePaymentMethodProvider
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
