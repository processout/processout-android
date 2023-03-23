package com.processout.sdk.di

import com.processout.sdk.api.service.*

internal interface ServiceGraph {
    val threeDSService: ThreeDSService
    val invoicesService: InvoicesService
    val customerTokensService: CustomerTokensService
    val alternativePaymentMethodsService: AlternativePaymentMethodsService
}

internal class ServiceGraphImpl(
    networkGraph: NetworkGraph,
    repositoryGraph: RepositoryGraph,
    configuration: AlternativePaymentMethodsConfiguration
) : ServiceGraph {

    override val threeDSService: ThreeDSService = ThreeDSServiceImpl(networkGraph.moshi)

    override val invoicesService: InvoicesService =
        InvoicesServiceImpl(repositoryGraph.invoicesRepository, threeDSService)

    override val customerTokensService: CustomerTokensService =
        CustomerTokensServiceImpl(repositoryGraph.customerTokensRepository, threeDSService)

    override val alternativePaymentMethodsService: AlternativePaymentMethodsService =
        AlternativePaymentMethodsServiceImpl(configuration)
}
