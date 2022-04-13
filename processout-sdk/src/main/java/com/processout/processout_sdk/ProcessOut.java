package com.processout.processout_sdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.processout.processout_sdk.POWebViews.CardTokenWebView;
import com.processout.processout_sdk.POWebViews.PaymentWebView;
import com.processout.processout_sdk.POWebViews.ProcessOutWebView;
import com.processout.processout_sdk.ProcessOutExceptions.ProcessOutException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jeremylejoux on 17/01/2018.
 */

public class ProcessOut {

    public static final String SDK_VERSION = "v2.15.0";

    private String projectId;
    private Context context;
    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public ProcessOut(Context context, String projectId) {
        this.projectId = projectId;
        this.context = context;
    }

    /**
     * Returns a card token that can be used to create a charge
     *
     * @param card     Card to be tokenized
     * @param callback Tokenization callback
     * @deprecated This method doesn't support metadatas
     */
    public void tokenize(@NonNull Card card, @NonNull final TokenCallback callback) {
        tokenizeBase(card, null, callback);
    }

    /**
     * Returns a card token that can be used to create a charge
     *
     * @param card     Card to be tokenized
     * @param metadata JSONObject containing metadatas to be stored during tokenization
     * @param callback Tokenization callback
     */
    public void tokenize(@NonNull Card card, JSONObject metadata, @NonNull final TokenCallback callback) {
        tokenizeBase(card, metadata, callback);
    }


    private void tokenizeBase(@NonNull Card card, JSONObject metadata, @NonNull final TokenCallback callback) {
        JSONObject body = null;
        try {
            body = new JSONObject(gson.toJson(card));
            if (metadata != null)
                body.put("metadata", metadata);
            Network.getInstance(this.context, this.projectId).CallProcessOut("/cards", Request.Method.POST, body, new Network.NetworkResult() {
                @Override
                public void onError(Exception error) {
                    callback.onError(error);
                }

                @Override
                public void onSuccess(JSONObject json) {
                    try {
                        JSONObject card = (JSONObject) json.get("card");
                        callback.onSuccess(card.get("id").toString());
                    } catch (JSONException e) {
                        callback.onError(e);
                    }
                }
            });
        } catch (JSONException e) {
            callback.onError(e);
        }
    }

    /**
     * Updates the CVC of a  previously stored card
     *
     * @param card     Card object containing the new CVC
     * @param callback Update callback
     */
    public void updateCvc(@NonNull Card card, @NonNull final CvcUpdateCallback callback) {
        JSONObject body = null;

        try {
            body = new JSONObject(gson.toJson(card));
            Network.getInstance(
                    this.context, this.projectId).CallProcessOut(
                    "/cards/" + card.getId(),
                    Request.Method.PUT,
                    body, new Network.NetworkResult() {
                        @Override
                        public void onError(Exception error) {
                            callback.onError(error);
                        }

                        @Override
                        public void onSuccess(JSONObject json) {
                            callback.onSuccess();
                        }
                    });
        } catch (JSONException e) {
            callback.onError(e);
        }
    }


    public enum GatewaysListingFilter {
        All,
        AlternativePaymentMethods,
        AlternativePaymentMethodWithTokenization
    }

    /**
     * Retrieves the list of gateway configurations
     *
     * @param filter   Filter for gateway configurations
     * @param callback Callback for listing gateway configurations
     */
    public void fetchGatewayConfigurations(@NonNull GatewaysListingFilter filter, @NonNull final FetchGatewaysConfigurationsCallback callback) {

        String filterValue;
        switch (filter) {
            case AlternativePaymentMethods:
                filterValue = "alternative-payment-methods";
                break;
            case AlternativePaymentMethodWithTokenization:
                filterValue = "alternative-payment-methods-with-tokenization";
                break;
            default:
                filterValue = "";
        }

        Network.getInstance(this.context, this.projectId).CallProcessOut(
                "/gateway-configurations?filter=" + filterValue + "&expand_merchant_accounts=true",
                Request.Method.GET, null, new Network.NetworkResult() {
                    @Override
                    public void onError(Exception error) {
                        callback.onError(error);
                    }

                    @Override
                    public void onSuccess(JSONObject json) {
                        try {
                            JSONArray configs = json.getJSONArray("gateway_configurations");

                            ArrayList<GatewayConfiguration> gways = new ArrayList<>();
                            for (int i = 0; i < configs.length(); i++) {
                                GatewayConfiguration g = gson.fromJson(
                                        configs.getJSONObject(i).toString(), GatewayConfiguration.class);
                                gways.add(g);
                            }

                            callback.onSuccess(gways);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError(e);
                        }
                    }
                });
    }

    /**
     * Returns the URL to redirect the user to an alternative payment method payment page to authorize a token
     *
     * @param apm            Gateway previously retrieved
     * @param customerId     Customer ID created on your backend
     * @param tokenId        Customer token ID created on your backend with empty source
     * @param additionalData AdditionalData to send to the APM
     */
    public Uri makeAPMToken(@NonNull GatewayConfiguration apm, @NonNull String customerId, @NonNull String tokenId, @NonNull Map<String, String> additionalData, @NonNull Context with) {
        // Build the URL
        String baseUrl = Network.CHECKOUT_URL + "/" + this.projectId;
        String checkout = "/" + customerId + "/" + tokenId + "/redirect/" + apm.getId();
        String globalUrl = baseUrl + checkout;

        // Buld the additionalData string
        String additionalDataString = generateAdditionalDataString(additionalData);
        if (!additionalData.isEmpty()) {
            // Add it if it's not empty
            globalUrl += "?" + additionalDataString;
        }

        return Uri.parse(globalUrl);
    }

    /**
     * Returns the URL to redirect the user to an alternative payment method payment page to authorize a token
     *
     * @param apm        Gateway previously retrieved
     * @param customerId Customer ID created on your backend
     * @param tokenId    Customer token ID created on your backend with empty source
     */
    public Uri makeAPMToken(@NonNull GatewayConfiguration apm, @NonNull String customerId, @NonNull String tokenId, @NonNull Context with) {
        // Calling makeAPMToken with an empty additionalData parameter
        return makeAPMToken(apm, customerId, tokenId, new HashMap<String, String>(), with);
    }

    /**
     * Returns the URL to redirect the user to an alternative payment method payment page to complete a payment
     *
     * @param apm            Gateway previously retrieved
     * @param invoiceId      Invoice created on your backend
     * @param additionalData AdditionalData to send to the APM
     */

    public Uri makeAPMPayment(@NonNull GatewayConfiguration apm, @NonNull String invoiceId, @NonNull Map<String, String> additionalData, @NonNull Context with) {
        // Build the URL
        String baseUrl = Network.CHECKOUT_URL + "/" + this.projectId;
        String checkout = "/" + invoiceId + "/redirect/" + apm.getId();
        String globalUrl = baseUrl + checkout;

        // Build the additionalData string
        String additionalDataString = generateAdditionalDataString(additionalData);
        if (!additionalData.isEmpty()) {
            // Add it if it's not empty
            globalUrl += "?" + additionalDataString;
        }

        return Uri.parse((globalUrl));
    }

    /**
     * Returns the URL to redirect the user to an alternative payment method payment page to complete a payment
     *
     * @param apm       Gateway previously retrieved
     * @param invoiceId Invoice created on your backend
     */
    public Uri makeAPMPayment(@NonNull GatewayConfiguration apm, @NonNull String invoiceId, @NonNull Context with) {
        // Call makeAPMPayment with empty additionalData parameter
        return makeAPMPayment(apm, invoiceId, new HashMap<String, String>(), with);
    }

    /**
     * Allow card payments authorization (with 3DS2 support)
     *
     * @param invoiceId previously generated invoice
     * @param source    source to use for the charge (card token, etc.)
     * @param thirdPartySDKVersion version of the 3rd party SDK being used for the calls.
     * @param handler   (Custom 3DS2 handler)
     */
    public void makeCardPayment(@NonNull final String invoiceId, @NonNull final String source, final String thirdPartySDKVersion, @NonNull final ThreeDSHandler handler, @NonNull final Context with) {
        try {
            // Generate the authorization body and forces 3DS2
            AuthorizationRequest authRequest = new AuthorizationRequest(source, thirdPartySDKVersion);
            final JSONObject body = new JSONObject(gson.toJson(authRequest));

            requestAuthorization(invoiceId, body, new RequestAuthorizationCallback() {
                @Override
                public void onError(Exception error) {
                    handler.onError(error);
                }

                @Override
                public void onSuccess(JSONObject json) {
                    // Handle the authorization result
                    AuthorizationResult result = gson.fromJson(
                            json.toString(), AuthorizationResult.class);

                    CustomerAction cA = result.getCustomerAction();
                    if (cA == null) {
                        // No customer action in the authorization result, we return the invoice id
                        handler.onSuccess(invoiceId);
                        return;
                    }

                    CustomerActionHandler customerActionHandler = new CustomerActionHandler(handler, new PaymentWebView(with), with, new CustomerActionHandler.CustomerActionCallback() {
                        @Override
                        public void shouldContinue(String source) {
                            makeCardPayment(invoiceId, source, thirdPartySDKVersion, handler, with);
                        }
                    });
                    customerActionHandler.handleCustomerAction(cA);
                }
            });
        } catch (JSONException e) {
            handler.onError(e);
        }
    }

    /**
     * Allow card payments authorization (with 3DS2 support)
     *
     * @param invoiceId previously generated invoice
     * @param source    source to use for the charge (card token, etc.)
     * @param handler   (Custom 3DS2 handler)
     */
    public void makeCardPayment(@NonNull final String invoiceId, @NonNull final String source, @NonNull final ThreeDSHandler handler, @NonNull final Context with) {
        try {
            // Generate the authorization body and forces 3DS2
            AuthorizationRequest authRequest = new AuthorizationRequest(source);
            final JSONObject body = new JSONObject(gson.toJson(authRequest));

            requestAuthorization(invoiceId, body, new RequestAuthorizationCallback() {
                @Override
                public void onError(Exception error) {
                    handler.onError(error);
                }

                @Override
                public void onSuccess(JSONObject json) {
                    // Handle the authorization result
                    AuthorizationResult result = gson.fromJson(
                            json.toString(), AuthorizationResult.class);

                    CustomerAction cA = result.getCustomerAction();
                    if (cA == null) {
                        // No customer action in the authorization result, we return the invoice id
                        handler.onSuccess(invoiceId);
                        return;
                    }

                    CustomerActionHandler customerActionHandler = new CustomerActionHandler(handler, new PaymentWebView(with), with, new CustomerActionHandler.CustomerActionCallback() {
                        @Override
                        public void shouldContinue(String source) {
                            makeCardPayment(invoiceId, source, handler, with);
                        }
                    });
                    customerActionHandler.handleCustomerAction(cA);
                }
            });
        } catch (JSONException e) {
            handler.onError(e);
        }
    }

    /**
     * Allow card payments authorization and marks the authorization as incremental (with 3DS2 support)
     *
     * @param invoiceId previously generated invoice
     * @param source    source to use for the charge (card token, etc.)
     * @param handler   (Custom 3DS2 handler)
     */
    public void makeIncrementalAuthorizationPayment(@NonNull final String invoiceId, @NonNull final String source, @NonNull final ThreeDSHandler handler, @NonNull final Context with) {
        try {
            // Generate the authorization body and forces 3DS2
            AuthorizationRequest authRequest = new AuthorizationRequest(source, true);
            final JSONObject body = new JSONObject(gson.toJson(authRequest));

            requestAuthorization(invoiceId, body, new RequestAuthorizationCallback() {
                @Override
                public void onError(Exception error) {
                    handler.onError(error);
                }

                @Override
                public void onSuccess(JSONObject json) {
                    // Handle the authorization result
                    AuthorizationResult result = gson.fromJson(
                            json.toString(), AuthorizationResult.class);

                    CustomerAction cA = result.getCustomerAction();
                    if (cA == null) {
                        // No customer action in the authorization result, we return the invoice id
                        handler.onSuccess(invoiceId);
                        return;
                    }

                    CustomerActionHandler customerActionHandler = new CustomerActionHandler(handler, new PaymentWebView(with), with, new CustomerActionHandler.CustomerActionCallback() {
                        @Override
                        public void shouldContinue(String source) {
                            makeIncrementalAuthorizationPayment(invoiceId, source, handler, with);
                        }
                    });
                    customerActionHandler.handleCustomerAction(cA);
                }
            });
        } catch (JSONException e) {
            handler.onError(e);
        }
    }

    /**
     * Allow card payments authorization and marks the authorization as incremental (with 3DS2 support)
     *
     * @param invoiceId previously generated invoice
     * @param source    source to use for the charge (card token, etc.)
     * @param thirdPartySDKVersion version of the 3rd party SDK being used for the calls.
     * @param handler   (Custom 3DS2 handler)
     */
    public void makeIncrementalAuthorizationPayment(@NonNull final String invoiceId, @NonNull final String source, final String thirdPartySDKVersion, @NonNull final ThreeDSHandler handler, @NonNull final Context with) {
        try {
            // Generate the authorization body and forces 3DS2
            AuthorizationRequest authRequest = new AuthorizationRequest(source, true, thirdPartySDKVersion);
            final JSONObject body = new JSONObject(gson.toJson(authRequest));

            requestAuthorization(invoiceId, body, new RequestAuthorizationCallback() {
                @Override
                public void onError(Exception error) {
                    handler.onError(error);
                }

                @Override
                public void onSuccess(JSONObject json) {
                    // Handle the authorization result
                    AuthorizationResult result = gson.fromJson(
                            json.toString(), AuthorizationResult.class);

                    CustomerAction cA = result.getCustomerAction();
                    if (cA == null) {
                        // No customer action in the authorization result, we return the invoice id
                        handler.onSuccess(invoiceId);
                        return;
                    }

                    CustomerActionHandler customerActionHandler = new CustomerActionHandler(handler, new PaymentWebView(with), with, new CustomerActionHandler.CustomerActionCallback() {
                        @Override
                        public void shouldContinue(String source) {
                            makeIncrementalAuthorizationPayment(invoiceId, source, handler, with);
                        }
                    });
                    customerActionHandler.handleCustomerAction(cA);
                }
            });
        } catch (JSONException e) {
            handler.onError(e);
        }
    }

    /**
     * Increments the authorization of an applicable invoice by a given amount
     *
     * @param invoiceId previously generated invoice
     * @param amount    amount by which the authorization should be incremented
     * @param handler   (Custom 3DS2 handler)
     */
    public void incrementAuthorizationAmount(@NonNull final String invoiceId, @NonNull final int amount, @NonNull final ThreeDSHandler handler) {
        try {
            IncrementAuthorizationRequest request = new IncrementAuthorizationRequest(amount);
            final JSONObject body = new JSONObject(gson.toJson(request));

            Network.getInstance(this.context, this.projectId).CallProcessOut(
                    "/invoices/" + invoiceId + "/increment_authorization", Request.Method.POST,
                    body, new Network.NetworkResult() {
                        @Override
                        public void onError(Exception error) {
                            handler.onError(error);
                        }

                        @Override
                        public void onSuccess(JSONObject json) {
                            handler.onSuccess(invoiceId);
                        }
                    }
            );
        } catch (JSONException e) {
            handler.onError(e);
        }
    }

    /**
     * Create a customer token from a card ID
     *
     * @param source     Card ID used for the customer token
     * @param customerId Customer ID created in backend
     * @param tokenId    Token ID created in backend
     * @param handler    3DS2 handler
     * @param with       Activity to display webviews and perform fingerprinting
     */
    public void makeCardToken(@NonNull final String source, @NonNull final String customerId, @NonNull final String tokenId, @NonNull final ThreeDSHandler handler, @NonNull final Context with) {
        try {
            TokenRequest request = new TokenRequest(source);
            final JSONObject body = new JSONObject(gson.toJson(request));

            Network.getInstance(this.context, this.projectId).CallProcessOut("/customers/" + customerId + "/tokens/" + tokenId, Request.Method.PUT, body, new Network.NetworkResult() {
                @Override
                public void onError(Exception error) {
                    handler.onError(error);
                }

                @Override
                public void onSuccess(JSONObject json) {
                    // Handle the authorization result
                    AuthorizationResult result = gson.fromJson(
                            json.toString(), AuthorizationResult.class);

                    CustomerAction cA = result.getCustomerAction();
                    if (cA == null) {
                        // No customer action in the authorization result, we return the invoice id
                        handler.onSuccess(tokenId);
                        return;
                    }

                    CustomerActionHandler customerActionHandler = new CustomerActionHandler(handler, new CardTokenWebView(with), with, new CustomerActionHandler.CustomerActionCallback() {
                        @Override
                        public void shouldContinue(String source) {
                            makeCardToken(source, customerId, tokenId, handler, with);
                        }
                    });
                    customerActionHandler.handleCustomerAction(cA);
                }
            });
        } catch (JSONException e) {
            handler.onError(e);
        }
    }

    /**
     * Create a customer token from a card ID
     *
     * @param source     Card ID used for the customer token
     * @param customerId Customer ID created in backend
     * @param tokenId    Token ID created in backend
     * @param thirdPartySDKVersion version of the 3rd party SDK being used for the calls.
     * @param handler    3DS2 handler
     * @param with       Activity to display webviews and perform fingerprinting
     */
    public void makeCardToken(@NonNull final String source, @NonNull final String customerId, @NonNull final String tokenId, final String thirdPartySDKVersion, @NonNull final ThreeDSHandler handler, @NonNull final Context with) {
        try {
            TokenRequest request = new TokenRequest(source, thirdPartySDKVersion);
            final JSONObject body = new JSONObject(gson.toJson(request));

            Network.getInstance(this.context, this.projectId).CallProcessOut("/customers/" + customerId + "/tokens/" + tokenId, Request.Method.PUT, body, new Network.NetworkResult() {
                @Override
                public void onError(Exception error) {
                    handler.onError(error);
                }

                @Override
                public void onSuccess(JSONObject json) {
                    // Handle the authorization result
                    AuthorizationResult result = gson.fromJson(
                            json.toString(), AuthorizationResult.class);

                    CustomerAction cA = result.getCustomerAction();
                    if (cA == null) {
                        // No customer action in the authorization result, we return the invoice id
                        handler.onSuccess(tokenId);
                        return;
                    }

                    CustomerActionHandler customerActionHandler = new CustomerActionHandler(handler, new CardTokenWebView(with), with, new CustomerActionHandler.CustomerActionCallback() {
                        @Override
                        public void shouldContinue(String source) {
                            makeCardToken(source, customerId, tokenId, handler, with);
                        }
                    });
                    customerActionHandler.handleCustomerAction(cA);
                }
            });
        } catch (JSONException e) {
            handler.onError(e);
        }
    }

    /**
     * Parses a intent uri
     *
     * @param uri Uri from a deep-link app opening
     * @return The gateway token if available, null otherwise
     * @deprecated Use handleAPMURLCallback instead
     */
    public static String handleURLCallback(@NonNull Uri uri) {
        if (uri.getHost().matches("processout.return"))
            return uri.getQueryParameter("token");

        return null;
    }

    /**
     * Parses an intent uri. Either for an APM payment return or after an makeAPMToken call
     *
     * @param uri Uri from a deep-link app opening
     * @return Null if the URL is not a correct processout url.
     * An APMTokenReturn object containing the customerId, tokenId and new token source
     * to update the customer token from your backend otherwise
     */
    public static APMTokenReturn handleAPMURLCallback(@NonNull Uri uri) {
        if (!uri.getHost().matches("processout.return")) {
            return null;
        }

        String token = uri.getQueryParameter("token");
        String customerId = uri.getQueryParameter("customer_id");
        String tokenId = uri.getQueryParameter("token_id");

        // if not check if we have a token
        if (token == null || token.isEmpty()) {
            // No parameter token is available
            return new APMTokenReturn(new ProcessOutException("Missing APM token in return paramaters"));
        }

        // Check if we have a customer id and token id
        if (customerId != null && tokenId != null && !customerId.isEmpty() && !tokenId.isEmpty()) {
            // Case of token creation
            return new APMTokenReturn(token, customerId, tokenId);
        }

        // Case of simple APM authorization
        return new APMTokenReturn(token);
    }

    public interface ThreeDSHandlerTestCallback {
        void onSuccess(String invoiceId);

        void onError(Exception error);
    }

    private static void destroyTestWebView(@NonNull ProcessOutWebView webView) {
        ((ViewGroup) webView.getParent()).removeView(webView);
        webView.removeAllViews();
        webView.clearHistory();
        webView.clearCache(true);
        webView.onPause();
        webView.destroy();

    }

    /**
     * @param additionalData additionalData to be sent to an APM
     * @return a query parameter String containing the additionalData for an APM
     */
    private String generateAdditionalDataString(@NonNull Map<String, String> additionalData) {
        // String builder
        StringBuilder builder = new StringBuilder("");

        // Loop over every key value
        for (String key : additionalData.keySet()) {
            // If the builder is not empty we concat with a &
            if (builder.length() > 0) {
                builder.append("&");
            }

            // Try to encode the value
            String encodedValue;
            try {
                encodedValue = URLEncoder.encode(additionalData.get(key), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // We default to an empty value in case the encoding fails
                encodedValue = "";
            }

            // append the key value generated
            builder.append("additional_data[" + key + "]=" + encodedValue);
        }

        // Return the builder completion
        return builder.toString();
    }

    /**
     * Requests an authorization for a specified invoice
     *
     * @param invoiceId previously generated invoice
     * @param body      the request body
     * @param callback  callback for handling customer action
     */
    private void requestAuthorization(@NonNull final String invoiceId, @NonNull final JSONObject body, @NonNull final RequestAuthorizationCallback callback) {
        Network.getInstance(this.context /* Using the same context as other network calls */, this.projectId).CallProcessOut(
                "/invoices/" + invoiceId + "/authorize", Request.Method.POST,
                body, new Network.NetworkResult() {
                    @Override
                    public void onError(Exception error) {
                        callback.onError(error);
                    }

                    @Override
                    public void onSuccess(JSONObject json) {
                        callback.onSuccess(json);
                    }
                });
    }

    /**
     * Generate a test ThreeDSHandler for 3DS2 challenges
     *
     * @param context Application context
     * @return
     */
    public static ThreeDSHandler createDefaultTestHandler(final Context context, final ThreeDSHandlerTestCallback callback) {
        final ProcessOutWebView[] w = new ProcessOutWebView[1];
        return new ThreeDSHandler() {
            @Override
            public void doFingerprint(DirectoryServerData directoryServerData, DoFingerprintCallback callback) {
                callback.continueCallback(
                        new ThreeDSFingerprintResponse(
                                "", "", new SDKEPhemPubKey("", "", "", ""),
                                "", ""));
            }

            @Override
            public void doChallenge(AuthenticationChallengeData authData, final DoChallengeCallback callback) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.app_name);
                builder.setMessage("Validate mobile 3DS2 challenge?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        callback.success();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        callback.error();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }

            @Override
            public void doPresentWebView(final ProcessOutWebView webView) {
                w[0] = webView;
                Activity a = (Activity) context;
                FrameLayout rootLayout = a.findViewById(android.R.id.content);
                rootLayout.addView(webView);
            }

            @Override
            public void onSuccess(String id) {
                // We check if a webview was required, if so we remove it.
                if (w[0] != null) {
                    destroyTestWebView(w[0]);
                }
                callback.onSuccess(id);
            }

            @Override
            public void onError(Exception error) {
                if (w[0] != null) {
                    destroyTestWebView(w[0]);
                }
                callback.onError(error);
            }
        };
    }
}
