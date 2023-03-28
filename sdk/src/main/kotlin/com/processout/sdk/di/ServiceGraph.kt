package com.processout.sdk.di

import com.processout.sdk.api.service.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

internal interface ServiceGraph {
    val invoicesService: InvoicesService
    val customerTokensService: CustomerTokensService
    val alternativePaymentMethodsService: AlternativePaymentMethodsService
}

internal class ServiceGraphImpl(
    networkGraph: NetworkGraph,
    repositoryGraph: RepositoryGraph,
    configuration: AlternativePaymentMethodsConfiguration
) : ServiceGraph {

    private val mainCoroutineScope: CoroutineScope
        get() = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val threeDSService: ThreeDSService = ThreeDSServiceImpl(networkGraph.moshi)

    override val invoicesService: InvoicesService =
        InvoicesServiceImpl(
            mainCoroutineScope,
            repositoryGraph.invoicesRepository,
            threeDSService
        )

    override val customerTokensService: CustomerTokensService =
        CustomerTokensServiceImpl(
            mainCoroutineScope,
            repositoryGraph.customerTokensRepository,
            threeDSService
        )

    override val alternativePaymentMethodsService: AlternativePaymentMethodsService =
        AlternativePaymentMethodsServiceImpl(configuration)
}
