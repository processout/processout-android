package com.processout.processout_sdk;

import static org.junit.Assert.fail;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.processout.example.ui.screen.TestActivity;
import com.processout.processout_sdk.POWebViews.ProcessOutWebView;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.concurrent.CountDownLatch;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@SuppressWarnings({"removal", "NewClassNamingConvention"})
@RunWith(AndroidJUnit4.class)
@LargeTest
public class UiTestSuite {

    private static final String projectId = "test-proj_2hO7lwt5vf3FjBFB37glPzMG3Y8Lq8O8";
    private static final String privateKey = "key_test_R56fdFWMpcAzt5Cenn3oK4emCowFe4l4";
    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    private static final int cardExpirationYear;
    private static final int cardExpirationMonth;
    private static final int INVOICE_ID_LENGTH = 35;

    static {
        // card is set to expire in one year from now
        Calendar calendar = Calendar.getInstance();
        cardExpirationYear = calendar.get(Calendar.YEAR) % 100 + 1;
        cardExpirationMonth = calendar.get(Calendar.MONTH);
    }

    @Rule
    public ActivityTestRule<TestActivity> activityRule = new ActivityTestRule<>(TestActivity.class, false, true);

    /**
     * TEST MAKE CARD PAYMENT
     */
    @Test
    public void testSuccessful3DSPayment() {
        final CountDownLatch signal = new CountDownLatch(1);

        final Activity withActivity = activityRule.getActivity();
        final ProcessOut p = new ProcessOut(withActivity.getApplication(), projectId);
        Card paymentCard = new Card("4000000000003246", cardExpirationMonth, cardExpirationYear, "737");
        p.tokenize(paymentCard, null, new TokenCallback() {
            @Override
            public void onError(Exception error) {
                fail("Could not tokenize the card");
            }

            @Override
            public void onSuccess(final String token) {
                Invoice invoice = new Invoice("test", "121.01", "EUR", new Device("android"));
                JSONObject body = null;
                try {
                    body = new JSONObject(gson.toJson(invoice));
                } catch (Exception e) {
                    fail("Could not encode body");
                    return;
                }
                Network.getTestInstance(withActivity.getApplication(), projectId, privateKey).CallProcessOut("/invoices", Request.Method.POST, body, new Network.NetworkResult() {
                    @Override
                    public void onError(Exception error) {
                        fail("Invoice creation failed");
                    }

                    @Override
                    public void onSuccess(JSONObject json) {
                        Invoice invoiceResult = null;
                        try {
                            invoiceResult = gson.fromJson(json.getJSONObject("invoice").toString(), Invoice.class);
                        } catch (JSONException e) {
                            fail("Unhandled exception");
                            e.printStackTrace();
                            return;
                        }

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
                            public void doPresentWebView(final ProcessOutWebView webView) {
                                final FrameLayout layout = withActivity.findViewById(android.R.id.content);
                                final int id = View.generateViewId();
                                webView.setId(id);
                                layout.addView(webView);

                                // Wait for the page to load and simulate successful click
                                Handler handler = new Handler();
                                Runnable r = new Runnable() {
                                    public void run() {
                                        // Simulate a click
                                        webView.loadUrl("javascript:document.getElementsByTagName('a')[0].click()");
                                    }
                                };
                                handler.postDelayed(r, 10000);
                            }

                            @Override
                            public void onSuccess(String invoiceId) {
                                signal.countDown();
                            }

                            @Override
                            public void onError(Exception error) {
                                fail("ThreeDS2 failed");
                            }
                        }, withActivity);
                    }
                });
            }
        });

        try {
            signal.await();// wait for callback
        } catch (InterruptedException e) {
            fail("Could not run test");
        }
    }

    @Test
    public void testSuccessful3DS2Payment() {
        final CountDownLatch signal = new CountDownLatch(1);

        final Activity withActivity = activityRule.getActivity();
        final ProcessOut p = new ProcessOut(withActivity.getApplication(), projectId);
        Card paymentCard = new Card("4000000000003253", cardExpirationMonth, cardExpirationYear, "737");
        p.tokenize(paymentCard, null, new TokenCallback() {
            @Override
            public void onError(Exception error) {
                fail("Could not tokenize the card");
            }

            @Override
            public void onSuccess(final String token) {
                Invoice invoice = new Invoice("test", "121.01", "EUR", new Device("android"));
                JSONObject body = null;
                try {
                    body = new JSONObject(gson.toJson(invoice));
                } catch (Exception e) {
                    fail("Could not encode body");
                    return;
                }

                Network.getTestInstance(withActivity.getApplication(), projectId, privateKey).CallProcessOut("/invoices", Request.Method.POST, body, new Network.NetworkResult() {
                    @Override
                    public void onError(Exception error) {
                        fail("Invoice creation failed");
                    }

                    @Override
                    public void onSuccess(JSONObject json) {
                        Invoice invoiceResult = null;
                        try {
                            invoiceResult = gson.fromJson(json.getJSONObject("invoice").toString(), Invoice.class);
                        } catch (JSONException e) {
                            fail("Unhandled exception");
                            e.printStackTrace();
                            return;
                        }

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
                            public void doPresentWebView(final ProcessOutWebView webView) {
                                fail("Webview should not be required");
                            }

                            @Override
                            public void onSuccess(String invoiceId) {
                                signal.countDown();
                            }

                            @Override
                            public void onError(Exception error) {
                                fail("ThreeDS2 failed");
                            }
                        }, withActivity);
                    }
                });
            }
        });

        try {
            signal.await();// wait for callback
        } catch (InterruptedException e) {
            fail("Could not run test");
        }
    }

    @Test
    public void testFailed3DS1Payment() {
        final CountDownLatch signal = new CountDownLatch(1);

        final Activity withActivity = activityRule.getActivity();
        final ProcessOut p = new ProcessOut(withActivity.getApplication(), projectId);
        Card paymentCard = new Card("4000000000003246", cardExpirationMonth, cardExpirationYear, "737");
        p.tokenize(paymentCard, null, new TokenCallback() {
            @Override
            public void onError(Exception error) {
                fail("Could not tokenize the card");
            }

            @Override
            public void onSuccess(final String token) {
                Invoice invoice = new Invoice("test", "121.01", "EUR", new Device("android"));
                JSONObject body = null;
                try {
                    body = new JSONObject(gson.toJson(invoice));
                } catch (Exception e) {
                    fail("Could not encode body");
                    return;
                }

                Network.getTestInstance(withActivity.getApplication(), projectId, privateKey).CallProcessOut("/invoices", Request.Method.POST, body, new Network.NetworkResult() {
                    @Override
                    public void onError(Exception error) {
                        fail("Invoice creation failed");
                    }

                    @Override
                    public void onSuccess(JSONObject json) {
                        Invoice invoiceResult = null;
                        try {
                            invoiceResult = gson.fromJson(json.getJSONObject("invoice").toString(), Invoice.class);
                        } catch (JSONException e) {
                            fail("Unhandled exception");
                            e.printStackTrace();
                            return;
                        }

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
                            public void doPresentWebView(final ProcessOutWebView webView) {
                                final FrameLayout layout = withActivity.findViewById(android.R.id.content);
                                final int id = View.generateViewId();
                                webView.setId(id);
                                layout.addView(webView);

                                // Wait for the page to load and simulate successful click
                                Handler handler = new Handler();
                                Runnable r = new Runnable() {
                                    public void run() {
                                        // Simulate a click
                                        webView.loadUrl("javascript:document.getElementsByTagName('a')[1].click()");
                                    }
                                };
                                handler.postDelayed(r, 4000);
                            }

                            @Override
                            public void onSuccess(String invoiceId) {
                                fail("ThreeDS2 succeeded but should have failed.");
                            }

                            @Override
                            public void onError(Exception error) {
                                signal.countDown();
                            }
                        }, withActivity);
                    }
                });
            }
        });

        try {
            signal.await();// wait for callback
        } catch (InterruptedException e) {
            fail("Could not run test");
        }
    }

    @Test
    public void testFailed3DS2Challenge() {
        final CountDownLatch signal = new CountDownLatch(1);

        final Activity withActivity = activityRule.getActivity();
        final ProcessOut p = new ProcessOut(withActivity.getApplication(), projectId);
        Card paymentCard = new Card("4000000000003253", cardExpirationMonth, cardExpirationYear, "737");
        p.tokenize(paymentCard, null, new TokenCallback() {
            @Override
            public void onError(Exception error) {
                fail("Could not tokenize the card");
            }

            @Override
            public void onSuccess(final String token) {
                Invoice invoice = new Invoice("test", "121.01", "EUR", new Device("android"));
                JSONObject body = null;
                try {
                    body = new JSONObject(gson.toJson(invoice));
                } catch (Exception e) {
                    fail("Could not encode body");
                    return;
                }

                Network.getTestInstance(withActivity.getApplication(), projectId, privateKey).CallProcessOut("/invoices", Request.Method.POST, body, new Network.NetworkResult() {
                    @Override
                    public void onError(Exception error) {
                        fail("Invoice creation failed");
                    }

                    @Override
                    public void onSuccess(JSONObject json) {
                        Invoice invoiceResult = null;
                        try {
                            invoiceResult = gson.fromJson(json.getJSONObject("invoice").toString(), Invoice.class);
                        } catch (JSONException e) {
                            fail("Unhandled exception");
                            e.printStackTrace();
                            return;
                        }

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
                                callback.error();
                            }

                            @Override
                            public void doPresentWebView(final ProcessOutWebView webView) {
                                fail("Webview should not be required");
                            }

                            @Override
                            public void onSuccess(String invoiceId) {
                                fail("ThreeDS2 should have failed.");
                            }

                            @Override
                            public void onError(Exception error) {
                                signal.countDown();
                            }
                        }, withActivity);
                    }
                });
            }
        });

        try {
            signal.await();// wait for callback
        } catch (InterruptedException e) {
            fail("Could not run test");
        }
    }

    /**
     * TEST MAKE CARD TOKEN
     */
    @Test
    public void testSuccessful3DS2Token() {
        final CountDownLatch signal = new CountDownLatch(1);

        final Activity withActivity = activityRule.getActivity();
        final ProcessOut p = new ProcessOut(withActivity.getApplication(), projectId);
        Card paymentCard = new Card("4000000000003253", cardExpirationMonth, cardExpirationYear, "737");
        p.tokenize(paymentCard, null, new TokenCallback() {
            @Override
            public void onError(Exception error) {
                fail("Could not tokenize the card");
            }

            @Override
            public void onSuccess(final String token) {
                Invoice invoice = new Invoice("test", "121.01", "EUR", new Device("android"));
                JSONObject body = null;
                try {
                    body = new JSONObject(gson.toJson(invoice));
                } catch (Exception e) {
                    fail("Could not encode body");
                    return;
                }

                Network.getTestInstance(withActivity.getApplication(), projectId, privateKey).CallProcessOut("/invoices", Request.Method.POST, body, new Network.NetworkResult() {
                    @Override
                    public void onError(Exception error) {
                        fail("Invoice creation failed");
                    }

                    @Override
                    public void onSuccess(JSONObject json) {
                        Invoice invoiceResult = null;
                        try {
                            invoiceResult = gson.fromJson(json.getJSONObject("invoice").toString(), Invoice.class);
                        } catch (JSONException e) {
                            fail("Unhandled exception");
                            e.printStackTrace();
                            return;
                        }

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
                            public void doPresentWebView(final ProcessOutWebView webView) {
                                fail("Webview should not be required");
                            }

                            @Override
                            public void onSuccess(String invoiceId) {
                                signal.countDown();
                            }

                            @Override
                            public void onError(Exception error) {
                                fail("ThreeDS2 failed");
                            }
                        }, withActivity);
                    }
                });
            }
        });

        try {
            signal.await();// wait for callback
        } catch (InterruptedException e) {
            fail("Could not run test");
        }
    }

    @Test
    public void testFailed3DS1Token() {
        final CountDownLatch signal = new CountDownLatch(1);

        final Activity withActivity = activityRule.getActivity();
        final ProcessOut p = new ProcessOut(withActivity.getApplication(), projectId);
        Card paymentCard = new Card("4000000000003246", cardExpirationMonth, cardExpirationYear, "737");
        p.tokenize(paymentCard, null, new TokenCallback() {
            @Override
            public void onError(Exception error) {
                fail("Could not tokenize the card");
            }

            @Override
            public void onSuccess(final String token) {
                Invoice invoice = new Invoice("test", "121.01", "EUR", new Device("android"));
                JSONObject body = null;
                try {
                    body = new JSONObject(gson.toJson(invoice));
                } catch (Exception e) {
                    fail("Could not encode body");
                    return;
                }

                Network.getTestInstance(withActivity.getApplication(), projectId, privateKey).CallProcessOut("/invoices", Request.Method.POST, body, new Network.NetworkResult() {
                    @Override
                    public void onError(Exception error) {
                        fail("Invoice creation failed");
                    }

                    @Override
                    public void onSuccess(JSONObject json) {
                        Invoice invoiceResult = null;
                        try {
                            invoiceResult = gson.fromJson(json.getJSONObject("invoice").toString(), Invoice.class);
                        } catch (JSONException e) {
                            fail("Unhandled exception");
                            e.printStackTrace();
                            return;
                        }

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
                            public void doPresentWebView(final ProcessOutWebView webView) {
                                final FrameLayout layout = withActivity.findViewById(android.R.id.content);
                                final int id = View.generateViewId();
                                webView.setId(id);
                                layout.addView(webView);

                                // Wait for the page to load and simulate successful click
                                Handler handler = new Handler();
                                Runnable r = new Runnable() {
                                    public void run() {
                                        // Simulate a click
                                        webView.loadUrl("javascript:document.getElementsByTagName('a')[1].click()");
                                    }
                                };
                                handler.postDelayed(r, 4000);
                            }

                            @Override
                            public void onSuccess(String invoiceId) {
                                fail("ThreeDS2 succeeded but should have failed.");
                            }

                            @Override
                            public void onError(Exception error) {
                                signal.countDown();
                            }
                        }, withActivity);
                    }
                });
            }
        });

        try {
            signal.await();// wait for callback
        } catch (InterruptedException e) {
            fail("Could not run test");
        }
    }

    @Test
    public void testFailed3DS2TokenChallenge() {
        final CountDownLatch signal = new CountDownLatch(1);

        final Activity withActivity = activityRule.getActivity();
        final ProcessOut p = new ProcessOut(withActivity.getApplication(), projectId);
        Card paymentCard = new Card("4000000000003253", cardExpirationMonth, cardExpirationYear, "737");
        p.tokenize(paymentCard, null, new TokenCallback() {
            @Override
            public void onError(Exception error) {
                fail("Could not tokenize the card");
            }

            @Override
            public void onSuccess(final String token) {
                Invoice invoice = new Invoice("test", "121.01", "EUR", new Device("android"));
                JSONObject body = null;
                try {
                    body = new JSONObject(gson.toJson(invoice));
                } catch (Exception e) {
                    fail("Could not encode body");
                    return;
                }

                Network.getTestInstance(withActivity.getApplication(), projectId, privateKey).CallProcessOut("/invoices", Request.Method.POST, body, new Network.NetworkResult() {
                    @Override
                    public void onError(Exception error) {
                        fail("Invoice creation failed");
                    }

                    @Override
                    public void onSuccess(JSONObject json) {
                        Invoice invoiceResult = null;
                        try {
                            invoiceResult = gson.fromJson(json.getJSONObject("invoice").toString(), Invoice.class);
                        } catch (JSONException e) {
                            fail("Unhandled exception");
                            e.printStackTrace();
                            return;
                        }

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
                                callback.error();
                            }

                            @Override
                            public void doPresentWebView(final ProcessOutWebView webView) {
                                fail("Webview should not be required");
                            }

                            @Override
                            public void onSuccess(String invoiceId) {
                                fail("ThreeDS2 succeeded but should have failed.");
                            }

                            @Override
                            public void onError(Exception error) {
                                signal.countDown();
                            }
                        }, withActivity);
                    }
                });
            }
        });

        try {
            signal.await();// wait for callback
        } catch (InterruptedException e) {
            fail("Could not run test");
        }
    }

    @Test
    public void testAuthorization() {
        final CountDownLatch signal = new CountDownLatch(1);

        final Activity withActivity = activityRule.getActivity();
        final ProcessOut p = new ProcessOut(withActivity.getApplication(), projectId);
        Card paymentCard = new Card("4000000000003253", cardExpirationMonth, cardExpirationYear, "737");
        p.tokenize(paymentCard, null, new TokenCallback() {
            @Override
            public void onError(Exception error) {
                fail("Could not tokenize the card");
            }

            @Override
            public void onSuccess(final String token) {
                Invoice invoice = new Invoice("test", "121.01", "EUR", new Device("android"));
                JSONObject body = null;
                try {
                    body = new JSONObject(gson.toJson(invoice));
                } catch (Exception e) {
                    fail("Could not encode body");
                    return;
                }

                Network.getTestInstance(withActivity.getApplication(), projectId, privateKey).CallProcessOut("/invoices", Request.Method.POST, body, new Network.NetworkResult() {
                    @Override
                    public void onError(Exception error) {
                        fail("Invoice creation failed");
                    }

                    @Override
                    public void onSuccess(JSONObject json) {
                        Invoice invoiceResult = null;
                        try {
                            invoiceResult = gson.fromJson(json.getJSONObject("invoice").toString(), Invoice.class);
                        } catch (JSONException e) {
                            fail("Unhandled exception");
                            e.printStackTrace();
                            return;
                        }

                        final Invoice finalInvoiceResult = invoiceResult;

                        p.makeCardPayment(finalInvoiceResult.getId(), token, new ThreeDSHandler() {
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
                            public void doPresentWebView(final ProcessOutWebView webView) {
                                fail("Webview should not be required");
                            }

                            @Override
                            public void onSuccess(String invoiceId) {
                                if (invoiceId.length() != INVOICE_ID_LENGTH) {
                                    fail("Authorization did not return a valid invoice id");
                                } else {
                                    signal.countDown();
                                }
                            }

                            @Override
                            public void onError(Exception error) {
                                fail("Authorization failed");
                            }
                        }, withActivity);
                    }
                });
            }
        });

        try {
            signal.await();// wait for callback
        } catch (InterruptedException e) {
            fail("Could not run test");
        }
    }

    @Test
    public void testIncrementalAuthorization() {
        final CountDownLatch signal = new CountDownLatch(1);

        final Activity withActivity = activityRule.getActivity();
        final ProcessOut p = new ProcessOut(withActivity.getApplication(), projectId);
        Card paymentCard = new Card("4000000000003253", cardExpirationMonth, cardExpirationYear, "737");
        p.tokenize(paymentCard, null, new TokenCallback() {
            @Override
            public void onError(Exception error) {
                fail("Could not tokenize the card");
            }

            @Override
            public void onSuccess(final String token) {
                Invoice invoice = new Invoice("test", "121.01", "EUR", new Device("android"));
                JSONObject body = null;
                try {
                    body = new JSONObject(gson.toJson(invoice));
                } catch (Exception e) {
                    fail("Could not encode body");
                    return;
                }

                Network.getTestInstance(withActivity.getApplication(), projectId, privateKey).CallProcessOut("/invoices", Request.Method.POST, body, new Network.NetworkResult() {
                    @Override
                    public void onError(Exception error) {
                        fail("Invoice creation failed");
                    }

                    @Override
                    public void onSuccess(JSONObject json) {
                        Invoice invoiceResult = null;
                        try {
                            invoiceResult = gson.fromJson(json.getJSONObject("invoice").toString(), Invoice.class);
                        } catch (JSONException e) {
                            fail("Unhandled exception");
                            e.printStackTrace();
                            return;
                        }

                        final Invoice finalInvoiceResult = invoiceResult;

                        p.makeIncrementalAuthorizationPayment(finalInvoiceResult.getId(), token, new ThreeDSHandler() {
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
                            public void doPresentWebView(final ProcessOutWebView webView) {
                                fail("Webview should not be required");
                            }

                            @Override
                            public void onSuccess(String invoiceId) {
                                if (invoiceId.length() != INVOICE_ID_LENGTH) {
                                    fail("Authorization did not return a valid invoice id");
                                } else {
                                    p.incrementAuthorizationAmount(finalInvoiceResult.getId(), 1, new ThreeDSHandler() {
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
                                        public void doPresentWebView(final ProcessOutWebView webView) {
                                            fail("Webview should not be required");
                                        }

                                        @Override
                                        public void onSuccess(String id) {
                                            signal.countDown();
                                        }

                                        @Override
                                        public void onError(Exception error) {
                                            fail("Invoice incrementation failed");
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onError(Exception error) {
                                fail("Authorization failed");
                            }
                        }, withActivity);
                    }
                });
            }
        });

        try {
            signal.await();// wait for callback
        } catch (InterruptedException e) {
            fail("Could not run test");
        }
    }
}
