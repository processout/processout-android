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
import com.processout.processout_sdk.CvcUpdateCallback;
import com.processout.processout_sdk.POErrors;
import com.processout.processout_sdk.ProcessOut;
import com.processout.processout_sdk.TokenCallback;

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



        final ProcessOut p = new ProcessOut(this, "your_project_id");
        Card c = new Card("Jeremy lejoux","4242424242424242", 11, 19, "123");
        p.tokenize(c, new TokenCallback() {
            @Override
            public void onError(POErrors error) {
                Log.e("PO", String.valueOf(error));
            }

            @Override
            public void onSuccess(String token)
            {
                // send the card token to your backend for charging
            }
        });

        // Update a cvc when needed
        p.updateCvc(new Card("card_token", "124"), new CvcUpdateCallback() {
            @Override
            public void onSuccess() {
                // CVC updated
            }

            @Override
            public void onError(POErrors error) {
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
