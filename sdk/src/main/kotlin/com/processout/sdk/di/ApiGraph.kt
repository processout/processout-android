package com.processout.sdk.di

internal data class ApiGraph(
    val repositoryGraph: RepositoryGraph,
    val serviceGraph: ServiceGraph,
    val providerGraph: ProviderGraph,
    val dispatcherGraph: DispatcherGraph
)
