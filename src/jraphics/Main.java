	package jraphics;
	
	import java.awt.Color;
	
	import javax.swing.JFrame;
	
	/**
	 * The main class of the program. It creates the swing frames, loads the object from a file and initializes all the required methods
	 * classes and listeners
	 * 
	 * @author Nikita Brancatisano, Nicola Bettinzoli, Alex Cominelli
	 */
	public class Main {
		
		public static final String FILE_LOCATION = "D:\\Blender\\teapot.obj";
		
		public static void main(String[] args) {
			//Creation of a Frame
			JFrame.setDefaultLookAndFeelDecorated(true);
			JFrame frame =  new JFrame("Graphics");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setBackground(Color.white);
			frame.setSize(500,500);
			
			//Adding a Panel to the Frame
			Jraphics jraphics = new Jraphics();
			Listener l = new Listener(jraphics);
			frame.add(jraphics);
			frame.setVisible(true);
			frame.addKeyListener(l);
			
			//Creation of a Mesh from an .obj file and its projection using a projection matrix
			jraphics.meshCube = new Mesh();
			jraphics.meshCube.loadFromObject(FILE_LOCATION);
			jraphics.matProj = AlgebraUtility.MatrixMakeProjection(90.0, (double)jraphics.getSize().width / (double)jraphics.getSize().height, 0.1, 1000.0);
			jraphics.frame = frame;
			//Main loop that keeps painting the screen and calculating the "rotation angle"
			jraphics.gameLoop();
		}
	}