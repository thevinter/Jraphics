package chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class Chat extends JFrame implements ActionListener {

	private static final long serialVersionUID = 2489294504830185360L;
	String name;
	InetAddress iadr;
	int port;
	MulticastSocket so;
	JTextArea txt = new JTextArea();
	JScrollPane sp = new JScrollPane(txt);
	JTextField write = new JTextField();
	JButton quit = new JButton("Go Offline");
	public Chat(String username, String groupAdr, int portNr) throws IOException {
		name = username;
		iadr = InetAddress.getByName(groupAdr);
		port = portNr;
		so = new MulticastSocket(port);
		so.joinGroup(iadr);
		new Receiver(so,txt);
		sendMess("Online");
		setTitle("Chatting with "+ name);
		txt.setEditable(true);
		add(quit,BorderLayout.NORTH);
		add(sp,BorderLayout.CENTER);
		add(write,BorderLayout.SOUTH);
		quit.addActionListener(this);
		write.addActionListener(this);
		setSize(400,250);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void sendMess(String s) {
		byte[] data = (name + ": " + s).getBytes();
		DatagramPacket packet = new DatagramPacket(data,data.length,iadr,port);
		try {
			so.send(packet);
		}
		catch(IOException ie) {
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(null, "Data overflow !");
		}
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==write) {
			sendMess(write.getText());
			write.setText("");
		}
		else if(e.getSource()==quit) {
			sendMess("Offline");
			try {
				so.leaveGroup(iadr);
			}
			catch(IOException ie) {
				Toolkit.getDefaultToolkit().beep();
				JOptionPane.showMessageDialog(null, "Data overflow, connection error !");
			}
			so.close();
			dispose();
			System.exit(0);
		}
	}

	public static void main(String[] arg) throws IOException {
		String in;
		in = JOptionPane.showInputDialog(null,"What's your name?");
		if(arg.length>0) {
			in = arg[0];			
		}
		if(in == null || in.isEmpty()) {
			System.exit(0);
		}
		new Chat(in,"224.0.0.7",9876);
		
	}


	class Receiver implements Runnable {
		Thread activity = new Thread(this);
		MulticastSocket so;
		JTextArea txt;
		public Receiver(MulticastSocket sock, JTextArea txtAr) {
			so = sock;
			txt = txtAr;
			activity.start();
		}

		@Override
		public void run() {
			byte[] data = new byte[1024];
			while(true) {
				try {
					DatagramPacket packet = new DatagramPacket(data,data.length);
					so.receive(packet);
					String mess = new String(data,0,packet.getLength());
					txt.append(mess+ "\n");
				} catch(IOException e) {
					break;
				}
			}
		}
	}
}
