package jraphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileSystemView;

public class GUI extends JFrame {

	private JButton openFileBtn = new JButton("Open File");
	private JButton howToUseBtn = new JButton("Command List");
	private JButton exitBtn = new JButton("Exit");
	private JButton startBtn = new JButton("Start!");
	private JLabel fileNameLbl = new JLabel("WELCOME TO JRAPHICS");
	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMI = new JMenu("File");
	private JMenuItem openFileMenu = new JMenuItem("Open File");
	private JSeparator separator = new JSeparator();
	private JMenuItem exitMenu = new JMenuItem("Exit");
	private JMenu reportMI = new JMenu("Report");
	private JMenuItem generateReportMenu = new JMenuItem("Generate Report");
	private JMenu helpMI = new JMenu("Help");
	private JMenuItem aboutMenu = new JMenuItem("About");
	private JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
	private JPanel panel1 = new JPanel(new BorderLayout());
	private JPanel panel2 = new JPanel(new GridLayout(1, 2, 10, 10));
	private JPanel panel3 = new JPanel(new GridLayout(1, 2, 10, 10));
	private File file;
	private boolean go = false;
	

	static void show3D(File file) {
		
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
		jraphics.meshCube.loadFromObject(file.getAbsolutePath());
		jraphics.matProj = AlgebraUtility.MatrixMakeProjection(90.0, (double)jraphics.getSize().width / (double)jraphics.getSize().height, 0.1, 1000.0);
		jraphics.frame = frame;
		//Main loop that keeps painting the screen and calculating the "rotation angle"
		jraphics.gameLoop();
		
	}
	
	void createGUI() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(600, 600));
        setResizable(false);

        setLayout(new BorderLayout(10, 10));

        fileMI.add(openFileMenu);
        fileMI.add(separator);
        fileMI.add(exitMenu);
        

        reportMI.add(generateReportMenu);

        helpMI.add(aboutMenu);

        menuBar.add(fileMI);
        menuBar.add(reportMI);
        menuBar.add(helpMI);

        setJMenuBar(menuBar);

        panel1.add(startBtn);

        panel2.add(fileNameLbl);
        panel2.add(openFileBtn);

        panel3.add(howToUseBtn);
        panel3.add(exitBtn);

        mainPanel.add(panel2, BorderLayout.NORTH);
        mainPanel.add(panel1, BorderLayout.CENTER);
        mainPanel.add(panel3, BorderLayout.SOUTH);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);
        
        exitBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

            	System.exit(0);
            }
        });
        startBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	go = true;
            }
        });
        howToUseBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

            	HowToUse();
            }
        });
        openFileBtn.addActionListener(new ActionListener() {
        	
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		
        		file = fileChooser();
        	}
        });
	}
	
	

	private static  File fileChooser() {
		
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		int returnValue = jfc.showOpenDialog(null);
		
		if(returnValue == JFileChooser.APPROVE_OPTION) {
			return jfc.getSelectedFile();
		}
		else return null;
	}
	
	private JFrame HowToUse() {
    	
    	JFrame f = new JFrame("A JFrame");
        f.setSize(250, 250);
        f.setLocation(300,200);
        final JTextArea textArea = new JTextArea(10, 40);
        f.getContentPane().add(BorderLayout.CENTER, textArea);
        textArea.append("proprio qui caro Nikita");
        f.setVisible(true);
		return f;
    }
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public boolean isGo() {
		return go;
	}

}
