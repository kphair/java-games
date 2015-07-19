package nu.xdi.games.spaceinvaders;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 * Window class to create the game window and attach the event handlers
 * 
 * @author Kevin Phair
 * @date 15 Jul 2015
 */
public class Window extends JComponent implements ActionListener {

	// Explicitly define a serialisation ID instead of overriding
	// the warning.
	private static final long serialVersionUID = 1L;
	private String title;
	private static int width, height;
	private JFrame window;
	private static BufferedImage playArea;
	
	public Window (String title, int width, int height) {
		this.title = title;
		this.width = width;
		this.height = height;

		window = new JFrame(title);
		window.add(this);				// Add this JComponent to the JFrame, "window"
		window.setSize(new Dimension (width,height));
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setVisible(true);	

		/*
		 *  Create a framebuffer for use by the game instead of having to redraw the whole
		 *  window each time
		 */
		
		width = window.getContentPane().getWidth();
		height = window.getContentPane().getHeight();
		playArea = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics g = playArea.getGraphics(); 
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		
		/* Attach the keyboard event handlers that are in Game.java
		 */
		window.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				Controls.keyPressed(e);
			}
			public void keyReleased(KeyEvent e) {
				Controls.keyReleased(e);
			}
		} );
	
		/* Attach a 60Hz timer to the window and start it
		 * This will run the game update code
		 */
		Timer t = new Timer(1000 / 60, this);
		t.start();
		
	}

	/**
	 * Timer event handler
	 * Updates object movement and repaints the game area
	 * 
	 * @param ActionEvent which called this handler
	 */
	public void actionPerformed(ActionEvent ae) {
		Game.movement(window.getContentPane());
		repaint();
	}

	/**
	 * Draw the component's graphics
	 * Generally this is the first step in updating the window and
	 * means redrawing the background and any major layout and graphical elements
	 * 
	 * @param graphics context for the JComponent to draw into
	 */
	protected void paintComponent(Graphics g) {
		
		// Update the dimensions of our graphics area in case it was resized 
		width = window.getContentPane().getWidth();
		height = window.getContentPane().getHeight();
		Game.update(playArea.getGraphics());
	}

	
	
	/**
	 * Draw the components children
	 * In this, the object under user control
	 * 
	 * @param graphics context for the JComponent that needs children repainted
	 */
	protected void paintChildren(Graphics g) {
		g.drawImage(playArea, 0, 0, width, height, null);
		
		// Game.update(g);
	}

	/**
	 * Draw the play area for a new game
	 * Print up the score, high score,
	 *  
	 * SCORE<1> HI-SCORE SCORE<2>
	 *   0000    0000      0000
	 */
	public static void drawPlayArea() {
		Graphics g = playArea.getGraphics();
		
		// Clear the framebuffer to black
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		
		// Draw the shields
		g.drawImage(Game.shield, 64, 384, 48, 32, null);
		g.drawImage(Game.shield, 154, 384, 48, 32, null);
		g.drawImage(Game.shield, 244, 384, 48, 32, null);
		g.drawImage(Game.shield, 334, 384, 48, 32, null);
		
		// Draw the line at the bottom
		g.setColor(Color.GREEN);
		g.fillRect(0, 478, 446, 2);

		Text.print(g, 16, 16, "SCORE<1> HI-SCORE SCORE<2>");
		Text.print(g, 280, 480, "CREDIT");

		
		
	}

	
	public static void attractModeScreen1() {
		
	}
	
}
