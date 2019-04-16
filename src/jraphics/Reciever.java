package jraphics;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import javax.swing.JTextArea;

public class Reciever implements Runnable{
	 Thread activity = new Thread(this);
     MulticastSocket so;
     JTextArea txt;
     public Reciever(MulticastSocket sock, JTextArea txtAr) {
    	 so = sock;
    	 txt = txtAr;
    	 activity.start();
     }
     
     public void run() {
         byte[] data = new byte[1024];
         while(true) {
                 try {
                         DatagramPacket packet = new DatagramPacket(data,data.length);
                         so.receive(packet);
                         String mess = new String(data,0,packet.getLength());
                         txt.append(mess+ "\n");
                 }catch(IOException e) {
                	 break;
                 }
     	}
	}
}
