package com.processout.sdk.ui.card.update

import android.os.Parcelable
import com.processout.sdk.ui.shared.configuration.POCancellationConfiguration
import kotlinx.parcelize.Parcelize

@Parcelize
data class POCardUpdateConfiguration(
    val cardId: String,
    val options: Options = Options()
) : Parcelable {

    @Parcelize
    data class Options(
        val cancellation: POCancellationConfiguration = POCancellationConfiguration()
    ) : Parcelable
}
