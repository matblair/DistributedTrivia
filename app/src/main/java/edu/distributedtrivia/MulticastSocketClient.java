package edu.distributedtrivia;

/**
 * Created by devneetsinghvirdi on 18/05/15.
 */

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastSocketClient extends Thread {


    final static String INET_ADDR = "225.4.5.6";
    final static int PORT = 8888;


    public void run() {

        String msg = "";

        try {
            // Get the address that we are going to connect to.
            InetAddress address = InetAddress.getByName(INET_ADDR);

            // Create a buffer of bytes, which will be used to store
            // the incoming bytes containing the information from the server.
            // Since the message is small here, 256 bytes should be enough.
            byte[] buf = new byte[1024];


            // Create a new Multicast socket (that will allow other sockets/programs
            // to join it as well.


            DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
            MulticastSocket clientSocket;


            clientSocket = new MulticastSocket(PORT);
            //Joint the Multicast group.
            clientSocket.joinGroup(address);

            while (true) {
                // Receive the information and print it.

                clientSocket.receive(msgPacket);

                msg = new String(buf, 0, buf.length);
                //System.out.println("Socket received msg: " + msg);
                Log.d("OUTPUT", "Socket received msg: " + msg);
                //Toast.makeText(context , "Socket received msg: " + msg, Toast.LENGTH_SHORT).show();

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
