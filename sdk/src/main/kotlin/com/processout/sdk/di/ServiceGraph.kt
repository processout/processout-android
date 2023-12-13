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
    val remoteLoggerService: POLoggerService
}

internal class DefaultServiceGraph(
    contextGraph: ContextGraph,
    networkGraph: NetworkGraph,
    repositoryGraph: RepositoryGraph,
    alternativePaymentMethodsConfiguration: AlternativePaymentMethodsConfiguration
) : ServiceGraph {

    private val mainCoroutineScope: CoroutineScope
        get() = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val threeDSService: ThreeDSService = DefaultThreeDSService(networkGraph.moshi)

    override val invoicesService: POInvoicesService =
        DefaultInvoicesService(
            mainCoroutineScope,
            repositoryGraph.invoicesRepository,
            threeDSService
        )

    override val customerTokensService: POCustomerTokensService =
        DefaultCustomerTokensService(
            mainCoroutineScope,
            repositoryGraph.customerTokensRepository,
            threeDSService
        )

    override val alternativePaymentMethodsService: POAlternativePaymentMethodsService =
        DefaultAlternativePaymentMethodsService(alternativePaymentMethodsConfiguration)

    override val browserCapabilitiesService: POBrowserCapabilitiesService =
        DefaultBrowserCapabilitiesService(contextGraph.application)

    override val systemLoggerService: POLoggerService =
        SystemLoggerService(minimumLevel = POLogLevel.DEBUG)

    override val remoteLoggerService: POLoggerService =
        RemoteLoggerService(minimumLevel = POLogLevel.WARN, repositoryGraph.logsRepository)
}
