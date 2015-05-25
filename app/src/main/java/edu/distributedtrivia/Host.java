package edu.distributedtrivia;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.List;


public class Host extends ActionBarActivity {

    int numOfRounds;

    TextView numQuestionsField;
    Button startGame;
    Button startStupid;
    Button increase;
    Button decrease;
    EditText edtName;
    Button btnName;
    ListView lstNames;
    String name;
    MyArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        edtName = (EditText) this.findViewById(R.id.edtName);
        btnName = (Button) this.findViewById(R.id.btnName);
        lstNames = (ListView) this.findViewById(R.id.lstContestants);
//        txtIP = (TextView) this.findViewById(R.id.txtIP);
        adapter = new MyArrayAdapter(this, Globals.userNames);
        lstNames.setAdapter(adapter);

        numOfRounds = Globals.DEFAULT_ROUNDS;
        Globals.gs = new GameState(this);

        // Check for WiFi connectivity
        ConnectivityManager connManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi == null || !mWifi.isConnected()) {
            Log.d("Error", "Sorry! You need to be in a WiFi network in order to send UDP multicast packets. Aborting.");
            return;
        }

        startMyTask(new MulticastClient());
        adapter.notifyDataSetChanged();

        String ip = wifiIpAddress(this);

        btnName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                name = edtName.getText().toString();
                btnName.setEnabled(false);
                edtName.setEnabled(false);

                Globals.userNames.add(name);
                Globals.userPlayer = new Player(name);
                startMyTask(new MulticastServer());

                adapter.notifyDataSetChanged();
            }
        });


        lstNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String item = (String) adapter.getItem(position);
                Toast.makeText(getApplicationContext(), item + " selected", Toast.LENGTH_LONG).show();
            }
        });

        numQuestionsField = (TextView) this.findViewById(R.id.numberOfRounds);
        numQuestionsField.setText(Integer.toString(numOfRounds));

        increase = (Button) this.findViewById(R.id.increaseQuestions);
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numOfRounds != Globals.MAX_ROUNDS) {
                    numOfRounds++;
                    numQuestionsField.setText(Integer.toString(numOfRounds));
                }
            }
        });

        decrease = (Button) this.findViewById(R.id.decreaseQuestions);
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numOfRounds != Globals.MIN_ROUNDS) {
                    numOfRounds--;
                    numQuestionsField.setText(Integer.toString(numOfRounds));
                }
            }
        });

        startGame = (Button) this.findViewById(R.id.btnStart);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.gs.gameSetup(numOfRounds, true);
                Intent i = new Intent();
                i.setClass(Host.this, QuestionActivity.class);
                startActivity(i);
            }
        });

        startStupid = (Button) this.findViewById(R.id.btnStartStupid);
        startStupid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.gs.gameSetup(numOfRounds, false);
                Intent i = new Intent();
                i.setClass(Host.this, QuestionActivity.class);
                startActivity(i);
            }
        });



//        startMyTask(new MulticastServer());

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        // API 11
    void startMyTask(AsyncTask<List<String>, List<String>, List<String>> asyncTask) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Globals.userNames);
        else
            asyncTask.execute(Globals.userNames);
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
                    Globals.userNames.clear();
                    while (in.available() > 0) {

                        String line = in.readUTF();
                        if (!line.equalsIgnoreCase(""))
                            Globals.userNames.add(line);
                        else
                            break;
                    }
//                Log.d("OUTPUT", "Socket received msg: " + lstNames.toString());
                    publishProgress(Globals.userNames);
                }
            } catch (UnknownHostException e) {
                Log.d("ERROR", e.toString());
            } catch (IOException e) {
                Log.d("ERROR", e.toString());
            }

            return Globals.userNames;
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
                for (String element : Globals.userNames) {
                    out.writeUTF(element);
                }
                byte[] bytes = baos.toByteArray();

                DatagramPacket msgPacket = new DatagramPacket(bytes, bytes.length, addr, PORT);
                serverSocket.send(msgPacket);

//            Log.d("OUTPUT", "Server sent packet with msg: " + Host.lstNames.toString());

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return Globals.userNames;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(List<String> lstNames) {
            super.onPostExecute(lstNames);

//        Toast.makeText(, "Server sent packet with msg: " + lstNames.toString(), Toast.LENGTH_SHORT).show();

//        Host.adapter.notifyDataSetChanged();
//        Log.d("SENT", "Server sent packet with msg: " + lstNames.toString());
        }
    }//Async Host
}//Host
