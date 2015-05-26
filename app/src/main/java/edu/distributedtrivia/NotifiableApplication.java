package edu.distributedtrivia;

import android.app.Application;

/**
 * Created by Mat on 27/05/15.
 */
public class NotifiableApplication extends Application {

    private NotifiableActivity currentActivity = null;

    public synchronized void setNotifiable(NotifiableActivity notifiable) {
        this.currentActivity = notifiable;
    }

    private synchronized void notifyCurrentActivity() {
        if (currentActivity != null)
            currentActivity.notifyActivityFromBgThread();
    }

}
