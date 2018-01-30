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
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Juan Carlos on 30/01/2018.
 */

public class Server implements Runnable {

    Handler mHandler =null;

    private boolean go=true;

    public Server(Handler handler){
        mHandler=handler;
    }


    public void sendMessage(String text){
        Message completeMessage =
                mHandler.obtainMessage(1);
        Bundle data = new Bundle();
        data.putString("message",text);
        completeMessage.setData(data);
        completeMessage.sendToTarget();
    }

    public void stopServer(){
        go=false;
    }
    public void run() {


        try {
            sendMessage("Host Address [" + InetAddress.getLocalHost().getHostName() + "] " + InetAddress.getLocalHost().getHostAddress());
            Enumeration<NetworkInterface> ni;

            ni = NetworkInterface.getNetworkInterfaces();

            InetAddress wlan=null;
            int index = 0;
            while (ni.hasMoreElements()) {
                NetworkInterface i = ni.nextElement();
                if (i != null) {
                    sendMessage("Interface [" + index + "]:" + i.getDisplayName());


                    Enumeration<InetAddress> addr = i.getInetAddresses();

                    while (addr.hasMoreElements()) {
                        InetAddress address= addr.nextElement();
                        sendMessage("\t" + address.getHostAddress());

                        //if(i.getDisplayName().equalsIgnoreCase("wlan0"))


                    }

                }
                sendMessage("***");
                index++;
            }


            InetSocketAddress isa = new InetSocketAddress(InetAddress.getByName("192.168.1.8"), 20000);
            sendMessage("Listening in " + isa.toString());
            DatagramSocket dsocket = new DatagramSocket(isa);
            byte[] buffer = new byte[1024];
            DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
            int n = 0;
            while (go) {
                dsocket.receive(datagram);
                sendMessage("[" + n + "] Datagram from " + datagram.getAddress()+" DATA:"+new String(datagram.getData()));
                n++;
            }
        } catch (SocketException ex) {
            Log.e("server", ex.getMessage());
        } catch (IOException ex) {
            Log.e("server", ex.getMessage());
        }
    }


}