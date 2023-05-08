@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.processout.sdk.api

import com.processout.processout_sdk.ProcessOutAccessor
import com.processout.sdk.BuildConfig
import com.processout.sdk.api.dispatcher.PONativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.network.ApiConstants
import com.processout.sdk.api.network.NetworkConfiguration
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.api.repository.POGatewayConfigurationsRepository
import com.processout.sdk.api.service.AlternativePaymentMethodsConfiguration
import com.processout.sdk.api.service.POAlternativePaymentMethodsService
import com.processout.sdk.api.service.POCustomerTokensService
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.core.exception.ProcessOutException
import com.processout.sdk.di.*

class ProcessOut private constructor(
    internal val apiGraph: ApiGraph
) {

    val gatewayConfigurations: POGatewayConfigurationsRepository
    val cards: POCardsRepository
    val invoices: POInvoicesService
    val customerTokens: POCustomerTokensService
    val alternativePaymentMethods: POAlternativePaymentMethodsService
    val nativeAlternativePaymentMethodEventDispatcher: PONativeAlternativePaymentMethodEventDispatcher

    init {
        with(apiGraph.repositoryGraph) {
            gatewayConfigurations = gatewayConfigurationsRepository
            cards = cardsRepository
        }
        with(apiGraph.serviceGraph) {
            invoices = invoicesService
            customerTokens = customerTokensService
            alternativePaymentMethods = alternativePaymentMethodsService
        }
        nativeAlternativePaymentMethodEventDispatcher =
            apiGraph.dispatcherGraph.nativeAlternativePaymentMethodEventDispatcher
    }

    companion object {
        const val NAME = BuildConfig.LIBRARY_NAME
        const val VERSION = BuildConfig.LIBRARY_VERSION

        lateinit var instance: ProcessOut
            private set

        lateinit var legacyInstance: com.processout.processout_sdk.ProcessOut
            private set

        /**
         * Entry point to ProcessOut SDK.
         * Configures singleton instances accessible by [ProcessOut.instance] and [ProcessOut.legacyInstance].
         * Method must be called only once and will throw [ProcessOutException] on subsequent calls.
         *
         * @throws ProcessOutException Thrown on subsequent calls indicating that ProcessOut SDK is already configured.
         */
        fun configure(configuration: ProcessOutConfiguration) {
            if (::instance.isInitialized)
                throw ProcessOutException("Already configured.")

            val networkGraph = NetworkGraphImpl(
                configuration = NetworkConfiguration(
                    application = configuration.application,
                    sdkVersion = VERSION,
                    baseUrl = ApiConstants.BASE_URL,
                    projectId = configuration.projectId,
                    privateKey = configuration.privateKey
                )
            )

            val repositoryGraph = RepositoryGraphImpl(
                networkGraph = networkGraph,
                contextGraph = ContextGraphImpl(
                    application = configuration.application
                )
            )

            val apiGraph = ApiGraph(
                repositoryGraph = repositoryGraph,
                serviceGraph = ServiceGraphImpl(
                    networkGraph = networkGraph,
                    repositoryGraph = repositoryGraph,
                    configuration = AlternativePaymentMethodsConfiguration(
                        baseUrl = ApiConstants.CHECKOUT_URL,
                        projectId = configuration.projectId
                    )
                ),
                dispatcherGraph = DispatcherGraphImpl()
            )

            instance = lazy { ProcessOut(apiGraph) }.value

            legacyInstance = lazy {
                ProcessOutAccessor.initLegacyProcessOut(
                    configuration.application,
                    configuration.projectId
                )
            }.value
        }
    }
}
