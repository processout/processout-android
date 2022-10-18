package com.processout.sdk.di

import com.processout.sdk.api.repository.GatewayConfigurationsRepository
import com.processout.sdk.api.repository.GatewayConfigurationsRepositoryImpl

internal interface RepositoryGraph {
    val gatewayConfigurationsRepository: GatewayConfigurationsRepository
}

internal class RepositoryGraphImpl(networkGraph: NetworkGraph) : RepositoryGraph {
    override val gatewayConfigurationsRepository: GatewayConfigurationsRepository =
        GatewayConfigurationsRepositoryImpl(networkGraph.gatewayConfigurationsApi)
}
