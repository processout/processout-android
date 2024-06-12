package com.processout.sdk.di

import com.processout.sdk.api.service.*
import com.processout.sdk.core.logger.POLogLevel
import com.processout.sdk.core.logger.POLoggerService
import com.processout.sdk.core.logger.SystemLoggerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

internal interface ServiceGraph {
    val invoicesService: POInvoicesService
    val customerTokensService: POCustomerTokensService
    val alternativePaymentMethodsService: POAlternativePaymentMethodsService
    val browserCapabilitiesService: POBrowserCapabilitiesService
    val systemLoggerService: POLoggerService
    val telemetryService: POLoggerService
}

internal class DefaultServiceGraph(
    contextGraph: ContextGraph,
    networkGraph: NetworkGraph,
    repositoryGraph: RepositoryGraph,
    alternativePaymentMethodsBaseUrl: String
) : ServiceGraph {

    private val mainCoroutineScope: CoroutineScope
        get() = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val threeDSService: ThreeDSService by lazy {
        DefaultThreeDSService(networkGraph.moshi)
    }

    override val invoicesService: POInvoicesService by lazy {
        DefaultInvoicesService(
            mainCoroutineScope,
            repositoryGraph.invoicesRepository,
            threeDSService
        )
    }

    override val customerTokensService: POCustomerTokensService by lazy {
        DefaultCustomerTokensService(
            mainCoroutineScope,
            repositoryGraph.customerTokensRepository,
            threeDSService
        )
    }

    override val alternativePaymentMethodsService: POAlternativePaymentMethodsService by lazy {
        DefaultAlternativePaymentMethodsService(alternativePaymentMethodsBaseUrl, contextGraph)
    }

    override val browserCapabilitiesService: POBrowserCapabilitiesService by lazy {
        DefaultBrowserCapabilitiesService(contextGraph)
    }

    override val systemLoggerService: POLoggerService by lazy {
        SystemLoggerService(minimumLevel = POLogLevel.DEBUG)
    }

    override val telemetryService: POLoggerService by lazy {
        TelemetryService(
            minimumLevel = POLogLevel.WARN,
            scope = mainCoroutineScope,
            repository = repositoryGraph.telemetryRepository,
            contextGraph = contextGraph
        )
    }
}
