package edu.distributedtrivia;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;


public class Host extends ActionBarActivity {

    TextView txtIP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        // Check for WiFi connectivity
        ConnectivityManager connManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi == null || !mWifi.isConnected()) {
            Log.d("Error", "Sorry! You need to be in a WiFi network in order to send UDP multicast packets. Aborting.");
            return;
        }

        String ip = wifiIpAddress(this);

        txtIP = (TextView) this.findViewById(R.id.txtIP);
        txtIP.setText(ip);

        new MulticastServer().execute();

    }

    protected String wifiIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endian if needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = "Unable to get host address!";
        }

        return ipAddressString;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_host, menu);
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

    public class MulticastServer extends AsyncTask<String, Void, String> {


        final static int PORT = 8888;
        final static String INET_ADDR = "225.4.5.6";

        String msg = "Hello how r u?";


        @Override
        protected String doInBackground(String... params) {

            try {
                InetAddress addr = InetAddress.getByName(INET_ADDR);

                DatagramSocket serverSocket = new DatagramSocket();

                byte data[] = msg.toString().getBytes();

                DatagramPacket msgPacket = new DatagramPacket(data, data.length, addr, PORT);
                serverSocket.send(msgPacket);

                Log.d("OUTPUT", "Server sent packet with msg: " + msg);

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return msg;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Toast.makeText(getApplicationContext(), "Server sent packet with msg: " + s, Toast.LENGTH_SHORT).show();
        }
    }//Async
}//Host
