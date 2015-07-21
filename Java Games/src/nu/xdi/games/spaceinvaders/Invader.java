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
	private int animFrame = 0;
	private int explode = 0;
	private BufferedImage image;
	static BufferedImage sprites = null;
	static BufferedImage explosion = null;
	static BufferedImage highInv = null;
	static BufferedImage medInv = null;
	static BufferedImage lowInv = null;
	private static int freeze;

	/**
	 * Constructor for a new Invader object with position and type
	 * @param x
	 * @param y
	 * @param type
	 * @param invader sprite image
	 * @param invader exploding image
	 */
	public Invader (int x, int y, int type, BufferedImage image) {
		this.x = x;
		this.y = y;
		this.type = type;
		this.image = image;
		this.animFrame = 0;
		this.explode = 0;
	}
	
	/**
	 * Initialise the invader array and sprites for the different types
	 * @param a
	 * @param sprites
	 * @param explosion
	 */
	public Invader () {

		BufferedImage image = null;
		Invader[] a = Game.invaders;
		
			System.out.println("Initialising new invaders array");
		if (highInv == null) {
			highInv = sprites.getSubimage(32, 0, 16, 16);
			medInv = sprites.getSubimage(16, 0, 16, 16); 
			lowInv = sprites.getSubimage(0, 0, 16, 16);
		}

		for (int j = 0; j < 11; ++j) {
			a[j] = new Invader(40 + j * 32, 128, 1, highInv);
			a[j + 11] = new Invader(40 + j * 32, 128 + 32, 2, medInv);
			a[j + 22] = new Invader(40 + j * 32, 128 + 64, 3, medInv);
			a[j + 33] = new Invader(40 + j * 32, 128 + 96, 4, lowInv);
			a[j + 44] = new Invader(40 + j * 32, 128 + 128, 5, lowInv);
		}
		
		freeze = 0;
	}
	
	/**
	 * Initialise a single invader
	 * @param x
	 * @param y
	 * @param type
	 */
	public void init (int x, int y, int type) {
		this.x = x;
		this.y = y;
		this.type = type;
		this.animFrame = 0;
		this.explode = 0;
		this.freeze = 0;
	}
	
	/**
	 * Redraw the invader on the screen with the current position and type
	 * @param Graphics context for play area
	 */
	public void redraw(Graphics g) {
		BufferedImage i;

		if (explode == 0) {
			i = image.getSubimage(0, animFrame * 8, 16, 8);
			g.drawImage(i, x, y, 32, 16, null);
		} else {
			if (explode == 16) {
				g.drawImage(Invader.explosion, x, y, 32, 16, null);
				explode--;
			} else if (--explode == 0) {
				// Erase the explosion
				g.drawImage(Invader.explosion, x, y, 32, 16, null);
				g.setXORMode(Color.BLACK);
				g.drawImage(Invader.explosion, x, y, 32, 16, null);
				g.setPaintMode();
				if (freeze > 0) freeze--;
				if (freeze == 0) {
					type = 0;
				}
				
			}
		}
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
	
	public void setExplode() {
		explode = 16;
		freeze++;
	}
	
	public static int getFreeze() {
		return freeze;
	}

}

