@file:Suppress("FoldInitializerAndIfToElvis")

package com.processout.example.ui.screen.card.payment

import com.processout.sdk.api.model.request.POInvoiceAuthorizationRequest
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.api.service.PO3DSService
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.core.POFailure.Code.Generic
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.card.tokenization.POCardTokenizationDelegate

class DefaultCardTokenizationDelegate(
    private val viewModel: CardPaymentViewModel,
    private val invoices: POInvoicesService,
    private val provide3DSService: () -> PO3DSService
) : POCardTokenizationDelegate {

    override suspend fun processTokenizedCard(
        card: POCard,
        saveCard: Boolean
    ): ProcessOutResult<Any> {
        val invoice = viewModel.createInvoice()
        if (invoice == null) {
            return ProcessOutResult.Failure(
                code = Generic(),
                localizedMessage = "Failed to create an invoice."
            )
        }
        return invoices.authorize(
            request = POInvoiceAuthorizationRequest(
                invoiceId = invoice.id,
                source = card.id,
                saveSource = saveCard,
                clientSecret = invoice.clientSecret
            ),
            threeDSService = provide3DSService()
        )
    }
}
