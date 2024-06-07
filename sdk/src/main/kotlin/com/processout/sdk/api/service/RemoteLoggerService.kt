package com.processout.sdk.api.service

import com.processout.sdk.api.model.request.DeviceData
import com.processout.sdk.api.model.request.TelemetryRequest
import com.processout.sdk.api.repository.TelemetryRepository
import com.processout.sdk.core.logger.BaseLoggerService
import com.processout.sdk.core.logger.LogEvent
import com.processout.sdk.core.logger.POLogLevel
import com.processout.sdk.di.ContextGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class RemoteLoggerService(
    minimumLevel: POLogLevel,
    private val scope: CoroutineScope,
    private val repository: TelemetryRepository,
    private val contextGraph: ContextGraph
) : BaseLoggerService(minimumLevel) {

    companion object {
        private const val ATTRIBUTE_LINE = "Line"
    }

    override fun log(event: LogEvent) {
        scope.launch {
            repository.send(event.toRequest(contextGraph.deviceData))
        }
    }

    private fun LogEvent.toRequest(deviceData: DeviceData) = TelemetryRequest(
        events = listOf(
            TelemetryRequest.Event(
                timestamp = timestamp.toString(),
                level = level.name.lowercase(),
                message = message,
                gatewayConfigurationId = null,
                customerId = null,
                customerTokenId = null,
                cardId = null,
                invoiceId = null,
                attributes = emptyMap()
            )
        ),
        metadata = TelemetryRequest.Metadata(
            application = TelemetryRequest.ApplicationMetadata(
                name = null,
                version = null
            ),
            device = TelemetryRequest.DeviceMetadata(
                language = deviceData.appLanguage,
                model = null,
                timeZone = deviceData.appTimeZoneOffset
            )
        )
    )
}
