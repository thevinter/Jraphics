package jraphics;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

class GUI extends JFrame
{
  JButton bChange ; // reference to the button object

  // constructor for ButtonFrame
  GUI(String title) 
  {
    super( title );                     // invoke the JFrame constructor
    setLayout( new FlowLayout() );      // set the layout manager

    bChange = new JButton("Click Me!"); // construct a JButton
    bChange.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e)
    	  {
    		System.out.println("we");
    		Main.selected=true;
    	  }
    });
    add( bChange );                     // add the button to the JFrame
    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );   
    
  }
}
