package com.processout.sdk.di

import com.processout.sdk.api.service.*
import com.processout.sdk.core.logger.POLogLevel
import com.processout.sdk.core.logger.POLoggerService
import com.processout.sdk.core.logger.SystemLoggerService

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

    private val customerActionsService: CustomerActionsService by lazy {
        DefaultCustomerActionsService(networkGraph.moshi)
    }

    override val invoicesService: POInvoicesService by lazy {
        DefaultInvoicesService(
            contextGraph.mainScope,
            repositoryGraph.invoicesRepository,
            customerActionsService
        )
    }

    override val customerTokensService: POCustomerTokensService by lazy {
        DefaultCustomerTokensService(
            contextGraph.mainScope,
            repositoryGraph.customerTokensRepository,
            customerActionsService
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
            repository = repositoryGraph.telemetryRepository,
            contextGraph = contextGraph
        )
    }
}
