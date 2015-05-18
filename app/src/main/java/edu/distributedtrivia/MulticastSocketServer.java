package edu.distributedtrivia;

/**
 * Created by devneetsinghvirdi on 18/05/15.
 */

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MulticastSocketServer extends Thread {


    final static int PORT = 8888;
    final static String INET_ADDR = "225.4.5.6";
    //MulticastSocket are limited, specifically in the range of 224.0.0.0 to 239.255.255.255.


    public void run() {
        try {
            InetAddress addr = InetAddress.getByName(INET_ADDR);

            DatagramSocket serverSocket = new DatagramSocket();

            String msg = "Hello how r u?";

            // Create a packet that will contain the data
            // (in the form of bytes) and send it.
            DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),
                    msg.getBytes().length, addr, PORT);
            serverSocket.send(msgPacket);

            System.out.println("Server sent packet with msg: " + msg);
            Log.d("OUTPUT", "Server sent packet with msg: " + msg);
            //Toast.makeText(context, "Server sent packet with msg: " + msg, Toast.LENGTH_SHORT).show();
            //Thread.sleep(500);


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}