package edu.distributedtrivia;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import edu.distributedtrivia.paxos.NameBroadcaster;
import edu.distributedtrivia.paxos.PaxosHandler;
import edu.distributedtrivia.paxos.PaxosListener;


public class ConnectTo extends NotifiableActivity {

    // View objects
    private EditText edtName;
    private Button btnName;
    private ListView lstNames;
    private MyArrayAdapter adapter;
    private String name;

    // Save the thread for broadcasting names
    private NameBroadcaster nameBroadcaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to);

        edtName = (EditText) this.findViewById(R.id.edtNameClient);
        btnName = (Button) this.findViewById(R.id.btnNameClient);
        lstNames = (ListView) this.findViewById(R.id.lstCombatants);

        // Do we need multicast lock here?
        WifiManager wim = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (wim != null) {
            WifiManager.MulticastLock mcLock = wim.createMulticastLock("msg");
            mcLock.acquire();

            adapter = new MyArrayAdapter(this, Globals.userNames);
            lstNames.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            lstNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = (String) adapter.getItem(position);
                    Toast.makeText(getApplicationContext(), item + " selected", Toast.LENGTH_LONG).show();
                }
            });

            adapter.notifyDataSetChanged();
            btnName.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // Updating on name
                    name = edtName.getText().toString();
                    btnName.setEnabled(false);
                    edtName.setEnabled(false);

                    Globals.userNames.add(name);
                    Globals.userPlayer = new Player(name);

                    // Start the paxos thread listener in the background
                    Thread proc = new Thread(new PaxosListener(name));
                    proc.start();

                    // Start broadcasting name
                    nameBroadcaster = new NameBroadcaster(name);
                    nameBroadcaster.start();

                    // Notify data stuff
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    // METHOD TO UPDATE
    public void notifyActivity(PaxosHandler.Actions action){
        switch(action){
            case START_GAME:
                // Move into new game
                nameBroadcaster.stopBroadcasting();
                // Start the new activity for the game
                Intent i = new Intent();
                i.setClass(ConnectTo.this, QuestionActivity.class);
                startActivity(i);
                break;
            case REFRESH:
                adapter.notifyDataSetChanged();
                break;
        }
    }
}
