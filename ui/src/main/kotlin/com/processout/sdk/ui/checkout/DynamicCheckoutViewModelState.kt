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

    @Immutable
    data class Starting(
        val cancelAction: POActionState?
    ) : DynamicCheckoutViewModelState

    @Immutable
    data class Started(
        val expressPayments: POImmutableList<ExpressPayment>,
        val regularPayments: POImmutableList<RegularPayment>,
        val cancelAction: POActionState?,
        val errorMessage: String? = null
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
            val id: String,
            val submitActionId: String
        ) : ExpressPayment

        @Immutable
        data class Express(
            val id: String,
            val name: String,
            val logoResource: POImageResource,
            val brandColor: POColor,
            val submitActionId: String
        ) : ExpressPayment
    }

    @Immutable
    data class RegularPayment(
        val id: String,
        val state: State,
        val content: Content?,
        val submitAction: POActionState?
    ) {
        @Immutable
        data class State(
            val name: String,
            val logoResource: POImageResource,
            val description: String?,
            val loading: Boolean,
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
