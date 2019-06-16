package com.processout.processout_sdk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.android.volley.Request;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class ProcessOutTest {

    private String projectId = "test-proj_gAO1Uu0ysZJvDuUpOGPkUBeE3pGalk3x";
    private String privateKey = "key_sandbox_5v1aedGG4spxuTH6zeR64uKAlX5LimAu";

    @Test
    public void threeDS2Fingerprint() {
        final CountDownLatch signal = new CountDownLatch(1);

        final ProcessOut p = new ProcessOut(InstrumentationRegistry.getContext(), projectId);
        Card c = new Card("4000000000003063", 10, 20, "737");
        p.tokenize(c,null,  new TokenCallback() {
            @Override
            public void onError(Exception error) {
                fail("Could not tokenize the card");
            }

            @Override
            public void onSuccess(final String token) {
                Invoice invoice = new Invoice("test", "121.01", "EUR", new Device("android"));
                try {
                    JSONObject body = new JSONObject(new Gson().toJson(invoice));
                    Network.getTestInstance(InstrumentationRegistry.getContext(), projectId, privateKey).CallProcessOut("/invoices", Request.Method.POST, body, new Network.NetworkResult() {
                        @Override
                        public void onError(Exception error) {
                            Log.e("PROCESSOUT", error.toString());
                            fail("Invoice creation failed");
                        }

                        @Override
                        public void onSuccess(JSONObject json) {
                            try {
                                Invoice invoiceResult = new Gson().fromJson(json.getJSONObject("invoice").toString(), Invoice.class);
                                p.makeCardPayment(invoiceResult.getId(), token, new ThreeDSHandler() {
                                    @Override
                                    public void doFingerprint(DirectoryServerData directoryServerData, DoFingerprintCallback callback) {
                                        callback.continueCallback(
                                                new ThreeDSFingerprintResponse(
                                                        "", "", new SDKEPhemPubKey("", "", "", ""),
                                                        "", ""));
                                    }

                                    @Override
                                    public void doChallenge(ThreeDSGatewayRequest authData, final DoChallengeCallback callback) {
                                        callback.success();
                                    }

                                    @Override
                                    public void onSuccess(String invoiceId) {
                                        signal.countDown();
                                    }

                                    @Override
                                    public void onError(Exception error) {
                                        fail("ThreeDS2 failed");
                                    }
                                });
                            } catch (JSONException e) {
                                fail("Unhandled exception");
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    fail("Unhandled exception");
                    e.printStackTrace();
                }
            }
        });

        try {
            signal.await();// wait for callback
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Could not run test");
        }
    }

    @Test
    public void threeDS2Challenge() {
        final CountDownLatch signal = new CountDownLatch(1);

        final ProcessOut p = new ProcessOut(InstrumentationRegistry.getContext(), projectId);
        Card c = new Card("4000000000000101", 10, 20, "737");
        p.tokenize(c,null,  new TokenCallback() {
            @Override
            public void onError(Exception error) {
                fail("Could not tokenize the card");
            }

            @Override
            public void onSuccess(final String token) {
                // Creation of the invoice
                Invoice invoice = new Invoice("test", "121.01", "EUR", new Device("android"));
                try {
                    JSONObject body = new JSONObject(new Gson().toJson(invoice));
                    Network.getTestInstance(InstrumentationRegistry.getContext(), projectId, privateKey).CallProcessOut("/invoices", Request.Method.POST, body, new Network.NetworkResult() {
                        @Override
                        public void onError(Exception error) {
                            fail("Invoice creation failed");
                        }

                        @Override
                        public void onSuccess(JSONObject json) {
                            try {
                                Invoice invoiceResult = new Gson().fromJson(json.getJSONObject("invoice").toString(), Invoice.class);
                                p.makeCardPayment(invoiceResult.getId(), token, new ThreeDSHandler() {
                                    @Override
                                    public void doFingerprint(DirectoryServerData directoryServerData, DoFingerprintCallback callback) {
                                        callback.continueCallback(
                                                new ThreeDSFingerprintResponse(
                                                        "", "", new SDKEPhemPubKey("", "", "", ""),
                                                        "", ""));
                                    }

                                    @Override
                                    public void doChallenge(ThreeDSGatewayRequest authData, final DoChallengeCallback callback) {
                                        callback.success();
                                    }

                                    @Override
                                    public void onSuccess(String invoiceId) {
                                        signal.countDown();
                                    }

                                    @Override
                                    public void onError(Exception error) {
                                        fail("ThreeDS2 failed" + error.toString());
                                    }
                                });
                            } catch (JSONException e) {
                                fail("Unhandled exception");
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    fail("Unhandled exception");
                    e.printStackTrace();
                }
            }
        });

        try {
            signal.await();// wait for callback
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Could not run test");
        }
    }
}