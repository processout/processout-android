package com.processout.processoutexample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.processout.processout_sdk.AlternativeGateway;
import com.processout.processout_sdk.ListAlternativeMethodsCallback;
import com.processout.processout_sdk.ProcessOut;

import java.util.ArrayList;

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
        final ProcessOut p = new ProcessOut(this, "test-proj_dHvuowrjviYWm7ZX0hXlb7X2yaxdgo06");
        p.listAlternativeMethods("iv_qQ2IpoUGzr3X5wKS3ZcEu6JVObcRGY6s", new ListAlternativeMethodsCallback() {
            @Override
            public void onSuccess(ArrayList<AlternativeGateway> gateways) {
                for (AlternativeGateway g :
                        gateways) {
                    g.redirect();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
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
