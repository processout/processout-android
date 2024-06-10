package com.processout.sdk.api.service

import com.processout.sdk.api.ProcessOutConfiguration.ApplicationInformation
import com.processout.sdk.api.model.request.DeviceData
import com.processout.sdk.api.model.request.TelemetryRequest
import com.processout.sdk.api.model.request.TelemetryRequest.*
import com.processout.sdk.api.repository.TelemetryRepository
import com.processout.sdk.core.logger.BaseLoggerService
import com.processout.sdk.core.logger.LogEvent
import com.processout.sdk.core.logger.POLogAttribute.CARD_ID
import com.processout.sdk.core.logger.POLogAttribute.CUSTOMER_ID
import com.processout.sdk.core.logger.POLogAttribute.CUSTOMER_TOKEN_ID
import com.processout.sdk.core.logger.POLogAttribute.FILE
import com.processout.sdk.core.logger.POLogAttribute.GATEWAY_CONFIGURATION_ID
import com.processout.sdk.core.logger.POLogAttribute.INVOICE_ID
import com.processout.sdk.core.logger.POLogAttribute.LINE
import com.processout.sdk.core.logger.POLogLevel
import com.processout.sdk.di.ContextGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class TelemetryService(
    minimumLevel: POLogLevel,
    private val scope: CoroutineScope,
    private val repository: TelemetryRepository,
    private val contextGraph: ContextGraph
) : BaseLoggerService(minimumLevel) {

    override fun log(event: LogEvent) {
        scope.launch {
            repository.send(
                event.toRequest(
                    deviceData = contextGraph.deviceData,
                    appInfo = contextGraph.configuration.applicationInformation
                )
            )
        }
    }

    private fun LogEvent.toRequest(
        deviceData: DeviceData,
        appInfo: ApplicationInformation?,
    ): TelemetryRequest {
        val additionalAttributes = mutableMapOf(
            FILE to simpleClassName,
            LINE to lineNumber.toString()
        )
        val standaloneAttributes = listOf(GATEWAY_CONFIGURATION_ID, CUSTOMER_ID, CUSTOMER_TOKEN_ID, CARD_ID, INVOICE_ID)
        attributes?.filterKeys { !standaloneAttributes.contains(it) }?.let {
            additionalAttributes.putAll(it)
        }
        return TelemetryRequest(
            events = listOf(
                Event(
                    timestamp = timestamp.toString(),
                    level = level.name.lowercase(),
                    message = message,
                    gatewayConfigurationId = attributes?.get(GATEWAY_CONFIGURATION_ID),
                    customerId = attributes?.get(CUSTOMER_ID),
                    customerTokenId = attributes?.get(CUSTOMER_TOKEN_ID),
                    cardId = attributes?.get(CARD_ID),
                    invoiceId = attributes?.get(INVOICE_ID),
                    attributes = additionalAttributes
                )
            ),
            metadata = Metadata(
                application = ApplicationMetadata(
                    name = appInfo?.name,
                    version = appInfo?.version
                ),
                device = DeviceMetadata(
                    language = deviceData.appLanguage,
                    model = deviceData.model,
                    timeZone = deviceData.timeZoneOffset
                )
            )
        )
    }
}
