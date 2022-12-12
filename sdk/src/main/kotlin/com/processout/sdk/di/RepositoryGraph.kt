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
        GatewayConfigurationsRepositoryImpl(networkGraph.moshi, networkGraph.gatewayConfigurationsApi)

    override val invoicesRepository: InvoicesRepository =
        InvoicesRepositoryImpl(networkGraph.moshi, networkGraph.invoicesApi, contextGraph)

    override val cardsRepository: CardsRepository =
        CardsRepositoryImpl(networkGraph.moshi, networkGraph.cardsApi, contextGraph)

    override val customerTokensRepository: CustomerTokensRepository =
        CustomerTokensRepositoryImpl(networkGraph.moshi, networkGraph.customerTokensApi, contextGraph)
}
