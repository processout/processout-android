package com.processout.processout_sdk;

import android.content.Context;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.processout.processout_sdk.ProcessOutExceptions.ProcessOutAuthException;
import com.processout.processout_sdk.ProcessOutExceptions.ProcessOutCardException;
import com.processout.processout_sdk.ProcessOutExceptions.ProcessOutException;
import com.processout.processout_sdk.ProcessOutExceptions.ProcessOutNetworkException;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by jeremylejoux on 17/01/2018.
 */

class Network {

    private static Network instance = new Network();
    private static RequestQueue queue;
    private static String projectId;
    private static String privateKey = "";

    protected static final String API_URL = "https://api.processout.com";
    protected static final String CHECKOUT_URL = "https://checkout.processout.com";

    private Network() {}

    public interface NetworkResult {
        void onError(Exception error);

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
                    callback.onError(new ProcessOutNetworkException("Could not connect to server"));
                } else if (error instanceof AuthFailureError) {
                    callback.onError(new ProcessOutAuthException("Request not authorized"));
                } else if (error instanceof ServerError) {
                    if (error.networkResponse.data!=null) {
                        try {
                            String data = new String(error.networkResponse.data,"UTF-8");
                            Gson g = new GsonBuilder()
                                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                    .create();
                            ErrorReponse resp = g.fromJson(data, ErrorReponse.class);
                            callback.onError(new ProcessOutCardException(resp.getErrorMessage(), resp.getErrorType()));
                        } catch (UnsupportedEncodingException e) {
                            callback.onError(e);
                        }
                    } else {
                        callback.onError(new ProcessOutNetworkException("Could not receive server data."));
                    }
                } else if (error instanceof NetworkError) {
                    callback.onError(new ProcessOutNetworkException("Could not connect to server"));
                } else if (error instanceof ParseError) {
                    callback.onError(new ProcessOutException("Error while parsing server response"));
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Basic " + Base64.encodeToString((projectId + ":" + privateKey).getBytes(), Base64.NO_WRAP));
                headers.put("Idempotency-Key", UUID.randomUUID().toString());
                return headers;
            }
        };

        RetryPolicy retryPolicy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(15), 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        queue.add(request);
    }


    static Network getTestInstance(Context context, String projectId, String privateKey) {
        if (Network.queue == null) {
            queue = Volley.newRequestQueue(context);
        }

        if (Network.projectId == null) {
            Network.projectId = projectId;
        }

        if (Network.privateKey.equals(""))
            Network.privateKey = privateKey;

        return instance;
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

class ErrorReponse {
    private String message;
    private String errorType;

    public ErrorReponse(String errorMessage, String errorCode) {
        this.message = message;this.errorType = errorType;
    }

    public String getErrorMessage() {
        return message;
    }

    public String getErrorType() {
        return errorType;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "message='" + message + '\'' +
                ", errorType='" + errorType + '\'' +
                '}';
    }
}
