package com.processout.sdk.core.retry

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlin.math.roundToLong

/** @suppress */
@ProcessOutInternalApi
sealed class PORetryStrategy(
    val maxRetries: Int,
    private val initialDelay: Long,
    private val minDelay: Long,
    private val maxDelay: Long,
    private val factor: Double
) {

    class Linear(
        maxRetries: Int,
        delay: Long
    ) : PORetryStrategy(
        maxRetries = maxRetries,
        initialDelay = delay,
        minDelay = delay,
        maxDelay = delay,
        factor = 1.0
    )

    class Exponential(
        maxRetries: Int,
        initialDelay: Long,
        minDelay: Long = initialDelay,
        maxDelay: Long,
        factor: Double
    ) : PORetryStrategy(
        maxRetries = maxRetries,
        initialDelay = initialDelay,
        minDelay = minDelay,
        maxDelay = maxDelay,
        factor = factor
    )

    class Iterator(
        private val iterator: kotlin.collections.Iterator<Double>,
        private val minDelay: Long,
        private val maxDelay: Long
    ) : kotlin.collections.Iterator<Long> {

        override fun hasNext(): Boolean = iterator.hasNext()

        override fun next(): Long {
            return iterator.next()
                .coerceIn(minDelay.toDouble()..maxDelay.toDouble())
                .roundToLong()
        }
    }

    val iterator: Iterator
        get() = Iterator(
            iterator = generateSequence(initialDelay.toDouble()) { previous ->
                previous * factor
            }.iterator(),
            minDelay = minDelay,
            maxDelay = maxDelay
        )
}
