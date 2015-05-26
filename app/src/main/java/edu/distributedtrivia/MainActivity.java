package edu.distributedtrivia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import edu.distributedtrivia.paxos.PaxosHandler;
import edu.distributedtrivia.paxos.PaxosListener;
import edu.distributedtrivia.paxos.PaxosMessage;


class TestSender implements Runnable {

    public TestSender(String lol){

    }

    public void run(){
        System.out.println("Doing shit");
        PaxosHandler handler = PaxosHandler.getHandler("John");
        PaxosMessage message = handler.proposeQuestionMsg(0);
        handler.proposeNewRound(message);
    }
}

public class MainActivity extends ActionBarActivity {


    Button btnJoin, btnHost, btnAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnJoin = (Button) this.findViewById(R.id.btnJoin);
        btnHost = (Button) this.findViewById(R.id.btnHost);
        btnAbout = (Button) this.findViewById(R.id.btnAbout);


        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globals.host = false;
                Intent intent = new Intent(MainActivity.this, ConnectTo.class);
                startActivity(intent);

            }
        });

        btnHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globals.host = true;
                Intent intent = new Intent(MainActivity.this, Host.class);
                startActivity(intent);


            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread proc = new Thread(new TestSender("hello"));
                proc.start();
//                Toast.makeText(getApplicationContext(), "Sent a message!", Toast.LENGTH_SHORT).show();

            }
        });


        Thread proc = new Thread(new PaxosListener("Left"));
        proc.start();

    }
}


