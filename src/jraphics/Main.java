package jraphics;
	
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;


	
	/**
	 * The main class of the program. It creates the swing frames, loads the object from a file and initializes all the required methods
	 * classes and listeners
	 * 
	 * @author Nikita Brancatisano, Nicola Bettinzoli, Alex Cominelli
	 */
	public class Main {
		
		public static void main(String[] args) {
			
			GUI gui = new GUI();
			gui.createGUI();
			gui.setVisible(true);
			
			while(gui.isGo() == false) {
				gui.setVisible(true);
			}
				GUI.show3D(gui.getFile());
		}
	}