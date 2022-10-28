package com.processout.sdk.di

import com.processout.sdk.api.repository.*

internal interface RepositoryGraph {
    val gatewayConfigurationsRepository: GatewayConfigurationsRepository
    val invoicesRepository: InvoicesRepository
    val cardsRepository: CardsRepository
}

internal class RepositoryGraphImpl(networkGraph: NetworkGraph) : RepositoryGraph {

    override val gatewayConfigurationsRepository: GatewayConfigurationsRepository =
        GatewayConfigurationsRepositoryImpl(networkGraph.gatewayConfigurationsApi)

    override val invoicesRepository: InvoicesRepository =
        InvoicesRepositoryImpl(networkGraph.invoicesApi)

    override val cardsRepository: CardsRepository = CardsRepositoryImpl(networkGraph.cardsApi)
}
