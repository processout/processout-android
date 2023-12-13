package com.processout.sdk.di

import com.processout.sdk.api.repository.*

internal interface RepositoryGraph {
    val gatewayConfigurationsRepository: POGatewayConfigurationsRepository
    val invoicesRepository: InvoicesRepository
    val cardsRepository: POCardsRepository
    val customerTokensRepository: CustomerTokensRepository
    val logsRepository: LogsRepository
}

internal class DefaultRepositoryGraph(
    contextGraph: ContextGraph,
    networkGraph: NetworkGraph
) : RepositoryGraph {

    override val gatewayConfigurationsRepository: POGatewayConfigurationsRepository =
        GatewayConfigurationsRepositoryImpl(networkGraph.moshi, networkGraph.gatewayConfigurationsApi)

    override val invoicesRepository: InvoicesRepository =
        InvoicesRepositoryImpl(networkGraph.moshi, networkGraph.invoicesApi, contextGraph)

    override val cardsRepository: POCardsRepository =
        CardsRepositoryImpl(networkGraph.moshi, networkGraph.cardsApi, contextGraph)

    override val customerTokensRepository: CustomerTokensRepository =
        CustomerTokensRepositoryImpl(networkGraph.moshi, networkGraph.customerTokensApi, contextGraph)

    override val logsRepository: LogsRepository =
        LogsRepositoryImpl(networkGraph.moshi, networkGraph.logsApi)
}
