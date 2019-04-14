package jraphics;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.net.*;

public class Client extends JPanel implements Runnable {
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String msg = "";
	private String serverIp;
	Socket socket;
	private static final int PORT = 4444;
	
	public Client() {
		super();
		serverIp = "127.0.0.1";
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						sendMessage(e.getActionCommand());
						userText.setText("");
					}
				}
		);
		add(userText, BorderLayout.SOUTH);
		chatWindow = new JTextArea();
		chatWindow.setEditable(false);
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(400,200);
		setVisible(true);
	}
	
	public void startRunning() {
		try {
			connectToServer();
			setupStream();
			chatting();
		}catch(EOFException eofExeption) {
			showMessage("\nconnessione terminata");
		}catch(IOException ioException ) {
			ioException.printStackTrace();
		}finally {
			closeEverything();
		}
	}
	
	private void connectToServer()throws IOException {
		showMessage(".....\n");
		socket = new Socket(InetAddress.getByName(serverIp), PORT);
		showMessage("connesso a : "+ socket.getInetAddress().getHostName());
	}
	
	private void setupStream() throws IOException{
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(socket.getInputStream());
		showMessage("\n stream pronti \n");
	}
	
	private void chatting() throws IOException {
		ableToType(true);
		do {
			try {
				msg = (String) in.readObject();
				showMessage("\n" + msg);
			}catch(ClassNotFoundException classNotFoundException) {
				showMessage("\n wat \n");
			}
		}while(!msg.equals("fine"));
	}
	
	private void closeEverything() {
		showMessage("addio");
		ableToType(false);
		try {
			out.close();
			in.close();
			socket.close();
		}catch(IOException e) {
			showMessage("fuck");
		}
	}
	
	public void sendMessage(String msg) {
		try{
			out.writeObject("\nclient: " + msg );
			out.flush();
			showMessage("\n you: " + msg);
		}catch(IOException e) {
			chatWindow.append("\n errore nell'invio");
		}
	}
	
	private void showMessage(String toShow){
		SwingUtilities.invokeLater(
					new Runnable() {
						public void run() {
							chatWindow.append(toShow) ;
						}
					}
				);
	}
	
	private void ableToType(Boolean isAble) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						userText.setEditable(isAble);
					}
				}
			);
	}

	@Override
	public void run() {
		startRunning();
		
	}
	
}
