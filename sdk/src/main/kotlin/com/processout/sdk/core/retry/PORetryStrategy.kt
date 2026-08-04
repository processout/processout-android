package com.processout.sdk.core.retry

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlin.math.roundToLong

/** @suppress */
@ProcessOutInternalApi
sealed class PORetryStrategy(
    val maxRetries: Int,
    private val seedDelay: Long,
    private val minDelay: Long,
    private val maxDelay: Long,
    private val factor: Double
) {

    class Linear(
        maxRetries: Int,
        delay: Long
    ) : PORetryStrategy(
        maxRetries = maxRetries,
        seedDelay = delay,
        minDelay = delay,
        maxDelay = delay,
        factor = 1.0
    )

    class Exponential(
        maxRetries: Int,
        seedDelay: Long,
        minDelay: Long = seedDelay,
        maxDelay: Long,
        factor: Double
    ) : PORetryStrategy(
        maxRetries = maxRetries,
        seedDelay = seedDelay,
        minDelay = minDelay,
        maxDelay = maxDelay,
        factor = factor
    )

    class BackoffIterator(
        private val iterator: Iterator<Double>,
        private val minDelay: Long,
        private val maxDelay: Long
    ) : Iterator<Long> {

        override fun hasNext(): Boolean = iterator.hasNext()

        override fun next(): Long {
            return iterator.next()
                .coerceIn(minDelay.toDouble()..maxDelay.toDouble())
                .roundToLong()
        }
    }

    fun newBackoffIterator() = BackoffIterator(
        iterator = generateSequence(seed = seedDelay.toDouble()) { previous ->
            previous * factor
        }.iterator(),
        minDelay = minDelay,
        maxDelay = maxDelay
    )
}
