package com.processout.example.ui.screen.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.processout.sdk.api.ProcessOut

class CardPaymentViewModel(

) : ViewModel() {

    class Factory(

    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            with(ProcessOut.instance) {
                CardPaymentViewModel(

                ) as T
            }
    }
}
