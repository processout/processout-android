package com.processout.sdk.di

import com.processout.sdk.api.service.*
import com.processout.sdk.core.logger.LogLevel
import com.processout.sdk.core.logger.LoggerService
import com.processout.sdk.core.logger.SystemLoggerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

internal interface ServiceGraph {
    val invoicesService: POInvoicesService
    val customerTokensService: POCustomerTokensService
    val alternativePaymentMethodsService: POAlternativePaymentMethodsService
    val browserCapabilitiesService: POBrowserCapabilitiesService
    val systemLoggerService: LoggerService
    val remoteLoggerService: LoggerService
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

    override val systemLoggerService: LoggerService =
        SystemLoggerService(minimumLevel = LogLevel.DEBUG)

    override val remoteLoggerService: LoggerService =
        RemoteLoggerService(minimumLevel = LogLevel.WARN, repositoryGraph.logsRepository)
}
