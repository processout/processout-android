package com.processout.processout_sdk;

import android.content.Context;
import com.android.volley.Request;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by jeremylejoux on 17/01/2018.
 */

public class ProcessOut {

    private String projectId;
    private Context context;

    public ProcessOut(Context context, String projectId) {
        this.projectId = projectId;
        this.context = context;
    }

    /**
     * Returns a card token that can be used to create a charge
     * @param card Card to be tokenized
     * @param callback Tokenization callback
     * @deprecated This method doesn't support metadatas
     */
    public void tokenize(Card card, final TokenCallback callback) {
        tokenizeBase(card, null, callback);
    }

    /**
     * Returns a card token that can be used to create a charge
     * @param card Card to be tokenized
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
     * @param card Card object containing the new CVC
     * @param callback Update callback
     */
    public void updateCvc(Card card, final CvcUpdateCallback callback) {
        Gson gson = new Gson();
        JSONObject body = null;

        try {
            body = new JSONObject(gson.toJson(card));
            Network.getInstance(this.context, this.projectId).CallProcessOut("/cards/" + card.getId(), Request.Method.PUT, body, new Network.NetworkResult() {
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
     * @param invoiceId Invoice ID that should be used for charging (this needs to be generated on your backend). Keep in mind that you should set the return_url to "your_app://processout.return". Check https://www.docs.processsout.com for more details
     * @param callback Callback for listing alternative payment methods
     */
    public void listAlternativeMethods(final String invoiceId, final ListAlternativeMethodsCallback callback) {
        final Gson gson = new Gson();
        final Context context = this.context;
        final String projectId = this.projectId;


        Network.getInstance(this.context, this.projectId).CallProcessOut("/gateway-configurations?filter=alternative-payment-methods", Request.Method.GET, null, new Network.NetworkResult() {
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
                        AlternativeGateway g = gson.fromJson(configs.getJSONObject(i).toString(), AlternativeGateway.class);
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
}
