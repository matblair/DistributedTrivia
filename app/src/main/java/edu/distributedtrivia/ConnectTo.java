package edu.distributedtrivia;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


public class ConnectTo extends ActionBarActivity {

    EditText edtIP;

    MulticastSocketClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to);

        edtIP = (EditText) this.findViewById(R.id.edtIP);

        WifiManager wim = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (wim != null) {
            WifiManager.MulticastLock mcLock = wim.createMulticastLock("msg");
            mcLock.acquire();

            try {
                client = new MulticastSocketClient();
                client.start();
            } catch (Exception e) {
                Log.d("Error", e.getStackTrace().toString());
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connect_to, menu);
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
