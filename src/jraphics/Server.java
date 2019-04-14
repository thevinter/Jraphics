package jraphics;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;

public class Server extends JPanel implements Runnable {
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private ServerSocket server;
	private Socket socket;
	private final static int PORT = 4444;
	private Boolean isConnected = false;
	JButton startServer = new JButton("server");
	JButton startClient = new JButton("client");
	File model;
	//private JPanel panel = new JPanel();
	
	public Server() {
		super();
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
		add(startServer , BorderLayout.NORTH);
		add(startClient , BorderLayout.AFTER_LAST_LINE);
		chatWindow = new JTextArea();
		add(chatWindow, BorderLayout.CENTER);
		add(new JScrollPane(chatWindow));
		chatWindow.setEditable(false);
		chatWindow.setSize(200,500);
		setSize(500,500);
		setVisible(true);
		
		
	}
	
	public void startRunning() {
		try {
			server = new ServerSocket(PORT);
			while(true) {
				try {
					waitConnection();
					setupStream();
					chatting();
				}catch(EOFException eofExeption) {
					showMessage("f");
				}finally {
					closeEverything();
				}
			}
		}catch(IOException eoExeption) {
			showMessage("f");
		}
	}
	
	private void waitConnection() throws IOException {
		showMessage("sto aspettando qualcuno");
		socket = server.accept();
		showMessage("connessione con " + socket.getInetAddress().getHostName());
	}
	
	private void chatting() throws IOException {
		String msg = "sei connesso";
		sendMessage(msg);
		ableToType(true);
		do {
			try {
				msg = (String) in.readObject();
				showMessage("\n" + msg);
			}catch(ClassNotFoundException classNotFoundException) {
				showMessage("wat");
			}
		}while(!(msg.equals("fine")));
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
			out.writeObject("\nserver: " + msg );
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
	
	private void setupStream() throws IOException{
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(socket.getInputStream());
		
		showMessage("\n stream pronti \n");
	}
	
	

	private String fileReader(String directory) {
		model = new File(directory);
		String text = "";
		String temp = "";
		BufferedReader b ;
		try {
			b = new BufferedReader(new FileReader(model));
			while((temp = b.readLine()) != null) {
				text.concat(temp + "\n");
			}
				b.close();
		}
		catch(IOException e) {
			System.out.println("no ok");
		}
		return text;
	}

	@Override
	public void run() {
		startRunning();
	}
	
	
}
