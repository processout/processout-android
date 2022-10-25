package com.processout.sdk.di

import com.processout.sdk.api.repository.CardsRepository
import com.processout.sdk.api.repository.CardsRepositoryImpl
import com.processout.sdk.api.repository.GatewayConfigurationsRepository
import com.processout.sdk.api.repository.GatewayConfigurationsRepositoryImpl

internal interface RepositoryGraph {
    val gatewayConfigurationsRepository: GatewayConfigurationsRepository
    val cardsRepository: CardsRepository
}

internal class RepositoryGraphImpl(networkGraph: NetworkGraph) : RepositoryGraph {
    override val gatewayConfigurationsRepository: GatewayConfigurationsRepository =
        GatewayConfigurationsRepositoryImpl(networkGraph.gatewayConfigurationsApi)
    override val cardsRepository: CardsRepository = CardsRepositoryImpl(networkGraph.cardsApi)
}
