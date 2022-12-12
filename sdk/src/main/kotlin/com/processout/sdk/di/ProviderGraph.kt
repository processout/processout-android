package com.processout.sdk.di

import com.processout.sdk.api.provider.AlternativePaymentMethodProvider
import com.processout.sdk.api.provider.AlternativePaymentMethodProviderConfiguration
import com.processout.sdk.api.provider.AlternativePaymentMethodProviderImpl

internal interface ProviderGraph {
    val alternativePaymentMethodProvider: AlternativePaymentMethodProvider
}

internal class ProviderGraphImpl(
    configuration: AlternativePaymentMethodProviderConfiguration
) : ProviderGraph {

    override val alternativePaymentMethodProvider: AlternativePaymentMethodProvider =
        AlternativePaymentMethodProviderImpl(configuration)
}
