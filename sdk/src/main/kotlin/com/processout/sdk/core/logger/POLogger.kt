package com.processout.sdk.core.logger

import com.processout.sdk.core.annotation.ProcessOutInternalApi

@ProcessOutInternalApi
class POLogger private constructor() {

    companion object {
        internal const val ATTRIBUTE_LINE = "Line"
        internal const val ATTRIBUTE_INVOICE_ID = "InvoiceId"
        internal const val ATTRIBUTE_GATEWAY_CONFIGURATION_ID = "GatewayConfigurationId"

        @Volatile
        private var services = emptyArray<POLoggerService>()
        private val servicesLock = mutableListOf<POLoggerService>()

        fun add(service: POLoggerService) {
            synchronized(servicesLock) {
                servicesLock.add(service)
                services = servicesLock.toTypedArray()
            }
        }

        fun debug(
            message: String,
            vararg args: Any?,
            attributes: Map<String, String>? = null
        ) {
            services.forEach { it.log(POLogLevel.DEBUG, message, *args, attributes = attributes) }
        }

        fun info(
            message: String,
            vararg args: Any?,
            attributes: Map<String, String>? = null
        ) {
            services.forEach { it.log(POLogLevel.INFO, message, *args, attributes = attributes) }
        }

        fun warn(
            message: String,
            vararg args: Any?,
            attributes: Map<String, String>? = null
        ) {
            services.forEach { it.log(POLogLevel.WARN, message, *args, attributes = attributes) }
        }

        fun error(
            message: String,
            vararg args: Any?,
            attributes: Map<String, String>? = null
        ) {
            services.forEach { it.log(POLogLevel.ERROR, message, *args, attributes = attributes) }
        }
    }
}
