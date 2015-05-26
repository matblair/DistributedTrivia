package edu.distributedtrivia;

import android.app.Application;

import edu.distributedtrivia.paxos.PaxosHandler;

/**
 * Created by Mat on 27/05/15.
 */
public class NotifiableApplication extends Application {

    private NotifiableActivity currentActivity = null;
    private static NotifiableApplication instance;

    public synchronized void setNotifiable(NotifiableActivity notifiable) {
        System.out.println("Set notify as current " + notifiable);
        this.currentActivity = notifiable;
    }

    public synchronized void notifyCurrentActivity(PaxosHandler.Actions action) {
        if (currentActivity != null)
            currentActivity.notifyActivityFromBgThread(action);
    }


    public static NotifiableApplication getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

}
