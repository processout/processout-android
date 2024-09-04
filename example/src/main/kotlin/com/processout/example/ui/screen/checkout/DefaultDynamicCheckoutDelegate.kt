package com.processout.example.ui.screen.checkout

import com.processout.example.shared.Constants
import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.model.request.PODynamicCheckoutInvoiceInvalidationReason
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.api.model.response.POInvoice
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.core.fold
import com.processout.sdk.ui.checkout.PODynamicCheckoutDelegate
import java.util.UUID

class DefaultDynamicCheckoutDelegate(
    private val invoices: POInvoicesService
) : PODynamicCheckoutDelegate {

    var customerId: String? = null

    override suspend fun newInvoice(
        currentInvoice: POInvoice,
        invalidationReason: PODynamicCheckoutInvoiceInvalidationReason
    ): POInvoiceRequest? {
        return createInvoice(
            InvoiceDetails(
                amount = "3",
                currency = "EUR"
            )
        ).fold(
            onSuccess = { invoice ->
                POInvoiceRequest(
                    invoiceId = invoice.id,
                    clientSecret = invoice.clientSecret
                )
            },
            onFailure = { null }
        )
    }

    private suspend fun createInvoice(details: InvoiceDetails) =
        invoices.createInvoice(
            POCreateInvoiceRequest(
                name = UUID.randomUUID().toString(),
                amount = details.amount,
                currency = details.currency,
                customerId = customerId,
                returnUrl = Constants.RETURN_URL
            )
        )
}
