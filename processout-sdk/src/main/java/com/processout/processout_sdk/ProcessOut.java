package com.processout.processout_sdk;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

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

    public void tokenize(Card card, final TokenCallback callback) {
        tokenizeBase(card, null, callback);
    }

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
                public void onError(POErrors error) {
                    callback.onError(error);
                }

                @Override
                public void onSuccess(JSONObject json) {
                    try {
                        JSONObject card = (JSONObject) json.get("card");
                        callback.onSuccess(card.get("id").toString());
                    } catch (JSONException e) {
                        callback.onError(POErrors.ParseError);
                    }
                }
            });
        } catch (JSONException e) {
            callback.onError(POErrors.ParseError);
        }
    }

    public void updateCvc(Card card, final CvcUpdateCallback callback) {
        Gson gson = new Gson();
        JSONObject body = null;

        if (card.getId() == null) {
            callback.onError(POErrors.ParseError);
            return;
        }

        try {
            body = new JSONObject(gson.toJson(card));
            Network.getInstance(this.context, this.projectId).CallProcessOut("/cards/" + card.getId(), Request.Method.PUT, body, new Network.NetworkResult() {
                @Override
                public void onError(POErrors error) {
                    callback.onError(error);
                }

                @Override
                public void onSuccess(JSONObject json) {
                    callback.onSuccess();
                }
            });
        } catch (JSONException e) {
            callback.onError(POErrors.ParseError);
        }
    }

}
