package es.ujaen.jccuevas.app.udpserver;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Juan Carlos on 30/01/2018.
 */

public class MulticastServer implements Runnable {

    Handler mHandler = null;

    private boolean go = true;

    public MulticastServer(Handler handler) {
        mHandler = handler;
    }


    public void sendMessage(String text) {
        Message completeMessage =
                mHandler.obtainMessage(1);
        Bundle data = new Bundle();
        data.putString("message", text);
        completeMessage.setData(data);
        completeMessage.sendToTarget();
    }

    public void stopServer() {
        go = false;
    }

    public void run() {


        try {
            sendMessage("Host Address [" + InetAddress.getLocalHost().getHostName() + "] " + InetAddress.getLocalHost().getHostAddress());

            InetSocketAddress isa = new InetSocketAddress(InetAddress.getByName("224.2.2.2"), 20000);
            InetAddress group = InetAddress.getByName("224.2.2.2");

            MulticastSocket mssocket = new MulticastSocket(20000);
            System.out.println("Joining group " + group.getHostAddress());
            mssocket.joinGroup(group);
            byte[] buffer = new byte[1024];
            DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
            int n = 0;

            sendMessage("Listening in " + group.toString());
            while (go) {
                mssocket.receive(datagram);
                sendMessage("[" + n + "] Datagram from " + datagram.getAddress() + " DATA:" + new String(datagram.getData()));
                n++;
            }

        } catch (SocketException ex) {
            Log.e("server", ex.getMessage());
        } catch (IOException ex) {
            Log.e("server", ex.getMessage());
        }
    }


}