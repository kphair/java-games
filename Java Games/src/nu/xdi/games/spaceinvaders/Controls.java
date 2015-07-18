package nu.xdi.games.spaceinvaders;

import java.awt.event.KeyEvent;

import javax.swing.JComboBox.KeySelectionManager;

/**
 * @author Kevin Phair
 * @date 18 Jul 2015
 * @version 1.0.0
 */
public class Controls {

	private static final int LEFT = 0;
	private static final int RIGHT = 1;
	private static final int FIRE = 2;
	private static final int P1_START = 3;
	private static final int QUIT = 4;
	
	private static boolean keyStats[] = {
			false,		// Left 
			false, 		// Right
			false, 		// Fire
			false,		// Player 1 start
			false		// Quit game
			};

	/**
	 * Set items in the controls matrix via key press events
	 * 
	 * @param keyPressed event
	 */
	public static void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		switch(key) {
			case KeyEvent.VK_LEFT: keyStats[LEFT] = true; break;
			case KeyEvent.VK_RIGHT: keyStats[RIGHT] = true; break;
			case KeyEvent.VK_SPACE: keyStats[FIRE] = true; break;
			case KeyEvent.VK_1: keyStats[P1_START] = true; break;
			case KeyEvent.VK_ESCAPE: keyStats[QUIT] = true; break;
		}
	}
	
	/**
	 * Reset items in the controls matrix via key release events
	 * 
	 * @param keyReleased event
	 */
	public static void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		
		switch(key) {
			case KeyEvent.VK_LEFT: keyStats[LEFT] = false; break;
			case KeyEvent.VK_RIGHT: keyStats[RIGHT] = false; break;
			case KeyEvent.VK_SPACE: keyStats[FIRE] = false; break;
			case KeyEvent.VK_1: keyStats[P1_START] = false; break;
			case KeyEvent.VK_ESCAPE: keyStats[QUIT] = false; break;
		}
	}

	public static boolean getLeft() { 
		return keyStats[LEFT];
	}
	public static boolean getRight() { 
		return keyStats[RIGHT];
	}
	public static boolean getFire() { 
		return keyStats[FIRE];
	}
	public static boolean getP1Start() { 
		return keyStats[P1_START];
	}
	public static boolean getQuit() { 
		return keyStats[QUIT];
	}
	
}
