package edu.distributedtrivia.paxos;

/**
 * Created by Mat on 26/05/15.
 *
 */
public class PaxosListener implements Runnable {
    // We use a paxos multicast socket for this
    private PaxosSocket socket;
    private PaxosHandler handler;

    // Create a new paxos lisetner that is listening on a multicast port
    public PaxosListener(String currentID) {
        socket = new PaxosSocket();
        handler = PaxosHandler.getHandler(currentID);
    }

    public void run(){
        while (true) {
            // Receive a message (BLOCKS ON IO)
            PaxosMessage message = socket.receiveMessage();
            System.out.println("Receieved a message with JSON " + message.toJson() + " in device " + handler.getSenderID());
            // Send to be handled by the appropriate context here
            handler.handleMessage(message);
        }
    }

}