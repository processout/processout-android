# Module ProcessOut Android SDK

## Migration From v3 To v4

### Breaking changes

- `ProcessOutApi` -> `ProcessOut`\
  `ProcessOutApiConfiguration` -> `ProcessOutConfiguration`
- `GatewayConfigurationsRepository` -> `POGatewayConfigurationsRepository`\
  `CardsRepository` -> `POCardsRepository`\
  `InvoicesRepository` -> `POInvoicesService`\
  `CustomerTokensRepository` -> `POCustomerTokensService`\
  `AlternativePaymentMethodProvider` -> `POAlternativePaymentMethodsService`\
  `NativeAlternativePaymentMethodEventDispatcher` -> `PONativeAlternativePaymentMethodEventDispatcher`
- `PONativeAlternativePaymentMethodParameter.ParameterType` enum values has been renamed and extended.
```
// Access type:
val parameter: PONativeAlternativePaymentMethodParameter
val type = parameter.type()
val rawType = parameter.rawType
```
