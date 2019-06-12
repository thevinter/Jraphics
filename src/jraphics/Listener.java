package jraphics;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * A simple KeyListener that registers WASD movement as well as the key based one.
 * 
 * @author Nikita Brancatisano, Nicola Bettinzoli, Alex Cominelli
 */
public class Listener implements KeyListener {
	
	public Jraphics j;
	
	public Listener(Jraphics j) {
		this.j = j;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			j.updateCameraX(1);
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
        	j.updateCameraX(-1);
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
        	j.updateCameraY(1);
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
        	j.updateCameraY(-1);
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
        	j.move(1);
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
        	j.setYaw(1);
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
        	j.move(-1);
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
        	j.setYaw(-1);
        }
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
