package com.processout.sdk.ui.checkout

import androidx.compose.runtime.Immutable
import com.processout.sdk.api.model.response.POColor
import com.processout.sdk.api.model.response.POImageResource
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState

@Immutable
internal sealed interface DynamicCheckoutViewModelState {

    //region States

    data object Starting : DynamicCheckoutViewModelState

    @Immutable
    data class Started(
        val expressPayments: POImmutableList<ExpressPayment>,
        val regularPayments: POImmutableList<RegularPayment>
    ) : DynamicCheckoutViewModelState

    @Immutable
    data class Success(
        val message: String
    ) : DynamicCheckoutViewModelState

    //endregion

    @Immutable
    sealed interface ExpressPayment {
        @Immutable
        data class GooglePay(
            val id: String
        ) : ExpressPayment

        @Immutable
        data class Express(
            val id: String,
            val name: String,
            val logoResource: POImageResource,
            val brandColor: POColor
        ) : ExpressPayment
    }

    @Immutable
    data class RegularPayment(
        val id: String,
        val state: State,
        val content: Content?,
        val action: POActionState?
    ) {
        @Immutable
        data class State(
            val name: String,
            val logoResource: POImageResource,
            val description: String?,
            val loading: Boolean,
            val selectable: Boolean,
            val selected: Boolean
        )

        @Immutable
        sealed interface Content {
            data class Card(
                val state: CardTokenizationViewModelState
            ) : Content

            data class NativeAlternativePayment(
                val state: NativeAlternativePaymentViewModelState
            ) : Content
        }
    }
}
