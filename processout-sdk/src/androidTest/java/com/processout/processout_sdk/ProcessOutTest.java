package com.processout.processout_sdk;

import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.fail;

public class ProcessOutTest {

    private String projectId = "test-proj_gAO1Uu0ysZJvDuUpOGPkUBeE3pGalk3x";
    private String privateKey = "key_sandbox_mah31RDFqcDxmaS7MvhDbJfDJvjtsFTB";
    final private ProcessOut p = new ProcessOut(InstrumentationRegistry.getContext(), projectId);
    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @Test
    public void threeDS2Fingerprint() {
        final CountDownLatch signal = new CountDownLatch(1);


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
                    JSONObject body = new JSONObject(gson.toJson(invoice));
                    Network.getTestInstance(InstrumentationRegistry.getContext(), projectId, privateKey).CallProcessOut("/invoices", Request.Method.POST, body, new Network.NetworkResult() {
                        @Override
                        public void onError(Exception error) {
                            Log.e("PROCESSOUT", error.toString());
                            fail("Invoice creation failed");
                        }

                        @Override
                        public void onSuccess(JSONObject json) {
                            try {
                                Invoice invoiceResult = gson.fromJson(json.getJSONObject("invoice").toString(), Invoice.class);
                                p.makeCardPayment(invoiceResult.getId(), token, new ThreeDSHandler() {
                                    @Override
                                    public void doFingerprint(DirectoryServerData directoryServerData, DoFingerprintCallback callback) {
                                        callback.continueCallback(
                                                new ThreeDSFingerprintResponse(
                                                        "", "", new SDKEPhemPubKey("", "", "", ""),
                                                        "", ""));
                                    }

                                    @Override
                                    public void doChallenge(AuthenticationChallengeData authData, final DoChallengeCallback callback) {
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
                    JSONObject body = new JSONObject(gson.toJson(invoice));
                    Network.getTestInstance(InstrumentationRegistry.getContext(), projectId, privateKey).CallProcessOut("/invoices", Request.Method.POST, body, new Network.NetworkResult() {
                        @Override
                        public void onError(Exception error) {
                            fail("Invoice creation failed");
                        }

                        @Override
                        public void onSuccess(JSONObject json) {
                            try {
                                Invoice invoiceResult = gson.fromJson(json.getJSONObject("invoice").toString(), Invoice.class);
                                p.makeCardPayment(invoiceResult.getId(), token, new ThreeDSHandler() {
                                    @Override
                                    public void doFingerprint(DirectoryServerData directoryServerData, DoFingerprintCallback callback) {
                                        callback.continueCallback(
                                                new ThreeDSFingerprintResponse(
                                                        "", "", new SDKEPhemPubKey("", "", "", ""),
                                                        "", ""));
                                    }

                                    @Override
                                    public void doChallenge(AuthenticationChallengeData authData, final DoChallengeCallback callback) {
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

    @Test
    public void tokenize() {
        final CountDownLatch signal = new CountDownLatch(1);

        Card c = new Card("424242424242", 11, 21, "123");
        p.tokenize(c, null, new TokenCallback() {
            @Override
            public void onError(Exception error) {
                fail("Could not tokenize");
            }

            @Override
            public void onSuccess(String token) {
                // SUCCESS
                signal.countDown();
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
    public void listAlternativePaymentGateways() {
        final CountDownLatch signal = new CountDownLatch(1);

        Invoice invoice = new Invoice("test", "123.0", "EUR", new Device("android"));
        try {
            JSONObject body = new JSONObject(gson.toJson(invoice));
            Network.getTestInstance(InstrumentationRegistry.getContext(), projectId, privateKey).CallProcessOut("/invoices", Request.Method.POST, body, new Network.NetworkResult() {
                @Override
                public void onError(Exception error) {
                    fail(error.toString());
                }

                @Override
                public void onSuccess(JSONObject json) {
                    signal.countDown();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            fail(e.toString());
        }

        try {
            signal.await();
        } catch (Exception error) {
            error.printStackTrace();
            fail("Could not run test");
        }
    }
}