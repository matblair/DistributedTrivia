package edu.distributedtrivia.paxos;

/**
 * Created by Mat on 27/05/15.
 * Basic class that allows us to continuously let everyone know who wants to play
 */
public class NameBroadcaster extends Thread {

    // Our private name and broadcast flag
    private String name;
    private Boolean broadcast;

    // Constructor
    public NameBroadcaster(String name){
        this.name = name;
        this.broadcast = true;
    }

    // Broadcasting names
    public void run(){
       while(broadcast){
           try {
               // Send name
               PaxosHandler handler = PaxosHandler.getHandler(name);
               handler.sendName(name);

               // Sleep 3 seconds and send again
               Thread.sleep(3000);
           } catch(Exception e){
               e.printStackTrace();
           }
        }
    }

    // Stop the broadcasting
    public void stopBroadcasting(){
        broadcast = false;
    }
}