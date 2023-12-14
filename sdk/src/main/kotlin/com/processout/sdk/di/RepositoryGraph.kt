package com.processout.sdk.di

import com.processout.sdk.api.repository.*
import com.processout.sdk.core.POFailure

internal interface RepositoryGraph {
    val gatewayConfigurationsRepository: POGatewayConfigurationsRepository
    val invoicesRepository: InvoicesRepository
    val cardsRepository: POCardsRepository
    val customerTokensRepository: CustomerTokensRepository
    val logsRepository: LogsRepository
}

internal class DefaultRepositoryGraph(
    contextGraph: ContextGraph,
    private val networkGraph: NetworkGraph
) : RepositoryGraph {

    private val failureMapper: ApiFailureMapper
        get() = ApiFailureMapper(adapter = networkGraph.moshi.adapter(POFailure.ApiError::class.java))

    override val gatewayConfigurationsRepository: POGatewayConfigurationsRepository =
        DefaultGatewayConfigurationsRepository(failureMapper, networkGraph.gatewayConfigurationsApi)

    override val invoicesRepository: InvoicesRepository =
        DefaultInvoicesRepository(failureMapper, networkGraph.invoicesApi, contextGraph)

    override val cardsRepository: POCardsRepository =
        DefaultCardsRepository(failureMapper, networkGraph.cardsApi, contextGraph)

    override val customerTokensRepository: CustomerTokensRepository =
        DefaultCustomerTokensRepository(failureMapper, networkGraph.customerTokensApi, contextGraph)

    override val logsRepository: LogsRepository =
        DefaultLogsRepository(failureMapper, networkGraph.logsApi)
}
