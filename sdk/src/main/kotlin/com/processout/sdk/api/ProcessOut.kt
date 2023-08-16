@file:Suppress("MemberVisibilityCanBePrivate", "unused", "RestrictedApi")

package com.processout.sdk.api

import com.processout.processout_sdk.ProcessOutAccessor
import com.processout.sdk.BuildConfig
import com.processout.sdk.api.dispatcher.PONativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.network.ApiConstants
import com.processout.sdk.api.network.NetworkConfiguration
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.api.repository.POGatewayConfigurationsRepository
import com.processout.sdk.api.service.*
import com.processout.sdk.core.logger.LogLevel
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.logger.SystemLoggerDestination
import com.processout.sdk.di.*

class ProcessOut private constructor(
    internal val apiGraph: ApiGraph
) {

    val gatewayConfigurations: POGatewayConfigurationsRepository
    val cards: POCardsRepository
    val invoices: POInvoicesService
    val customerTokens: POCustomerTokensService
    val alternativePaymentMethods: POAlternativePaymentMethodsService
    val browserCapabilities: POBrowserCapabilitiesService
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
            browserCapabilities = browserCapabilitiesService
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

        val isConfigured: Boolean
            get() = ::instance.isInitialized

        /**
         * Entry point to ProcessOut Android SDK.
         * Configures singleton instances accessible by [ProcessOut.instance] and [ProcessOut.legacyInstance].
         * Configuration applies only on first invocation and all subsequent calls are ignored.
         */
        fun configure(configuration: ProcessOutConfiguration) {
            if (isConfigured) {
                POLogger.info("Already configured.")
                return
            }

            if (configuration.debug) {
                POLogger.add(SystemLoggerDestination(LogLevel.DEBUG))
            }

            val contextGraph = ContextGraphImpl(
                application = configuration.application
            )

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
                contextGraph = contextGraph,
                networkGraph = networkGraph
            )

            val apiGraph = ApiGraph(
                repositoryGraph = repositoryGraph,
                serviceGraph = ServiceGraphImpl(
                    contextGraph = contextGraph,
                    networkGraph = networkGraph,
                    repositoryGraph = repositoryGraph,
                    alternativePaymentMethodsConfiguration = AlternativePaymentMethodsConfiguration(
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
