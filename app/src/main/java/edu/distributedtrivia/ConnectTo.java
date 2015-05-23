package edu.distributedtrivia;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;


public class ConnectTo extends ActionBarActivity {

    EditText edtName;
    Button btnName;
    Boolean flag = false;
    String name, params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to);

        edtName = (EditText) this.findViewById(R.id.edtName);
        btnName = (Button) this.findViewById(R.id.btnName);

        WifiManager wim = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (wim != null) {
            WifiManager.MulticastLock mcLock = wim.createMulticastLock("msg");
            mcLock.acquire();

            startMyTask(new MulticastClient());

        }

        btnName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!flag) {
                    name = edtName.getText().toString();
                    btnName.setText("Edit Name");
                    flag = true;
                    edtName.setEnabled(false);
                } else {
                    btnName.setText("Set Name");
                    flag = false;
                    edtName.setEnabled(true);
                }
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        // API 11
    void startMyTask(AsyncTask<String, ?, ?> asyncTask) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            asyncTask.execute();
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


    public class MulticastClient extends AsyncTask<String, String, String> {

        final static String INET_ADDR = "225.4.5.6";
        final static int PORT = 8888;
        String msg;

        @Override
        protected String doInBackground(String... params) {

            try {
                InetAddress address = InetAddress.getByName(INET_ADDR);

                byte[] buf = new byte[1024];

                DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
                MulticastSocket clientSocket;

                clientSocket = new MulticastSocket(PORT);
                clientSocket.joinGroup(address);

                while (true) {
                    clientSocket.receive(msgPacket);

                    msg = new String(msgPacket.getData(), 0, msgPacket.getLength());
                    Log.d("OUTPUT", "Socket received msg: " + msg);

                    publishProgress(msg);
                }

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
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            Toast.makeText(getApplicationContext(), "Socket received msg: " + values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
