package com.processout.processout_sdk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Base64;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by jeremylejoux on 17/01/2018.
 */

public class ProcessOut {

    private String projectId;
    private Context context;
    private String ThreeDS2ChallengeSuccess = "gway_req_eyJib2R5Ijoie1widHJhbnNTdGF0dXNcIjpcIllcIn0ifQ==";
    private String ThreeDS2ChallengeError = "gway_req_eyJib2R5Ijoie1widHJhbnNTdGF0dXNcIjpcIk5cIn0ifQ==";

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
    public void tokenize(Card card, final TokenCallback callback) {
        tokenizeBase(card, null, callback);
    }

    /**
     * Returns a card token that can be used to create a charge
     *
     * @param card     Card to be tokenized
     * @param metadata JSONObject containing metadatas to be stored during tokenization
     * @param callback Tokenization callback
     */
    public void tokenize(Card card, JSONObject metadata, final TokenCallback callback) {
        tokenizeBase(card, metadata, callback);
    }


    private void tokenizeBase(Card card, JSONObject metadata, final TokenCallback callback) {
        Gson gson = new Gson();
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
    public void updateCvc(Card card, final CvcUpdateCallback callback) {
        Gson gson = new Gson();
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

    /**
     * Retrieves the list of active alternative payment methods
     *
     * @param invoiceId Invoice ID that should be used for charging (this needs to be generated on your backend).
     *                  Keep in mind that you should set the return_url to "your_app://processout.return".
     *                  Check https://www.docs.processsout.com for more details
     * @param callback  Callback for listing alternative payment methods
     */
    public void listAlternativeMethods(final String invoiceId, final ListAlternativeMethodsCallback callback) {
        final Gson gson = new Gson();
        final Context context = this.context;
        final String projectId = this.projectId;


        Network.getInstance(this.context, this.projectId).CallProcessOut(
                "/gateway-configurations?filter=alternative-payment-methods&expand_merchant_accounts=true",
                Request.Method.GET, null, new Network.NetworkResult() {
                    @Override
                    public void onError(Exception error) {
                        callback.onError(error);
                    }

                    @Override
                    public void onSuccess(JSONObject json) {
                        try {
                            JSONArray configs = json.getJSONArray("gateway_configurations");

                            ArrayList<AlternativeGateway> gways = new ArrayList<>();
                            for (int i = 0; i < configs.length(); i++) {
                                AlternativeGateway g = gson.fromJson(
                                        configs.getJSONObject(i).toString(), AlternativeGateway.class);
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
     * Allow card payments authorization (with 3DS2 support)
     *
     * @param invoiceId previously generated invoice
     * @param source    source to use for the charge (card token, etc.)
     * @param handler   (Custom 3DS2 handler)
     */
    public void makeCardPayment(final String invoiceId, final String source, final ThreeDSHandler handler) {
        try {
            // Generate the authorization body and forces 3DS2
            AuthorizationRequest authRequest = new AuthorizationRequest(source);
            final JSONObject body = new JSONObject(new Gson().toJson(authRequest));

            Network.getInstance(this.context, this.projectId).CallProcessOut(
                    "/invoices/" + invoiceId + "/authorize", Request.Method.POST,
                    body, new Network.NetworkResult() {
                        @Override
                        public void onError(Exception error) {
                            handler.onError(error);
                        }

                        @Override
                        public void onSuccess(JSONObject json) {
                            Gson gson = new Gson();
                            // Handle the authorization result
                            AuthorizationResult result = gson.fromJson(
                                    json.toString(), AuthorizationResult.class);
                            handleAuthorizationResult(invoiceId, source, handler, result);
                        }
                    });
        } catch (JSONException e) {
            handler.onError(e);
        }
    }

    private void handleAuthorizationResult(
            final String invoiceId, final String source, final ThreeDSHandler handler, AuthorizationResult result) {
        CustomerAction cA = result.getCustomerAction();
        if (cA == null) {
            // No customer action in the authorization result, we return the invoice id
            handler.onSuccess(invoiceId);
            return;
        }

        // Customer action required
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        switch (cA.getType()) {
            case FINGERPRINT_MOBILE:
                DirectoryServerData directoryServerData = new Gson().fromJson(new String(Base64.decode(cA.getValue().getBytes(), Base64.NO_WRAP)), DirectoryServerData.class);
                handler.doFingerprint(directoryServerData, new ThreeDSHandler.DoFingerprintCallback() {
                    @Override
                    public void continueCallback(ThreeDSFingerprintResponse request) {
                        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                        MiscGatewayRequest gwayRequest = new MiscGatewayRequest(gson.toJson(request, ThreeDSFingerprintResponse.class));
                        String jsonRequest = Base64.encodeToString(gson.toJson(gwayRequest, MiscGatewayRequest.class).getBytes(), Base64.NO_WRAP);
                        makeCardPayment(invoiceId, "gway_req_" + jsonRequest, handler);
                    }
                });
                break;
            case CHALLENGE_MOBILE:
                AuthenticationChallengeData authentificationData = new Gson().fromJson(new String(Base64.decode(cA.getValue().getBytes(), Base64.NO_WRAP)), AuthenticationChallengeData.class);
                handler.doChallenge(authentificationData, new ThreeDSHandler.DoChallengeCallback() {
                    @Override
                    public void success() {
                        makeCardPayment(invoiceId, ThreeDS2ChallengeSuccess, handler);
                    }

                    @Override
                    public void error() {
                        makeCardPayment(invoiceId, ThreeDS2ChallengeError, handler);
                    }
                });
                break;
            default:
                //TODO: handle FINGERPRINT, URL and REDIRECT for mobiles that don't support 3ds2 challenges
                handler.onError(null);
                break;
        }
    }


    public interface ThreeDSHandlerTestCallback {
        void onSuccess(String invoiceId);
        void onError(Exception error);
    }
    /**
     * Generate a test ThreeDSHandler for 3DS2 challenges
     *
     * @param context Application context
     * @return
     */
    public static ThreeDSHandler createDefaultTestHandler(final Context context, final ThreeDSHandlerTestCallback callback) {
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
            public void onSuccess(String invoiceId) {
                callback.onSuccess(invoiceId);
            }

            @Override
            public void onError(Exception error) {
                callback.onError(error);
            }
        };
    }
}
