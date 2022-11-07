package com.processout.sdk.api.repository

import androidx.annotation.RestrictTo
import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.response.*
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.ProcessOutResult

interface InvoicesRepository {

    suspend fun authorize(
        invoiceId: String,
        request: POInvoiceAuthorizationRequest
    ): ProcessOutResult<POInvoiceAuthorizeSuccess>

    fun authorize(
        invoiceId: String,
        request: POInvoiceAuthorizationRequest,
        callback: ProcessOutCallback<POInvoiceAuthorizeSuccess>
    )

    suspend fun assignCustomerToken(
        customerId: String,
        tokenId: String,
        request: POCustomerTokenRequest
    ): ProcessOutResult<POCustomerTokenSuccess>

    fun assignCustomerToken(
        customerId: String,
        tokenId: String,
        request: POCustomerTokenRequest,
        callback: ProcessOutCallback<POCustomerTokenSuccess>
    )

    suspend fun initiatePayment(
        request: PONativeAlternativePaymentMethodRequest
    ): ProcessOutResult<PONativeAlternativePaymentMethod>

    fun initiatePayment(
        request: PONativeAlternativePaymentMethodRequest,
        callback: ProcessOutCallback<PONativeAlternativePaymentMethod>
    )

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    suspend fun createInvoice(request: POCreateInvoiceRequest): ProcessOutResult<POInvoice>

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    suspend fun createCustomer(request: POCreateCustomerRequest): ProcessOutResult<POCustomer>

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    suspend fun createCustomerToken(
        customerId: String,
    ): ProcessOutResult<POCustomerTokenSuccess>

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun createCustomerToken(
        customerId: String,
        callback: ProcessOutCallback<POCustomerTokenSuccess>
    )
}
