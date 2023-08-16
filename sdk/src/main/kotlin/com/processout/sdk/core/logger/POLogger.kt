package com.processout.sdk.core.logger

internal class POLogger private constructor() {

    companion object {
        @Volatile
        private var destinations = emptyArray<LoggerDestination>()
        private val destinationsLock = mutableListOf<LoggerDestination>()

        fun add(destination: LoggerDestination) {
            synchronized(destinationsLock) {
                destinationsLock.add(destination)
                destinations = destinationsLock.toTypedArray()
            }
        }

        fun debug(
            message: String,
            vararg args: Any?,
            attributes: Map<String, String>? = null
        ) {
            destinations.forEach { it.log(LogLevel.DEBUG, message, *args, attributes = attributes) }
        }

        fun info(
            message: String,
            vararg args: Any?,
            attributes: Map<String, String>? = null
        ) {
            destinations.forEach { it.log(LogLevel.INFO, message, *args, attributes = attributes) }
        }

        fun warn(
            message: String,
            vararg args: Any?,
            attributes: Map<String, String>? = null
        ) {
            destinations.forEach { it.log(LogLevel.WARN, message, *args, attributes = attributes) }
        }

        fun error(
            message: String,
            vararg args: Any?,
            attributes: Map<String, String>? = null
        ) {
            destinations.forEach { it.log(LogLevel.ERROR, message, *args, attributes = attributes) }
        }
    }
}
