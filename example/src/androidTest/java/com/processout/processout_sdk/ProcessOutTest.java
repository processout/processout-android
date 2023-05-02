package com.processout.processout_sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import android.net.Uri;

import androidx.test.core.app.ApplicationProvider;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

@SuppressWarnings("removal")
public class ProcessOutTest {

    private final String projectId = "test-proj_2hO7lwt5vf3FjBFB37glPzMG3Y8Lq8O8";
    private final String privateKey = "key_test_R56fdFWMpcAzt5Cenn3oK4emCowFe4l4";
    private final ProcessOut p = new ProcessOut(ApplicationProvider.getApplicationContext(), projectId);
    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

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
    public void listPaymentGateways() {
        final CountDownLatch signal = new CountDownLatch(1);

        Invoice invoice = new Invoice("test", "123.0", "EUR", new Device("android"));
        try {
            JSONObject body = new JSONObject(gson.toJson(invoice));
            Network.getTestInstance(ApplicationProvider.getApplicationContext(), projectId, privateKey).CallProcessOut("/invoices", Request.Method.POST, body, new Network.NetworkResult() {
                @Override
                public void onError(Exception error) {
                    fail(error.toString());
                }

                @Override
                public void onSuccess(JSONObject json) {
                    try {
                        Invoice invoiceResult = gson.fromJson(json.getJSONObject("invoice").toString(), Invoice.class);
                    } catch (JSONException e) {
                        fail("Unhandled exception");
                        e.printStackTrace();
                        return;
                    }
                    p.fetchGatewayConfigurations(ProcessOut.GatewaysListingFilter.AlternativePaymentMethodWithTokenization, new FetchGatewaysConfigurationsCallback() {
                        @Override
                        public void onSuccess(ArrayList<GatewayConfiguration> gateways) {
                            signal.countDown();
                        }

                        @Override
                        public void onError(Exception e) {
                            fail("Error while fetching gateways");
                        }
                    });
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

    @Test
    public void testReturnAPMTokenHandling() {
        Uri testUri = Uri.parse("https://processout.return?token=test-token&customer_id=test-customer-id&token_id=test-token-id");
        APMTokenReturn apmReturn = p.handleAPMURLCallback(testUri);
        assertNotNull(apmReturn);
        assertEquals(apmReturn.getType(), APMTokenReturn.APMReturnType.TokenCreation);
        assertEquals(apmReturn.getCustomerId(), "test-customer-id");
        assertEquals(apmReturn.getToken(), "test-token");
        assertEquals(apmReturn.getTokenId(), "test-token-id");
        assertEquals(apmReturn.getToken(), "test-token");
    }

    @Test
    public void testReturnAPMPaymentHandling() {
        Uri testUri = Uri.parse("https://processout.return?token=test-token");
        APMTokenReturn apmReturn = p.handleAPMURLCallback(testUri);
        assertNotNull(apmReturn);
        assertEquals(apmReturn.getType(), APMTokenReturn.APMReturnType.Authorization);
        assertEquals(apmReturn.getToken(), "test-token");
    }

    @Test
    public void testReturnAPMWrongURI() {
        Uri testUri = Uri.parse("https://processout.wrong?token=test-token&customer_id=test-customer-id&token_id=test-token-id");
        APMTokenReturn apmReturn = p.handleAPMURLCallback(testUri);
        assertNull(apmReturn);
    }
}
