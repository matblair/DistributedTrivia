package edu.distributedtrivia;

import android.support.v7.app.ActionBarActivity;

/**
 * Created by Mat on 27/05/15.
 */
public abstract class NotifiableActivity extends ActionBarActivity {

    // Our abstract method to notify they activity, each view should implement this.
    public abstract void notifyActivity();

    // Our concrete view to ensure notify activity runs in the UIThread
    public final void notifyActivityFromBgThread() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyActivity();
            }});
    }

    // Method to ensure the appropriate view gets updated at any given point
    @Override
    protected void onStart() {
        super.onStart();
        NotifiableApplication app = (NotifiableApplication)getApplication();
        app.setNotifiable(this);
    }

    @Override
    protected void onStop() {
        NotifiableApplication app = (NotifiableApplication)getApplication();
        app.setNotifiable(null);
        super.onStop();
    }



}
