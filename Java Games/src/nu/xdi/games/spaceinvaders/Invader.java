package nu.xdi.games.spaceinvaders;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

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

	private static int direction = 1;
	private static boolean changeDirection = false;
	private static boolean moveDown = false;
	private static int currentRow = 4;
	private static int currentCol = 0;
	static Invader[] invaders = new Invader[55];
	private static int invaderStep = 0;					// Resets at 60
	private static int stepSound = 0;					// loops around from 0-3
	
	
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

		setDirection (1);
		changeDirection = false;
		moveDown = false;
		currentRow = 4;
		currentCol = 0;
		invaderStep = 0;
		stepSound = 1;
		freeze = 0;
		
		System.out.println("Initialising new invaders array");
		if (highInv == null) {
			highInv = sprites.getSubimage(32, 0, 16, 16);
			medInv = sprites.getSubimage(16, 0, 16, 16); 
			lowInv = sprites.getSubimage(0, 0, 16, 16);
		}

		Invader[] a = invaders;
		for (int j = 0; j < 11; ++j) {
			a[j] = new Invader(40 + j * 32, Game.currentWave * 32 + 128, 1, highInv);
			a[j + 11] = new Invader(40 + j * 32, Game.currentWave * 32 + 128 + 32, 2, medInv);
			a[j + 22] = new Invader(40 + j * 32, Game.currentWave * 32 + 128 + 64, 3, medInv);
			a[j + 33] = new Invader(40 + j * 32, Game.currentWave * 32 + 128 + 96, 4, lowInv);
			a[j + 44] = new Invader(40 + j * 32, Game.currentWave * 32 + 128 + 128, 5, lowInv);
		}
		
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
	 * Step from left to right across the current row of invaders
	 * moving up one row if the end is reached
	 */
	public static boolean marchInvaders(Graphics g) {
		int i;
		int activeCount = 0;
		
		while (true) {
			if (++invaderStep == 72) {
				invaderStep = 0;
				Sound.steps[stepSound].stop();
				if (++stepSound == 4) stepSound = 0;
				Sound.steps[stepSound].play();
			}
			
			Invader currentInv = invaders[currentRow * 11 + currentCol];
			if (currentInv.getFreeze() > 0) {
				for (Invader inv : invaders) {
					if (inv.getType() > 0 && inv.getExplode() == 0) {
						activeCount++;
					}
					if (inv.getExplode() > 0) {
						inv.redraw(g);
					}
				}
				break;
			}

			if (invadersLeft(invaders) == 0) return false;

			// If the current invader is active, move it and give it chance to shoot
			if (currentInv.getType() > 0) {
				currentInv.moveAcross();
				if (moveDown) {
					currentInv.undraw(g);
					currentInv.moveDown();
				}
				currentInv.redraw(g);

				// Give the current invader a chance to fire
				if (!Game.isBaseInactive() && new Random().nextInt(100) > 90) {
					// Scan down to make sure it isn't above another invader
					for (i = currentRow; i <= 4; ++i)  {
						if (i == 4) break;
						if (invaders[(i + 1) * 11 + currentCol].getType() > 0) {
							break;
						}	
					}
					// if i == 4 then all clear. Use the next free missile slot if one available
					if (i == 4) {
						Game.missile.dropMissile(currentInv.getX() + 14, currentInv.getY() + 32);
					}
				}
			}
			// Move to the next invader in the array
			currentCol++;
			if (currentCol > 10) {
				currentCol = 0;
				currentRow--;
				/*
				 * If all the invaders are done check to see if the changeDirection flag is set
				 */
				if (currentRow < 0) {
					currentRow = 4;
					/*
					 * Check to see if the direction of the invaders needs to be changed
					 * and if they need to be told they can move down 
					 */
					if (changeDirection) {
						direction = -direction;
						currentCol = 0;
						changeDirection = false;
						moveDown = true;
					} else {
						moveDown = false;
					}
				}
			} // end of active invader's activity
			if (currentInv.getType() > 0) break;
		}
		return true;
	}

	/**
	 * Counts up the remaining invaders on the screen
	 * Includes invaders which are in the process of blowing up
	 * @param invader array
	 * @return number of invaders
	 */
	private static int invadersLeft(Invader[] invaders) {
		int activeCount = 0;
		
		for (Invader inv : invaders) {
			if (inv.getType() > 0 && inv.getExplode() == 0) {
				activeCount++;
			}
		}
		return activeCount;
	}

	/**
	 * Set the direction of the Invaders
	 * When the direction is +/-2 (moving down and right/left) it will be changed to +/-1 when
	 * the entire squadron has been redrawn
	 * 
	 * @param newDirection - -2 = moving down and left, -1 = left, 1 = right, 2 = moving down and right
	 */
	public static void setDirection(int newDirection) {
		direction = newDirection;
	}

	/**
	 * Sets the changeDirection flag
	 * This tells the game that before going around do move all the invaders again it should
	 * check to see if the direction is to be changed.
	 */
	public static void changeDirection() {
		changeDirection = true;
	}

	public static boolean getChangeDirection() {
		return changeDirection;
	}
	/**
	 * This returns the status of the moveDown flag. This is set at the end of a full movement update
	 * if the changeDirection flag is set.
	 * @return
	 */
	public static boolean getMoveDown() {
		return moveDown;
	}
	
	/**
	 * Get the direction of the invaders
	 * @return -2 = moving down and left, -1 = left, 1 = right, 2 = moving down and right
	 */
	public static int getDirection() {
		return direction;
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
	
		if (type > 0 && ((x < 22 && getDirection() < 0) || (x > 392 && getDirection() > 0))) {
			changeDirection();
		}
		x += Math.signum(getDirection()) * 4;
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
		Sound.invaderExplosion.play();
	}
	
	public static int getFreeze() {
		return freeze;
	}

}

