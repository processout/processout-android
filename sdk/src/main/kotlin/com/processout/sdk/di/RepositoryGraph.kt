package com.processout.sdk.di

import com.processout.sdk.api.repository.*
import com.processout.sdk.core.POFailure

internal interface RepositoryGraph {
    val gatewayConfigurationsRepository: POGatewayConfigurationsRepository
    val invoicesRepository: InvoicesRepository
    val cardsRepository: POCardsRepository
    val customerTokensRepository: CustomerTokensRepository
    val telemetryRepository: TelemetryRepository
}

internal class DefaultRepositoryGraph(
    contextGraph: ContextGraph,
    private val networkGraph: NetworkGraph
) : RepositoryGraph {

    private val failureMapper: ApiFailureMapper
        get() = ApiFailureMapper(adapter = networkGraph.moshi.adapter(POFailure.ApiError::class.java))

    override val gatewayConfigurationsRepository: POGatewayConfigurationsRepository by lazy {
        DefaultGatewayConfigurationsRepository(failureMapper, contextGraph.mainScope, networkGraph.gatewayConfigurationsApi)
    }

    override val invoicesRepository: InvoicesRepository by lazy {
        DefaultInvoicesRepository(failureMapper, networkGraph.invoicesApi, contextGraph)
    }

    override val cardsRepository: POCardsRepository by lazy {
        DefaultCardsRepository(failureMapper, networkGraph.cardsApi, contextGraph)
    }

    override val customerTokensRepository: CustomerTokensRepository by lazy {
        DefaultCustomerTokensRepository(failureMapper, networkGraph.customerTokensApi, contextGraph)
    }

    override val telemetryRepository: TelemetryRepository by lazy {
        DefaultTelemetryRepository(failureMapper, contextGraph.mainScope, networkGraph.telemetryApi)
    }
}
