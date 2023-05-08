package com.processout.sdk.di

import com.processout.sdk.api.service.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

internal interface ServiceGraph {
    val invoicesService: POInvoicesService
    val customerTokensService: POCustomerTokensService
    val alternativePaymentMethodsService: POAlternativePaymentMethodsService
}

internal class ServiceGraphImpl(
    networkGraph: NetworkGraph,
    repositoryGraph: RepositoryGraph,
    configuration: AlternativePaymentMethodsConfiguration
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
        AlternativePaymentMethodsServiceImpl(configuration)
}
