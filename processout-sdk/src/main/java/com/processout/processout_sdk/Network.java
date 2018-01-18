package com.processout.processout_sdk;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jeremylejoux on 17/01/2018.
 */

class Network {

    private static Network instance = new Network();
    private static RequestQueue queue;
    private static String projectId;

    private static final String API_URL = "https://api.processout.ninja";

    private Network() {}

    public interface NetworkResult {
        void onError(POErrors error);
        void onSuccess(JSONObject json);
    }

    void CallProcessOut(String url, int method, JSONObject body, final NetworkResult callback) {
        JsonObjectRequest request = new JsonObjectRequest(method, API_URL + url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    callback.onError(POErrors.NetworkError);
                } else if (error instanceof AuthFailureError) {
                    callback.onError(POErrors.AuthorizationError);
                } else if (error instanceof ServerError) {
                    if (error.networkResponse.statusCode < 500)
                        callback.onError(POErrors.BadRequest);
                    else
                        callback.onError(POErrors.InternalError);
                } else if (error instanceof NetworkError) {
                    callback.onError(POErrors.NetworkError);
                } else if (error instanceof ParseError) {
                    callback.onError(POErrors.ParseError);
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + Base64.encodeToString((projectId + ":").getBytes(), Base64.NO_WRAP));
                return headers;
            }
        };

        queue.add(request);
    }


    static Network getInstance(Context context, String projectId) {
        if (Network.queue == null) {
            queue = Volley.newRequestQueue(context);
        }

        if (Network.projectId == null) {
            Network.projectId = projectId;
        }

        return instance;
    }
}
