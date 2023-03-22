package com.processout.sdk.di

import com.processout.sdk.api.service.*

internal interface ServiceGraph {
    val threeDSHandler: ThreeDSHandler
    val invoicesService: InvoicesService
    val customerTokensService: CustomerTokensService
    val alternativePaymentMethodsService: AlternativePaymentMethodsService
}

internal class ServiceGraphImpl(
    networkGraph: NetworkGraph,
    repositoryGraph: RepositoryGraph,
    configuration: AlternativePaymentMethodsConfiguration
) : ServiceGraph {

    override val threeDSHandler: ThreeDSHandler = ThreeDSHandlerImpl(networkGraph.moshi)

    override val invoicesService: InvoicesService =
        InvoicesServiceImpl(repositoryGraph.invoicesRepository, threeDSHandler)

    override val customerTokensService: CustomerTokensService =
        CustomerTokensServiceImpl(repositoryGraph.customerTokensRepository, threeDSHandler)

    override val alternativePaymentMethodsService: AlternativePaymentMethodsService =
        AlternativePaymentMethodsServiceImpl(configuration)
}
