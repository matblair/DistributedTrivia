package edu.distributedtrivia.paxos;

/**
 * Created by Mat on 26/05/15.
 *
 */
public class PaxosListener {
    // We use a paxos multicast socket for this
    private PaxosSocket socket;

    // Create a new paxos lisetner that is listening on a multicast port
    public PaxosListener() {
        socket = new PaxosSocket();
    }

    // Listen in a new background thread and make paxos handler handle it
    public void listen() {
        while (true) {
            // Receive a message (BLOCKS ON IO)
            PaxosMessage message = socket.receiveMessage();
            // Send to be handled by the appropriate context here
            // ???????????
        }
    }

}