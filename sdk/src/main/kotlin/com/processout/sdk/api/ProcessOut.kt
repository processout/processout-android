@file:Suppress("MemberVisibilityCanBePrivate", "unused", "RestrictedApi", "removal")

package com.processout.sdk.api

import com.processout.processout_sdk.ProcessOutAccessor
import com.processout.sdk.BuildConfig
import com.processout.sdk.api.dispatcher.DefaultEventDispatchers
import com.processout.sdk.api.dispatcher.POEventDispatchers
import com.processout.sdk.api.dispatcher.PONativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.dispatcher.nativeapm.DefaultNativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.network.ApiConstants
import com.processout.sdk.api.network.NetworkConfiguration
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.api.repository.POGatewayConfigurationsRepository
import com.processout.sdk.api.service.*
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.di.*

/**
 * Entry point to ProcessOut Android SDK.
 * Provides configuration and access to services.
 * Use function [ProcessOut.configure] to initialize the SDK.
 * Access services with [ProcessOut.instance] and [ProcessOut.legacyInstance].
 */
class ProcessOut private constructor(
    internal val apiGraph: ApiGraph
) {

    /** Gateway configurations repository. */
    val gatewayConfigurations: POGatewayConfigurationsRepository by lazy {
        apiGraph.repositoryGraph.gatewayConfigurationsRepository
    }

    /** Cards repository. */
    val cards: POCardsRepository by lazy {
        apiGraph.repositoryGraph.cardsRepository
    }

    /** Invoices service. */
    val invoices: POInvoicesService by lazy {
        apiGraph.serviceGraph.invoicesService
    }

    /** Customer tokens service. */
    val customerTokens: POCustomerTokensService by lazy {
        apiGraph.serviceGraph.customerTokensService
    }

    /** Alternative payment methods service. */
    val alternativePaymentMethods: POAlternativePaymentMethodsService by lazy {
        apiGraph.serviceGraph.alternativePaymentMethodsService
    }

    /** Browser capabilities service. */
    val browserCapabilities: POBrowserCapabilitiesService by lazy {
        apiGraph.serviceGraph.browserCapabilitiesService
    }

    /** Dispatcher that allows to handle events during native alternative payments. */
    @Deprecated(
        message = "Use replacement property.",
        replaceWith = ReplaceWith("dispatchers.nativeAlternativePaymentMethod")
    )
    val nativeAlternativePaymentMethodEventDispatcher: PONativeAlternativePaymentMethodEventDispatcher by lazy {
        DefaultNativeAlternativePaymentMethodEventDispatcher
    }

    /** Dispatchers that allows to handle events during various payment flows. */
    val dispatchers: POEventDispatchers by lazy { DefaultEventDispatchers }

    /**
     * Entry point to ProcessOut Android SDK.
     * Provides configuration and access to services.
     * Use function [ProcessOut.configure] to initialize the SDK.
     * Access services with [ProcessOut.instance] and [ProcessOut.legacyInstance].
     */
    companion object {
        /** SDK name. */
        const val NAME: String = BuildConfig.LIBRARY_NAME

        /** SDK version. */
        const val VERSION: String = BuildConfig.LIBRARY_VERSION

        /** Singleton instance of ProcessOut. */
        lateinit var instance: ProcessOut
            private set

        /** Singleton instance of legacy ProcessOut. __Note:__ it will be removed in a future major release. */
        lateinit var legacyInstance: com.processout.processout_sdk.ProcessOut
            private set

        /** Returns _true_ if ProcessOut has been configured. */
        val isConfigured: Boolean
            get() = ::instance.isInitialized

        /**
         * Configures singleton instances accessible by [ProcessOut.instance] and [ProcessOut.legacyInstance].
         * Configuration applies only on first invocation and all subsequent calls are ignored.
         */
        fun configure(configuration: ProcessOutConfiguration) {
            if (isConfigured) {
                POLogger.info("Already configured.")
                return
            }

            val contextGraph = DefaultContextGraph(
                configuration = configuration
            )

            val networkGraph = DefaultNetworkGraph(
                configuration = NetworkConfiguration(
                    application = configuration.application,
                    sdkVersion = VERSION,
                    baseUrl = ApiConstants.BASE_URL,
                    projectId = configuration.projectId,
                    privateKey = configuration.privateKey,
                    debug = configuration.debug
                )
            )

            val repositoryGraph = DefaultRepositoryGraph(
                contextGraph = contextGraph,
                networkGraph = networkGraph
            )

            val apiGraph = ApiGraph(
                contextGraph = contextGraph,
                repositoryGraph = repositoryGraph,
                serviceGraph = DefaultServiceGraph(
                    contextGraph = contextGraph,
                    networkGraph = networkGraph,
                    repositoryGraph = repositoryGraph,
                    alternativePaymentMethodsConfiguration = AlternativePaymentMethodsConfiguration(
                        baseUrl = ApiConstants.CHECKOUT_URL,
                        projectId = configuration.projectId
                    )
                )
            )

            if (configuration.debug) {
                POLogger.add(apiGraph.serviceGraph.systemLoggerService)
            }

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
