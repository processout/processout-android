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

internal class ServiceGraphImpl(
    contextGraph: ContextGraph,
    networkGraph: NetworkGraph,
    repositoryGraph: RepositoryGraph,
    alternativePaymentMethodsConfiguration: AlternativePaymentMethodsConfiguration
) : ServiceGraph {

    private val mainCoroutineScope: CoroutineScope
        get() = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val threeDSService: ThreeDSService = ThreeDSServiceImpl(networkGraph.moshi)

    override val invoicesService: POInvoicesService =
        InvoicesServiceImpl(
            mainCoroutineScope,
            repositoryGraph.invoicesRepository,
            threeDSService
        )

    override val customerTokensService: POCustomerTokensService =
        CustomerTokensServiceImpl(
            mainCoroutineScope,
            repositoryGraph.customerTokensRepository,
            threeDSService
        )

    override val alternativePaymentMethodsService: POAlternativePaymentMethodsService =
        AlternativePaymentMethodsServiceImpl(alternativePaymentMethodsConfiguration)

    override val browserCapabilitiesService: POBrowserCapabilitiesService =
        BrowserCapabilitiesServiceImpl(contextGraph.application)

    override val systemLoggerService: POLoggerService =
        SystemLoggerService(minimumLevel = POLogLevel.DEBUG)

    override val remoteLoggerService: POLoggerService =
        RemoteLoggerService(minimumLevel = POLogLevel.WARN, repositoryGraph.logsRepository)
}
