package com.processout.sdk.api.repository

import com.processout.sdk.api.model.POGatewayConfigurations
import com.processout.sdk.api.network.GatewayConfigurationsApi
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.exception.ProcessOutException

internal class GatewayConfigurationsRepositoryImpl(
    private val api: GatewayConfigurationsApi
) : GatewayConfigurationsRepository {

    //TODO: implement generic inline function to handle Success/Failure including exceptions
    override suspend fun fetchGatewayConfigurations(): ProcessOutResult<POGatewayConfigurations> {
        val response = api.getGatewayConfigurations()
        return if (response.isSuccessful) {
            response.body()?.let { ProcessOutResult.Success(it) }
                ?: ProcessOutResult.Failure("Response body is empty.")
        } else {
            ProcessOutResult.Failure(
                response.errorBody()?.string()
                    ?: "Response code: ${response.code()}", ProcessOutException("NetworkException")
            )
        }
    }
}
