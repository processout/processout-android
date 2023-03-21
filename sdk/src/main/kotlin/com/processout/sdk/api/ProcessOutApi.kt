package com.processout.sdk.api

import com.processout.processout_sdk.ProcessOut
import com.processout.processout_sdk.ProcessOutAccessor
import com.processout.sdk.BuildConfig
import com.processout.sdk.api.dispatcher.NativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.network.ApiConstants
import com.processout.sdk.api.network.NetworkConfiguration
import com.processout.sdk.api.provider.AlternativePaymentMethodProvider
import com.processout.sdk.api.provider.AlternativePaymentMethodProviderConfiguration
import com.processout.sdk.api.repository.CardsRepository
import com.processout.sdk.api.repository.GatewayConfigurationsRepository
import com.processout.sdk.api.service.CustomerTokensService
import com.processout.sdk.api.service.InvoicesService
import com.processout.sdk.core.exception.ProcessOutException
import com.processout.sdk.di.*

class ProcessOutApi private constructor(
    internal val apiGraph: ApiGraph
) {

    val gatewayConfigurations: GatewayConfigurationsRepository
    val cards: CardsRepository
    val invoices: InvoicesService
    val customerTokens: CustomerTokensService
    val alternativePaymentMethods: AlternativePaymentMethodProvider =
        apiGraph.providerGraph.alternativePaymentMethodProvider
    val nativeAlternativePaymentMethodEventDispatcher: NativeAlternativePaymentMethodEventDispatcher =
        apiGraph.dispatcherGraph.nativeAlternativePaymentMethodEventDispatcher

    init {
        with(apiGraph.repositoryGraph) {
            gatewayConfigurations = gatewayConfigurationsRepository
            cards = cardsRepository
        }
        with(apiGraph.serviceGraph) {
            invoices = invoicesService
            customerTokens = customerTokensService
        }
    }

    companion object {
        const val NAME = BuildConfig.LIBRARY_NAME
        const val VERSION = BuildConfig.LIBRARY_VERSION

        lateinit var instance: ProcessOutApi
            private set

        lateinit var legacyInstance: ProcessOut
            private set

        fun configure(configuration: ProcessOutApiConfiguration) {
            if (::instance.isInitialized)
                throw ProcessOutException("Already configured.")

            val repositoryGraph = RepositoryGraphImpl(
                networkGraph = NetworkGraphImpl(
                    configuration = NetworkConfiguration(
                        application = configuration.application,
                        sdkVersion = VERSION,
                        baseUrl = ApiConstants.BASE_URL,
                        projectId = configuration.projectId,
                        privateKey = configuration.privateKey
                    )
                ),
                contextGraph = ContextGraphImpl(
                    application = configuration.application
                )
            )

            val apiGraph = ApiGraph(
                repositoryGraph = repositoryGraph,
                serviceGraph = ServiceGraphImpl(repositoryGraph),
                providerGraph = ProviderGraphImpl(
                    configuration = AlternativePaymentMethodProviderConfiguration(
                        projectId = configuration.projectId,
                        checkoutURL = ApiConstants.CHECKOUT_URL
                    )
                ),
                dispatcherGraph = DispatcherGraphImpl()
            )

            apiGraph.let {
                instance = lazy { ProcessOutApi(it) }.value
            }

            legacyInstance = lazy {
                ProcessOutAccessor.initLegacyProcessOut(
                    configuration.application,
                    configuration.projectId
                )
            }.value
        }
    }
}
