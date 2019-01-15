package com.processout.processoutexample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.processout.processout_sdk.Card;
import com.processout.processout_sdk.Contact;
import com.processout.processout_sdk.CvcUpdateCallback;
import com.processout.processout_sdk.ProcessOut;
import com.processout.processout_sdk.TokenCallback;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        final ProcessOut p = new ProcessOut(this, "proj_kVWqvz7UoS3oux2UZg5tgLjXxvPTnh0k");
        Contact contact = new Contact("11 street name", "", "City", "State", "US", "10000");
        Card c = new Card("Jeremy lejoux","4242424242424242", 11, 19, "123", contact);
        try {
            p.tokenize(c, new TokenCallback() {
                @Override
                public void onError(Exception error) {
                    Log.e("PO", error.toString());
                }

                @Override
                public void onSuccess(String token)
                {
                    Log.d("PO", token);
                    // send the card token to your backend for charging
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Update a cvc when needed
        p.updateCvc(new Card("card_8FnqE2v1bJW7iBjsqpYev6UmOxKsvRs7", "124"), new CvcUpdateCallback() {
            @Override
            public void onSuccess() {
                // CVC updated
                Log.d("ProcessOut", "successfuly updated CVC");
            }

            @Override
            public void onError(Exception error) {
                Log.d("ProcessOut", error.toString());
                // error
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
