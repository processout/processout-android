package com.processout.sdk.api.model.response

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.processout.sdk.core.util.findBy
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Transaction details.
 */
/** @suppress */
@ProcessOutInternalApi
@JsonClass(generateAdapter = true)
data class POTransaction(
    @Json(name = "status")
    val rawStatus: String
) {
    /**
     * Returns supported [Status] or [Status.UNKNOWN] otherwise.
     */
    fun status() = Status::rawStatus.findBy(rawStatus) ?: Status.UNKNOWN

    /**
     * Transaction status.
     */
    @JsonClass(generateAdapter = false)
    enum class Status(val rawStatus: String) {
        /** Waiting transaction. */
        WAITING("waiting"),

        /** Pending transaction. */
        PENDING("pending"),

        /** Pending capture transaction. */
        PENDING_CAPTURE("pending-capture"),

        /** Failed transaction. */
        FAILED("failed"),

        /** Voided transaction. */
        VOIDED("voided"),

        /** State of a chargeback transaction where the chargeback outcome hasn't been defined yet (can still be won or lost). */
        CHARGEBACK_INITIATED("chargeback-initiated"),

        /** Reversed transaction. */
        REVERSED("reversed"),

        /** Partially refunded transaction. */
        PARTIALLY_REFUNDED("partially-refunded"),

        /** Refunded transaction. */
        REFUNDED("refunded"),

        /** Solved transaction. */
        SOLVED("solved"),

        /** Authorized transaction. */
        AUTHORIZED("authorized"),

        /** Completed transaction. */
        COMPLETED("completed"),

        /** State of a transaction that has been asked for information retrieval request (part of chargeback process). */
        RETRIEVAL_REQUEST("retrieval-request"),

        /** Transaction has been flagged as fraud by issuer. */
        FRAUD_NOTIFICATION("fraud-notification"),

        /** Transaction was blocked by the merchant or an anti fraud solution. */
        BLOCKED("blocked"),

        /** Transaction is in a anti-fraud review state. */
        IN_REVIEW("in-review"),

        /** Transaction status unknown. */
        UNKNOWN(String())
    }
}
