package com.processout.sdk.di

import com.processout.sdk.api.service.*

internal interface ServiceGraph {
    val threeDSService: ThreeDSService
    val invoicesService: InvoicesService
    val customerTokensService: CustomerTokensService
}

internal class ServiceGraphImpl(repositoryGraph: RepositoryGraph) : ServiceGraph {

    override val threeDSService: ThreeDSService = ThreeDSServiceImpl()

    override val invoicesService: InvoicesService =
        InvoicesServiceImpl(repositoryGraph.invoicesRepository, threeDSService)

    override val customerTokensService: CustomerTokensService =
        CustomerTokensServiceImpl(repositoryGraph.customerTokensRepository, threeDSService)
}
