package com.processout.example.ui.screen.card.payment

data class CardPaymentViewModelState(
    val amount: String = String(),
    val currency: String = String(),
    val invoiceId: String? = null,
    val customerId: String? = null
)

sealed interface CardPaymentViewModelEvent {
    data object LaunchTokenization : CardPaymentViewModelEvent
}
