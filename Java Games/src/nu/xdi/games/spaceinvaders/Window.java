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
		Graphics g = getPlayAreaGraphics(); 
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
	 * Get the graphics context for drawing into the play area
	 * 
	 * @return a Graphics reference to the play area
	 */
	public static Graphics getPlayAreaGraphics() {
		return playArea.getGraphics();
	}
	
	/**
	 * Get the image for the play area
	 * 
	 * @return a BuffereImage reference to the play area
	 */
	public static BufferedImage getPlayArea() {
		return playArea;
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
		Game.update(getPlayAreaGraphics());
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
		
		// Only draw the shields if the invaders are in wave 0 to 4
		if (Game.currentWave < 5) {
			g.drawImage(Game.shield, 64, 382, 48, 32, null);
			g.drawImage(Game.shield, 154, 382, 48, 32, null);
			g.drawImage(Game.shield, 244, 382, 48, 32, null);
			g.drawImage(Game.shield, 334, 382, 48, 32, null);
		}
		
		// Draw the line at the bottom
		g.setColor(Color.GREEN);
		g.fillRect(0, 478, 446, 2);

		g.setColor(Color.GRAY);
		Text.print(g, 16, 16, "SCORE<1> HI-SCORE SCORE<2>");
		Text.print(g, 280, 480, "CREDIT");
		
	}

	
	public static void attractModeScreen1() {
		Graphics g = playArea.getGraphics();
		
		// Clear the framebuffer to black
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);

		g.setColor(Color.GRAY);
		Text.print(g, 16, 16, "SCORE<1> HI-SCORE SCORE<2>");
		Game.showScores();
		
		g.setColor(Color.WHITE);
		Text.print(g, 192, 96, "PLAY");
		Text.print(g, 112, 144, "SPACE  INVADERS");
		Text.print(g, 64, 208, "*SCORE ADVANCE TABLE*");
		g.drawImage(Game.saucer, 126, 240, 32, 16, null);
		Text.print(g, 158, 248, "=? MYSTERY");

		g.drawImage(Game.spriteSheet.getSubimage(32, 0, 16, 8), 126, 272, 32, 16, null);
		Text.print(g, 158, 272, "=30 POINTS");
		g.drawImage(Game.spriteSheet.getSubimage(16, 0, 16, 8), 126, 304, 32, 16, null);
		Text.print(g, 158, 304, "=20 POINTS");
		g.drawImage(Game.spriteSheet.getSubimage(0, 0, 16, 8), 126, 336, 32, 16, null);
		Text.print(g, 158, 336, "=10 POINTS");

		Text.print(g, 0, 368, "EXTRA LIFE EVERY 1500 POINTS");
		
		Text.print(g, 32, 416, "PRESS  5 TO INSERT COIN");
		Text.print(g, 144, 432, "1 TO PLAY");
		Text.print(g, 144, 448, "ESC TO QUIT");
		
		Text.print(g, 280, 480, "CREDIT", Color.GRAY);
		Game.showCredits();
		
	}
	
	public static void gameOverScreen () {
		
		Graphics g = playArea.getGraphics();

		g.setColor(Color.RED);
		InvadersApp.pause(30);
		Text.print(g, 144, 128, "G");
		InvadersApp.pause(30);
		Text.print(g, 160, 128, "A");
		InvadersApp.pause(30);
		Text.print(g, 176, 128, "M");
		InvadersApp.pause(30);
		Text.print(g, 192, 128, "E");
		InvadersApp.pause(30);
		
		Text.print(g, 224, 128, "O");
		InvadersApp.pause(30);
		Text.print(g, 240, 128, "V");
		InvadersApp.pause(30);
		Text.print(g, 256, 128, "E");
		InvadersApp.pause(30);
		Text.print(g, 272, 128, "R");

	}
	
}
