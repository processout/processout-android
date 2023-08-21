package com.processout.sdk.core.logger

internal class POLogger private constructor() {

    companion object {
        const val ATTRIBUTE_INVOICE_ID = "InvoiceId"
        const val ATTRIBUTE_GATEWAY_CONFIGURATION_ID = "GatewayConfigurationId"

        @Volatile
        private var services = emptyArray<LoggerService>()
        private val servicesLock = mutableListOf<LoggerService>()

        fun add(service: LoggerService) {
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
            services.forEach { it.log(LogLevel.DEBUG, message, *args, attributes = attributes) }
        }

        fun info(
            message: String,
            vararg args: Any?,
            attributes: Map<String, String>? = null
        ) {
            services.forEach { it.log(LogLevel.INFO, message, *args, attributes = attributes) }
        }

        fun warn(
            message: String,
            vararg args: Any?,
            attributes: Map<String, String>? = null
        ) {
            services.forEach { it.log(LogLevel.WARN, message, *args, attributes = attributes) }
        }

        fun error(
            message: String,
            vararg args: Any?,
            attributes: Map<String, String>? = null
        ) {
            services.forEach { it.log(LogLevel.ERROR, message, *args, attributes = attributes) }
        }
    }
}
