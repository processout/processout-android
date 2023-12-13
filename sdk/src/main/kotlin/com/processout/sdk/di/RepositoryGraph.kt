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
        DefaultGatewayConfigurationsRepository(networkGraph.moshi, networkGraph.gatewayConfigurationsApi)

    override val invoicesRepository: InvoicesRepository =
        DefaultInvoicesRepository(networkGraph.moshi, networkGraph.invoicesApi, contextGraph)

    override val cardsRepository: POCardsRepository =
        DefaultCardsRepository(networkGraph.moshi, networkGraph.cardsApi, contextGraph)

    override val customerTokensRepository: CustomerTokensRepository =
        DefaultCustomerTokensRepository(networkGraph.moshi, networkGraph.customerTokensApi, contextGraph)

    override val logsRepository: LogsRepository =
        DefaultLogsRepository(networkGraph.moshi, networkGraph.logsApi)
}
