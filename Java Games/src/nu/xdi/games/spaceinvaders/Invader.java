package nu.xdi.games.spaceinvaders;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Invader class to handle the properties of each invader on the screen
 * 
 * @author Kevin Phair
 * @date 15 Jul 2015
 */
public class Invader {
	private int x;
	private int y;
	private int type;		// 0:inactive, 1-5:rows 1-5, 6:saucer
	private BufferedImage image;
	private int animFrame = 0;
	private int explode = 0;

	/**
	 * Constructor for a new Invader object with position and type
	 * 
	 * @param x
	 * @param y
	 * @param type
	 */
	public Invader(int x, int y, int type, BufferedImage image) {
		this.x = x;
		this.y = y;
		this.type = type;
		this.image = image;
		this.animFrame = 0;
	}
	
	/**
	 * Redraw the invader on the screen with the current position and type
	 * 
	 * @param Graphics context for play area
	 */
	public void redraw(Graphics g) {
		BufferedImage i;

		if (explode == 0) {
			i = image.getSubimage(0, animFrame * 8, 16, 8);
		} else {
			i = Game.invExplosion;
			explode--;
		}
		g.drawImage(i, x, y, 32, 16, null);
	}
	
	/**
	 * Clear the position where the invader is before it moves down one line
	 * 
	 * @param Graphics context for play area
	 */
	public void undraw(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(x, y, 32, 16);
	}
	
	/**
	 * Step an invader across the screen
	 * 
	 * The invaders are redrawn from the bottom row up.
	 * When they are marching right, each row is updated starting from the right.
	 * When they are marching left, each row is updated starting from the left.
	 * 
	 * When any invader hits the edge of the screen, all of them move down 16 pixels
	 * and the direction changes
	 * 
	 * The array starts from the top left invader and goes left to right then down
	 */
	public void moveAcross() {
	
		if (type > 0 && ((x < 22 && Game.getDirection() < 0) || (x > 392 && Game.getDirection() > 0))) {
			Game.changeDirection();
		}
		x += Math.signum(Game.getDirection()) * 4;
		animFrame = (++animFrame % 2);
	}
	
	public void moveDown() {
		y += 16;
	}
	
	/**
	 * Get the type of the current invader
	 * 
	 * @return invader type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Get the horizontal position of invader
	 * 
	 * @return current X position 
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get the vertical position of invader
	 * 
	 * @return current Y position 
	 */
	public int getY() {
		return y;
	}
	
	
	/**
	 * Get the current explode status for this invader
	 * 
	 * When an invader explodes, the explosion stays in its place for 16 frames 
	 * before being blanked. The movement of the rest of the invaders pauses
	 * while the explosion is visible
	 * 
	 * @return explode timer
	 */
	public int getExplode() {
		return explode;
	}

	/**
	 * Check to see if the player's shot has hit this invader
	 * 
	 * @param explode
	 */
	public void testExplode(int shotX, int shotY) {

		int hitX = x;
		int hity = y;
		int hitW = 24;
		int hitH = 16;
		
		switch (type) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
		}
		
		this.explode = explode;
	}

	
}

