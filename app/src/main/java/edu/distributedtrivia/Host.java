package edu.distributedtrivia;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import edu.distributedtrivia.paxos.NameBroadcaster;
import edu.distributedtrivia.paxos.PaxosHandler;
import edu.distributedtrivia.paxos.PaxosListener;

public class Host extends NotifiableActivity {

    // The number of rounds selected
    private int numOfRounds;

    // All the view objects
    private TextView numQuestionsField;
    private Button startGame;
    private Button startStupid;
    private Button increase;
    private Button decrease;
    private EditText edtName;
    private Button btnName;
    private ListView lstNames;
    private String name;
    private MyArrayAdapter adapter;

    // Save the thread
    private NameBroadcaster nameBroadcaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        // Our view activities
        edtName = (EditText) this.findViewById(R.id.edtName);
        btnName = (Button) this.findViewById(R.id.btnName);
        lstNames = (ListView) this.findViewById(R.id.lstContestants);
        adapter = new MyArrayAdapter(this, Globals.userNames);
        lstNames.setAdapter(adapter);

        // Set our default number of lines
        numOfRounds = Globals.DEFAULT_ROUNDS;
        Globals.gs = new GameState(this);

        // Check for WiFi connectivity
        ConnectivityManager connManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi == null || !mWifi.isConnected()) {
            Log.d("Error", "Sorry! You need to be in a WiFi network in order to send UDP multicast packets. Aborting.");
            return;
        }

        // Set data set
        adapter.notifyDataSetChanged();
        btnName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                name = edtName.getText().toString();
                btnName.setEnabled(false);
                edtName.setEnabled(false);
                startGame.setEnabled(true);
                startStupid.setEnabled(true);

                // Add to the global usernames
                Globals.userNames.add(name);
                Globals.userPlayer = new Player(name);

                // Start the paxos thread listener in the background
                Thread proc = new Thread(new PaxosListener(name));
                proc.start();

                // Start broadcasting name
                nameBroadcaster = new NameBroadcaster(name);
                nameBroadcaster.start();

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

        // Buttons to increase or decrease number of questions
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

        // This button will use paxos
        startGame = (Button) this.findViewById(R.id.btnStart);
        startGame.setEnabled(false);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generalisedSetup(true);

                // Start the new activity for the game
                Intent i = new Intent();
                i.setClass(Host.this, QuestionActivity.class);
                startActivity(i);
            }
        });


        // This button wont use paxos
        startStupid = (Button) this.findViewById(R.id.btnStartStupid);
        startStupid.setEnabled(false);
        startStupid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generalisedSetup(false);
                Intent i = new Intent();
                i.setClass(Host.this, QuestionActivity.class);
                startActivity(i);
            }
        });

    }

    private void generalisedSetup(Boolean paxos){
        nameBroadcaster.stopBroadcasting();
        Globals.gs.gameSetup(numOfRounds, paxos);
        PaxosHandler handler = PaxosHandler.getHandler(name);
        handler.setGameState(Globals.gs);
        handler.sendStart(paxos, numOfRounds);
    }

    // The public method to allow this activity to update. Doesn't do anything except update list
    public void notifyActivity(PaxosHandler.Actions action) {
        switch(action){
            case REFRESH:
                // We want to update people
                adapter.notifyDataSetChanged();
                break;
        }
    }
}

