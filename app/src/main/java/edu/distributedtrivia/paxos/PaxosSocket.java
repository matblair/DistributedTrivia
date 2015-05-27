package edu.distributedtrivia.paxos;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.security.PublicKey;

/**
 * Created by Mat on 16/05/15.
 */
public class PaxosSocket {

    // Private variables for use in adressing
    private final static String INET_ADDR = "225.4.5.6";
    private final static int RETRY_TIMER = 300;
    private PaxosMessage retry;

    private InetAddress address;
    private final static int PORT = 8888;
    private int retryTimer;
    // Private variables for use in receiving;
    private MulticastSocket clientSocket;

    // Again a singleton

    public PaxosSocket (){
        try {
            address = InetAddress.getByName(INET_ADDR);
            clientSocket = new MulticastSocket(PORT);
            clientSocket.setLoopbackMode(true);
            clientSocket.joinGroup(address);
//            handleRetry();
            // Disable loopback server!
        } catch (UnknownHostException e) {
            System.out.println("Uh Oh...");
        } catch (IOException e) {
            System.out.println("What's up!");
        }
    }

    public PaxosMessage receiveMessage(){
        byte[] buf = new byte[1024];
        DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
        try {
            // Receive the next multicast packet
            clientSocket.receive(msgPacket);
        } catch (IOException e){
            System.out.println("Something brock in receiving stuff");
        }
        // Build the message out of it
        PaxosMessage m = PaxosMessage.buildFromJsonString(new String(msgPacket.getData()));
        return m;
    }

    public void sendMessage(PaxosMessage message){
        // Create the datagram packet
//        retry = message;
//        System.out.println("Sending message: " + message.toJson());
        byte[] buffer = message.toJson().getBytes();
        DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, PORT);
        // Try to send it
        try {
            clientSocket.send(request);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendBackgroundMessage(final PaxosMessage message){
        Thread t = new Thread(new Runnable(){
            @Override
            public void run() {
                sendMessage(message);
            }});
        t.start();
    }

//    private synchronized void setRetryTime(int retry){
//        retryTimer = retry;
//    }
//
//    private synchronized int getRetryTimer(){
//        return retryTimer;
//    }

//    private void handleRetry(){
//        Thread t = new Thread(new Runnable(){
//            @Override
//            public void run() {
//                try {
//                    while (true) {
//                        while (getRetryTimer() > 0) {
//                            System.out.println(" I AM RUNNING!");
//                            // Set the value on the text field!
//                            // Sleep for 10 ms
//                            Thread.sleep(1);
//                            setRetryTime(getRetryTimer() - 1);
//                            break;
//                        }
//                        if(retry != null) {
//                            sendMessage(retry);
//                            setRetryTime(RETRY_TIMER);
//                            retry = null;
//                        }
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        t.start();
//
//    }

}
