package com.processout.sdk.di

import com.processout.sdk.api.repository.*

internal interface RepositoryGraph {
    val gatewayConfigurationsRepository: GatewayConfigurationsRepository
    val invoicesRepository: InvoicesRepository
    val cardsRepository: CardsRepository
    val customerTokensRepository: CustomerTokensRepository
}

internal class RepositoryGraphImpl(
    networkGraph: NetworkGraph,
    contextGraph: ContextGraph
) : RepositoryGraph {

    override val gatewayConfigurationsRepository: GatewayConfigurationsRepository =
        GatewayConfigurationsRepositoryImpl(networkGraph.gatewayConfigurationsApi)

    override val invoicesRepository: InvoicesRepository =
        InvoicesRepositoryImpl(networkGraph.invoicesApi, contextGraph, networkGraph.moshi)

    override val cardsRepository: CardsRepository =
        CardsRepositoryImpl(networkGraph.cardsApi, contextGraph)

    override val customerTokensRepository: CustomerTokensRepository =
        CustomerTokensRepositoryImpl(networkGraph.customerTokensApi, contextGraph, networkGraph.moshi)
}
