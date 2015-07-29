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
	private static int invaderStep = 53;
	private static int stepSound = 1;					// loops around from 0-3
	
	
	static BufferedImage sprites = null;
	static BufferedImage explosion = null;
	static BufferedImage highInv = null;
	static BufferedImage medInv = null;
	static BufferedImage lowInv = null;
	private static int freeze;
	private static boolean invadersWin = false;

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

		setDirection (1);
		changeDirection = false;
		moveDown = false;
		currentRow = 4;
		currentCol = 0;
		invaderStep = 53;
		stepSound = 1;
		freeze = 0;
		invadersWin = false;
		
		if (highInv == null) {
			highInv = sprites.getSubimage(32, 0, 16, 16);
			medInv = sprites.getSubimage(16, 0, 16, 16); 
			lowInv = sprites.getSubimage(0, 0, 16, 16);
		}

		Invader[] a = invaders;
		for (int j = 0; j < 11; ++j) {
			a[j] = new Invader(40 + j * 32, Game.currentWave * 16 + 144, 1, highInv);
			a[j + 11] = new Invader(40 + j * 32, Game.currentWave * 16 + 144 + 32, 2, medInv);
			a[j + 22] = new Invader(40 + j * 32, Game.currentWave * 16 + 144 + 64, 3, medInv);
			a[j + 33] = new Invader(40 + j * 32, Game.currentWave * 16 + 144 + 96, 4, lowInv);
			a[j + 44] = new Invader(40 + j * 32, Game.currentWave * 16 + 144 + 128, 5, lowInv);
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
		int invaderCount = invadersLeft(invaders);

		if (invaderStep-- <= 0) {
			if (invaderCount > 49) invaderStep = 53;
			else if (invaderCount > 42) invaderStep = 46;
			else if (invaderCount > 35) invaderStep = 39;
			else if (invaderCount > 28) invaderStep = 34;
			else if (invaderCount > 22) invaderStep = 28;
			else if (invaderCount > 17) invaderStep = 24;
			else if (invaderCount > 14) invaderStep = 21;
			else if (invaderCount > 11) invaderStep = 18;
			else if (invaderCount > 9) invaderStep = 16;
			else if (invaderCount > 7) invaderStep = 13;
			else if (invaderCount > 6) invaderStep = 11;
			else if (invaderCount > 5) invaderStep = 9;
			else if (invaderCount > 4) invaderStep = 8;
			else if (invaderCount > 3) invaderStep = 7;
			else if (invaderCount > 2) invaderStep = 6;
			else if (invaderCount > 1) invaderStep = 5;
			else if (invaderCount > 0) invaderStep = 4;
			else if (invaderCount == 0) invaderStep = 53;
			
			Sound.steps[stepSound].stop();
			if (++stepSound == 4) stepSound = 0;
			Sound.steps[stepSound].play();
		}
		
		Invader currentInv;
		while (true) {
			
			if (Invader.getFreeze() > 0) {
				for (Invader inv : invaders) {
					if (inv.getExplode() > 0) {
						inv.redraw(g);
					}
				}
				break;
			}

			if (invaderCount == 0) return false;

			currentInv = invaders[currentRow * 11 + currentCol];

			// If the current invader is active, move it and give it chance to shoot (especially if directly overhead)
			if (currentInv.getType() > 0) {
				currentInv.moveAcross(invaderCount);
				if (moveDown) {
					currentInv.undraw(g);
					currentInv.moveDown();
					if (currentInv.getY() >= 432) {
						currentInv.setType(1);
						invadersWin = true;
					}
				}
				currentInv.redraw(g);

				// Fire?
				
				if (!Game.isBaseInactive() && new Random().nextInt(100) > (Math.abs(currentInv.getX() - Game.getBaseX()) > 20 ? 90 : 50)) {
					// Scan down to make sure it isn't above another invader
					for (i = currentRow; i <= 4; ++i)  {
						if (i == 4) break;
						if (invaders[(i + 1) * 11 + currentCol].getType() > 0) {
							break;
						}	
					}
					// if i == 4 then all clear. Use the next free missile slot if one available
					if (i == 4) {
						Game.missile.dropMissile(currentInv.getX() + 14, currentInv.getY() + 28);
					}
				}
			}
			// Move to the next invader in the array
			currentCol++;
			if (currentCol > 10) {
				currentCol = 0;
				currentRow--;
				if (invadersWin) Game.invadersWin();
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
	 * Get the direction of the invaders
	 * @return -2 = moving down and left, -1 = left, 1 = right, 2 = moving down and right
	 */
	public static int getDirection() {
		return direction;
	}

	/**
	 * This returns the status of the moveDown flag. This is set at the end of a full movement update
	 * if the changeDirection flag is set.
	 * @return
	 */
	public static boolean getMoveDown() {
		return moveDown;
	}
	public void moveDown() {
		y += 16;
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
	public void moveAcross(int count) {
	
		int dir = getDirection();
		
		if (type > 0 && ((x <= 12 && dir < 0) || (x >= 398 && dir > 0))) {
			changeDirection();
		}
		x += Math.signum(dir) * 4;
		// The last invader always moves faster when going left
		if (dir < 0 && count == 1) {
			x -= 2;
		}
		animFrame = (++animFrame % 2);
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
	
	public void setType(int type) {
		this.type = type;
		if (type == 1) this.image = highInv;
	}

}

