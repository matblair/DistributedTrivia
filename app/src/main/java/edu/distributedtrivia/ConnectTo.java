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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.List;


public class ConnectTo extends ActionBarActivity {

    EditText edtName;
    Button btnName;
    ListView lstNames;
    MyArrayAdapter adapter;
    String name, params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to);

        edtName = (EditText) this.findViewById(R.id.edtNameClient);
        btnName = (Button) this.findViewById(R.id.btnNameClient);
        lstNames = (ListView) this.findViewById(R.id.lstCombatants);

        WifiManager wim = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (wim != null) {
            WifiManager.MulticastLock mcLock = wim.createMulticastLock("msg");
            mcLock.acquire();

            adapter = new MyArrayAdapter(this, MainActivity.userNames);
            lstNames.setAdapter(adapter);
            adapter.notifyDataSetChanged();


            lstNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String item = (String) adapter.getItem(position);
                    Toast.makeText(getApplicationContext(), item + " selected", Toast.LENGTH_LONG).show();
                }
            });


            startMyTask(new MulticastClient());
            adapter.notifyDataSetChanged();

            btnName.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    name = edtName.getText().toString();
                    btnName.setEnabled(false);
                    edtName.setEnabled(false);

                    MainActivity.userNames.add(name);
                    startMyTask(new MulticastServer());
                    adapter.notifyDataSetChanged();
                }
            });

        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        // API 11
    void startMyTask(AsyncTask<List<String>, List<String>, List<String>> asyncTask) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, MainActivity.userNames);
        else
            asyncTask.execute(MainActivity.userNames);
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


    public class MulticastClient extends AsyncTask<List<String>, List<String>, List<String>> {

        final static String INET_ADDR = "225.4.5.6";
        final static int PORT = 8888;
        String msg;

        @Override
        protected List<String> doInBackground(List<String>... params) {

            try {
                InetAddress address = InetAddress.getByName(INET_ADDR);

                byte[] buf = new byte[1024];

                DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
                MulticastSocket clientSocket;

                clientSocket = new MulticastSocket(PORT);
                clientSocket.joinGroup(address);

                while (true) {
                    clientSocket.receive(msgPacket);

                    // read from byte array
                    ByteArrayInputStream bais = new ByteArrayInputStream(msgPacket.getData());
                    DataInputStream in = new DataInputStream(bais);
                    MainActivity.userNames.clear();
                    while (in.available() > 0) {

                        String line = in.readUTF();
                        if (!line.equalsIgnoreCase(""))
                            MainActivity.userNames.add(line);
                        else
                            break;
                    }
//                Log.d("OUTPUT", "Socket received msg: " + lstNames.toString());
                    publishProgress(MainActivity.userNames);
                }
            } catch (UnknownHostException e) {
                Log.d("ERROR", e.toString());
            } catch (IOException e) {
                Log.d("ERROR", e.toString());
            }

            return MainActivity.userNames;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(List<String>... values) {
            super.onProgressUpdate(values);

            adapter.notifyDataSetChanged();
//        Toast.makeText(getApplicationContext(), "Socket received msg: " + values[0].toString(), Toast.LENGTH_SHORT).show();
//
//        int numberOfLevels=5;
//        final WifiManager WifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wifiInfo = WifiManager.getConnectionInfo();
//        int level=WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
//
//        Toast.makeText(getApplicationContext(), "Signal strength: " + level, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(List<String> list) {
            super.onPostExecute(list);
        }
    }//Async Client


    public class MulticastServer extends AsyncTask<List<String>, List<String>, List<String>> {


        final static int PORT = 8888;
        final static String INET_ADDR = "225.4.5.6";


        @Override
        protected List<String> doInBackground(List<String>... params) {

            try {
                InetAddress addr = InetAddress.getByName(INET_ADDR);

                DatagramSocket serverSocket = new DatagramSocket();

                // write to byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(baos);
                for (String element : MainActivity.userNames) {
                    out.writeUTF(element);
                }
                byte[] bytes = baos.toByteArray();

                DatagramPacket msgPacket = new DatagramPacket(bytes, bytes.length, addr, PORT);
                serverSocket.send(msgPacket);

//            Log.d("OUTPUT", "Server sent packet with msg: " + Host.lstNames.toString());

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return MainActivity.userNames;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(List<String> lstNames) {
            super.onPostExecute(lstNames);

//        Toast.makeText(, "Server sent packet with msg: " + lstNames.toString(), Toast.LENGTH_SHORT).show();

            adapter.notifyDataSetChanged();
//        Log.d("SENT", "Server sent packet with msg: " + lstNames.toString());
        }
    }//Async Host
}
