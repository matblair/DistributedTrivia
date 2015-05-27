package edu.distributedtrivia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import edu.distributedtrivia.paxos.PaxosHandler;

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
                Intent i = new Intent();
                i.setClass(MainActivity.this, AboutActivity.class);
                startActivity(i);
            }
        });
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        PaxosHandler.reset();
//
////        if(Globals.userNames != null) {
////            Globals.userNames.clear();
////        }
////        if(Globals.gs != null) {
////            Globals.gs = null;
////        }
//    }

}


