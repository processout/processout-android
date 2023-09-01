package com.processout.sdk.core.retry

internal sealed class RetryStrategy(
    val maxRetries: Int,
    private val initialDelay: Long,
    private val maxDelay: Long,
    private val factor: Double
) {
    class Linear(maxRetries: Int, delay: Long) :
        RetryStrategy(maxRetries, initialDelay = delay, maxDelay = delay, factor = 1.0)

    class Exponential(maxRetries: Int, initialDelay: Long, maxDelay: Long, factor: Double) :
        RetryStrategy(maxRetries, initialDelay, maxDelay, factor)

    val iterator: Iterator<Long>
        get() = generateSequence(initialDelay) { previous ->
            (previous * factor).toLong().coerceAtMost(maxDelay)
        }.iterator()
}
