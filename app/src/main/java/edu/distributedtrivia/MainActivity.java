package edu.distributedtrivia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


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
//                Toast.makeText(getApplicationContext(), "Join", Toast.LENGTH_SHORT).show();

            }
        });

        btnHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globals.host = true;
                Intent intent = new Intent(MainActivity.this, Host.class);
                startActivity(intent);

//                Toast.makeText(getApplicationContext(), "Host", Toast.LENGTH_SHORT).show();

            }
        });

    }
}
