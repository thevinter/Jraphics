package jraphics;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

class GUI extends JFrame {
	
		private JButton openFileBtn = new JButton("Open File") ; // reference to the button object
		private JButton howToUseBtn = new JButton("Command List");
		private JButton exitBtn = new JButton("Exit");
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
		
	  // constructor for ButtonFrame
		
	 

	GUI(String title) {
		  
	    super("JRAPHICS");     // invoke the JFrame constructor
	    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );  
	    setSize(new Dimension(600, 600));
        setResizable(false);
        setLayout(new BorderLayout(10, 10));      // set the layout manager
        
	    fileMI.add(openFileMenu);
        fileMI.add(separator);
        fileMI.add(exitMenu);
        

        reportMI.add(generateReportMenu);

        helpMI.add(aboutMenu);

        menuBar.add(fileMI);
        menuBar.add(reportMI);
        menuBar.add(helpMI);

        setJMenuBar(menuBar);

        panel2.add(fileNameLbl);
        panel2.add(openFileBtn);

        panel3.add(howToUseBtn);
        panel3.add(exitBtn);

        mainPanel.add(panel2, BorderLayout.NORTH);
        mainPanel.add(panel1, BorderLayout.CENTER);
        mainPanel.add(panel3, BorderLayout.SOUTH);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);
	    
	    openFileBtn.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e)
	    	  {
	    		System.out.println("we");
	    		file = fileChooser();
	    		if(!file.equals(null))
	    			Main.selected = true;
	    	  }
	    });

	    howToUseBtn.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e)
	    	  {
	    		JFrame f = new JFrame("Command List");
	    		JFrame.setDefaultLookAndFeelDecorated(true);
		        f.setSize(250, 250);
		        f.setLocation(300,200);
		        final JTextArea textArea = new JTextArea(10, 40);
		        f.getContentPane().add(BorderLayout.CENTER, textArea);
		        textArea.append("");
		        f.setVisible(true);
	    	  }
	    });

	    exitBtn.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e)
	    	  {
	    		System.exit(0);
	    	  }
	    });

	    aboutMenu.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e)
	    	  {
	    		JFrame f = new JFrame("About");
	    		JFrame.setDefaultLookAndFeelDecorated(true);
		        f.setSize(1000, 200);
		        f.setLocation(300,200);
		        final JTextArea textArea = new JTextArea(800, 500);
		        f.getContentPane().add(BorderLayout.CENTER, textArea);
		        textArea.append("This engine allows you to render any .obj file using basic Java Swing libraries.\r\n" + 
		        		"\r\n" + 
		        		"The mesh has to be made only of triangles since the engine is incapable of rendering quads.\r\n" + 
		        		"\r\n" + 
		        		"Currently there's no public library aviable so it's hard to use it in your own project but I' ll try to implement and share one soon for public use.\r\n" + 
		        		"\r\n" + 
		        		"See this as a recreational project, it's probably not optimized enough, but it has culling, clipping, and a basic camera implementation.");
		        f.setVisible(true);
	    	  }
	    });
	    
	    exitMenu.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e)
	    	  {
	    		System.exit(0);
	    	  }
	    });
	    
	    openFileMenu.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e)
	    	  {
	    		System.out.println("we");
	    		file = fileChooser();
	    		if(!file.equals(null))
	    			Main.selected = true;
	    	  }
	    });
	    
	    generateReportMenu.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e)
	    	  {
	    		JFrame f = new JFrame("Report");
	    		JFrame.setDefaultLookAndFeelDecorated(true);
		        f.setSize(70, 70);
		        f.setLocation(300,200);
		        final JTextArea textArea = new JTextArea(800, 500);
		        f.getContentPane().add(BorderLayout.CENTER, textArea);
		        textArea.append("YOU CAN'T!!!");
		        f.setVisible(true);
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
	  
	  public File getFile() {
			return file;
		}
}
