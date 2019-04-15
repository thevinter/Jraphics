	package jraphics;
	
	import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

	
	public class Main {
		
		public static void main(String[] args) {
			//Creation of a Frame
			JFrame.setDefaultLookAndFeelDecorated(true);
			JFrame frame =  new JFrame("Graphics");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setBackground(Color.white);
			frame.setSize(500,500);
			
			//Adding a Panel to the Frame
			Jraphics jraphics = new Jraphics();
			JPanel panel = new JPanel();
			
			
			
			Listener l = new Listener(jraphics);
			frame.add(jraphics, BorderLayout.WEST);
			panel.setBackground(Color.blue);
			panel.setVisible(false);
			frame.add(panel, BorderLayout.EAST);
			frame.setVisible(true);
			frame.addKeyListener(l);
			
			JButton btn1 = new JButton("conection");
			jraphics.add(btn1);
			
			btn1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					panel.setVisible(true);
				}
			});
						
			JButton startServer = new JButton("server");
			startServer.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					Server server = new Server();
					panel.add(server);
					server.startRunning();
				}
			});
				
			JButton startClient = new JButton("client");
			startClient.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					Client client = new Client();
					panel.add(client);
					client.startRunning();
				}
			});
			
			panel.add(startServer);
			panel.add(startClient);

			
			//Creation of a Mesh from an .obj file and its projection using a projection matrix
			jraphics.meshCube = new Mesh();
			jraphics.meshCube.loadFromObject("D:\\Blender\\queen.obj");
			jraphics.matProj = AlgebraUtility.MatrixMakeProjection(90.0, (double)jraphics.getSize().width / (double)jraphics.getSize().height, 0.1, 1000.0);
			jraphics.frame = frame;
			//Main loop that keeps painting the screen and calculating the "rotation angle"

			jraphics.gameLoop();
			
			
			
		}
	}
