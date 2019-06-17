package com.processout.processoutexample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.processout.processout_sdk.Card;
import com.processout.processout_sdk.ProcessOut;
import com.processout.processout_sdk.TokenCallback;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data == null)
            this.initiatePayment();
        else
            Log.d("PROCESSOUT", "TOKEN=" + data.getQueryParameter("token"));
    }

    public void initiatePayment() {
        final ProcessOut p = new ProcessOut(this, "test-proj_WijDbvE1oEkS67ikx2cfu25Nr5Qx4emX");
        Card c = new Card("4000000000003063", 10, 20, "737");
        p.tokenize(c,null,  new TokenCallback() {
            @Override
            public void onError(Exception error) {
                Log.e("PROCESSOUT", error.toString());
            }

            @Override
            public void onSuccess(String token) {
                p.makeCardPayment("invoice-id", token, ProcessOut.createDefaultTestHandler(MainActivity.this, new ProcessOut.ThreeDSHandlerTestCallback() {
                    @Override
                    public void onSuccess(String invoiceId) {
                        Log.d("PROCESSOUT", "invocie: " + invoiceId);
                    }

                    @Override
                    public void onError(Exception error) {
                        Log.e("PROCESSOUT", error.toString());
                    }
                }));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
