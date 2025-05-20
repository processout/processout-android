package com.processout.sdk.di

import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.*
import com.processout.sdk.api.model.response.napm.v2.NativeAlternativePaymentAuthorizationResponseBody.NextStep
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse.CustomerInstruction
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse.NextStep.SubmitData.Parameter
import com.processout.sdk.api.network.*
import com.processout.sdk.api.network.interceptor.BasicAuthInterceptor
import com.processout.sdk.api.network.interceptor.RetryInterceptor
import com.processout.sdk.api.network.interceptor.UserAgentInterceptor
import com.processout.sdk.api.preferences.Preferences
import com.processout.sdk.core.logger.POLogger
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Date
import java.util.concurrent.TimeUnit

internal interface NetworkGraph {
    val moshi: Moshi
    val gatewayConfigurationsApi: GatewayConfigurationsApi
    val invoicesApi: InvoicesApi
    val cardsApi: CardsApi
    val customerTokensApi: CustomerTokensApi
    val telemetryApi: TelemetryApi
}

internal class DefaultNetworkGraph(
    contextGraph: ContextGraph,
    preferences: Preferences,
    baseUrl: String,
    sdkVersion: String
) : NetworkGraph {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(BasicAuthInterceptor(contextGraph))
            .addInterceptor(UserAgentInterceptor(contextGraph, preferences, sdkVersion))
            .addInterceptor(RetryInterceptor())
            .addInterceptor(HttpLoggingInterceptor { message ->
                if (contextGraph.configuration.debug) {
                    POLogger.debug(message)
                }
            }.apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()
    }

    override val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .add(
                PolymorphicJsonAdapterFactory.of(NextStep::class.java, "type")
                    .withSubtype(NextStep.SubmitData::class.java, "submit_data")
                    .withSubtype(NextStep.Redirect::class.java, "redirect")
                    .withDefaultValue(NextStep.Unknown)
            ).add(
                PolymorphicJsonAdapterFactory.of(Parameter::class.java, "type")
                    .withSubtype(Parameter.Text::class.java, "text")
                    .withSubtype(Parameter.SingleSelect::class.java, "single-select")
                    .withSubtype(Parameter.Bool::class.java, "boolean")
                    .withSubtype(Parameter.Digits::class.java, "digits")
                    .withSubtype(Parameter.Phone::class.java, "phone")
                    .withSubtype(Parameter.Email::class.java, "email")
                    .withSubtype(Parameter.Card::class.java, "card")
                    .withSubtype(Parameter.Otp::class.java, "otp")
                    .withDefaultValue(Parameter.Unknown)
            ).add(
                PolymorphicJsonAdapterFactory.of(CustomerInstruction::class.java, "type")
                    .withSubtype(CustomerInstruction.Text::class.java, "text")
                    .withSubtype(CustomerInstruction.Image::class.java, "image_url")
                    .withSubtype(CustomerInstruction.Barcode::class.java, "barcode")
                    .withSubtype(CustomerInstruction.Group::class.java, "group")
                    .withDefaultValue(CustomerInstruction.Unknown)
            ).add(
                PolymorphicJsonAdapterFactory.of(PODynamicCheckoutPaymentMethod::class.java, "type")
                    .withSubtype(Card::class.java, "card")
                    .withSubtype(CardCustomerToken::class.java, "card_customer_token")
                    .withSubtype(GooglePay::class.java, "googlepay")
                    .withSubtype(AlternativePayment::class.java, "apm")
                    .withSubtype(AlternativePaymentCustomerToken::class.java, "apm_customer_token")
                    .withDefaultValue(Unknown)
            ).build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    override val gatewayConfigurationsApi: GatewayConfigurationsApi by lazy {
        retrofit.create(GatewayConfigurationsApi::class.java)
    }

    override val invoicesApi: InvoicesApi by lazy {
        retrofit.create(InvoicesApi::class.java)
    }

    override val cardsApi: CardsApi by lazy {
        retrofit.create(CardsApi::class.java)
    }

    override val customerTokensApi: CustomerTokensApi by lazy {
        retrofit.create(CustomerTokensApi::class.java)
    }

    override val telemetryApi: TelemetryApi by lazy {
        retrofit.create(TelemetryApi::class.java)
    }
}
