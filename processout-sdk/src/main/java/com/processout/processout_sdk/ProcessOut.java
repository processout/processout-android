package com.processout.processout_sdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;

/**
 * Created by jeremylejoux on 17/01/2018.
 */

public class ProcessOut {

    public static final String SDK_VERSION = "v2.11.0";

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
     * @param invoiceId Invoice ID that should be used for charging (this needs to be generated on your backend).
     *                  Keep in mind that you should set the return_url to "your_app://processout.return".
     *                  Check https://www.docs.processsout.com for more details
     * @param filter    Filter for gateway configurations
     * @param callback  Callback for listing gateway configurations
     */
    public void fetchGatewayConfigurations(@NonNull final String invoiceId, @NonNull GatewaysListingFilter filter, @NonNull final FetchGatewaysConfigurationsCallback callback) {
        final Context context = this.context;
        final String projectId = this.projectId;


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
                                g.setContext(context);
                                g.setProjectId(projectId);
                                g.setInvoiceId(invoiceId);
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
     * @param apm
     * @param customerId
     * @param tokenId
     */
    public void makeAPMToken(@NonNull GatewayConfiguration apm, @NonNull String customerId, @NonNull String tokenId) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(Network.CHECKOUT_URL + "/" + this.projectId + "/" + customerId + "/" + tokenId + "/redirect/" + apm.getId()));
        this.context.startActivity(browserIntent);
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

            Network.getInstance(this.context /* Using the same context as other network calls */, this.projectId).CallProcessOut(
                    "/invoices/" + invoiceId + "/authorize", Request.Method.POST,
                    body, new Network.NetworkResult() {
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
    public static APMTokenReturn handleAMPURLCallback(@NonNull Uri uri) {
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
