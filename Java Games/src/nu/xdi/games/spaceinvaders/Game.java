package nu.xdi.games.spaceinvaders;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import org.omg.CORBA.FREE_MEM;

import nu.xdi.graphics.util.Images;

/**
 * Game function code
 * This class manages the tracking and updating of all game objects

 * @author Kevin Phair
 * @date 15 Jul 2015
 */
public class Game {
	private static int baseX = 240;
	private static int shotX, shotY;
	private static int shotExplode = 0;
	private static int playerShotCount;
	
	private static int direction = 0;
	private static boolean changeDirection = false;
	private static boolean moveDown = false;
	private static int currentRow = 4;
	private static int currentCol = 0;
	static Invader[] invaders = new Invader[55];
	static boolean gameRunning = false;

	static BufferedImage spriteSheet;
	
	static BufferedImage cheekySprite;
	static BufferedImage shotExplosion;
	static BufferedImage base;
	static BufferedImage missileExplosion;
	static BufferedImage shield;
	
	static BufferedImage saucer;
	static BufferedImage saucerExplosion;
	private static int saucerX;
	private static int saucerTimer;
	private static int saucerScore = 50;
	

	static Missile missile;
	private static int credits = 0;
	private static int score1 = 0;
	private static int score2 = 0;
	private static int hiScore = 0;
	private static int lives1 = 3;
	private static int lives2 = 3;
	private static int baseInactive = 0;
	private static int waveEndTimer = 0;
	
	/**
	 * Start the game by resetting the score and initialising a new array of invaders
	 */
	public static void StartGame() {

		shotX = -1;
		baseX = 36;
		shotExplode = 0;
		playerShotCount = 0;
		saucerScore = 150;
		saucerX = -1;
		
		lives1 = 3;
		lives2 = 3;
		score1 = 0;
		score2 = 0;

		direction = 0;
		changeDirection = false;
		moveDown = false;
		currentRow = 4;
		currentCol = 0;
		
		Game.setDirection(1);
		Window.drawPlayArea();
		showScores();
		showCredits();
		showLives(lives1);
		baseInactive = 120;
		waveEndTimer = 0;
		gameRunning = true;
		
	}
	
	private static boolean checkCollision (int xPos, int yPos) {
		
		// The destination area to be written into for comparison
		BufferedImage destImg = Window.getPlayArea().getSubimage(xPos, yPos, 1, 8);

		// Scan the destination and flag a collision if a pixel is lit
		for (int y = 0; y < 4; ++y) {
			if (((destImg.getRGB(0, y * 2) & 0xffffff) > 0)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Redraw the enemies, player, barriers, bullets and score.
	 * @param graphics context of drawable area
	 */
	public static void update(Graphics g) {
		
		if (gameRunning) {

			
			// Draw player base
			if (baseInactive == 0) {
				g.drawImage(base.getSubimage(0, 0, 16, 8), baseX, 432, 32, 16, null);
			} else {
				baseInactive--;
			}

			// TASK: Extract this block out into its own class
			
			// If player's shot is active (x is positive) then redraw it
			if (shotX >= 0) {
				if (shotExplode > 0) {
					g.drawImage(shotExplosion, shotX, shotY + 4, 16, 16, null);
					if (--shotExplode == 0) {
						g.setXORMode(Color.BLACK);
						g.drawImage(shotExplosion, shotX, shotY + 4, 16, 16, null);
						g.setPaintMode();
						shotX = -2;					// Set to -2. See movement() further down
					}
				} else {
					// Erase old shot
					g.setColor(Color.BLACK);
					g.clearRect(shotX + 6, shotY, 2, 8);
					shotY -= 8;
					
					// Test if it's reached the top of the screen or can be redrawn in new position
					if (shotY <= 64) {
						shotExplode = 8;
					} else if (checkCollision(shotX + 6, shotY)) {
						// Shot will collide with something
						shotY -= 8;
						shotExplode = 8;
						// Is it an invader?
						// Iterate through the invader array to see which one it is
						for (Invader inv : invaders ) {
							if (inv.getType() > 0) {
								// is it within the hitbox?
								if (shotX >= inv.getX() - 2 && shotX <= inv.getX() + 24) {
									if (shotY >= inv.getY() && shotY <= inv.getY() + 14) {
										inv.setExplode();
										shotX = -2;
										switch (inv.getType()) {
											case 1: score1 += 10;		// Top score
											case 2: 
											case 3: score1 += 10;		// Mid score
											case 4: 
											case 5: score1 += 10;		// Low score
										}
										showScores();
										shotExplode = 0;
										break;
									}
								}
							}
						}
					} else {
						// otherwise just draw it in new position
						g.setColor(Color.WHITE);
						g.fillRect(shotX + 6, shotY, 2, 8);
					}
				}
			}
			
			/*
			 * Invader missile handler
			 */
			if (missile.isActive()) {
				missile.update();
			}
			// Move to the next missile slot
			missile.tick();
		
			if (marchInvaders(g) == false && waveEndTimer == 0) {
				waveEndTimer = 60;
			} else if (waveEndTimer > 0) {
				System.out.println(waveEndTimer);
				if (--waveEndTimer == 0) {
					System.out.println("New wave");
					
					missile = new Missile(spriteSheet.getSubimage(72, 0, 9, 32));
					new Invader();
					Game.setDirection(1);
					Window.drawPlayArea();
					showScores();
					showCredits();
					showLives(lives1);
					saucerScore = 50;
					playerShotCount = 0;
					changeDirection = false;
					moveDown = false;
					currentRow = 4;
					currentCol = 0;
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
				if (new Random().nextInt(100) > 90) {
					// Scan down to make sure it isn't above another invader
					for (i = currentRow; i <= 4; ++i)  {
						if (i == 4) break;
						if (invaders[(i + 1) * 11 + currentCol].getType() > 0) {
							break;
						}	
					}
					// if i == 4 then all clear. Use the next free missile slot if one available
					if (i == 4) {
						missile.dropMissile(currentInv.getX() + 14, currentInv.getY() + 32);
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
	 * Update movement of the player and their projectile (if active)
	 * @param window that contains the game
	 */
	public static void movement(Container window) {

		if (baseInactive == 0) {
			if (Controls.getLeft() && baseX > 36) baseX -= 2;								// LEFT
			if (Controls.getRight() && baseX < 372) baseX += 2;		// RIGHT

			// Fire key has to be released before another shot can be fired.
			if (!Controls.getFire() && shotX == -2) {
				shotX = -1;
			}
			if (Controls.getFire() && shotX == -1 && Invader.getFreeze() == 0) {
				shotX = baseX + 10	;
				shotY = 432;
				
				playerShotCount++;
				if (playerShotCount == 14) {
					switch (saucerScore) {
						case 50: saucerScore = 100; break;
						
					}
					
				}
			}
		}
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
	 * Get the value of player 1's score
	 * @return player 1 score
	 */
	public static int getScore1() {
		return score1;
	}
	/**
	 * Get the value of player 2's score
	 * @return player 2 score
	 */
	public static int getScore2() {
		return score2;
	}
	/**
	 * Get the value of the current high score
	 * @return current high score
	 */
	public static int getHiSCore() {
		return hiScore;
	}
	/**
	 * Get number of credits in machine
	 * @return number of credits
	 */
	public static int getCredits() {
		return credits;
	}
	/**
	 * Add a credit (insert coin)
	 */
	public static void addCredit() {
		credits++;
	}
	/**
	 * Get the number of lives left for player 1
	 * @return player 1 lives left
	 */
	public static int getLives1() {
		return lives1;
	}
	/**
	 * Get the number of lives left for player 2
	 * @return player 2 lives left
	 */
	public static int getLives2() {
		return lives2;
	}
	
	/**
	 * Update the score display
	 */
	public static void showScores() {
		Graphics g = Window.getPlayAreaGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(48, 48, 64, 16);
		g.fillRect(176, 48, 64, 16);
		g.fillRect(336, 48, 64, 16);
		g.setColor(Color.RED);
		Text.print(g, 48, 48, String.format("%04d", score1));
		Text.print(g, 176, 48, String.format("%04d", hiScore));
		Text.print(g, 336, 48, String.format("%04d", score2));
	}
	/**
	 * Update the credits display
	 */
	public static void showCredits() {
		Graphics g = Window.getPlayAreaGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(392, 480, 32, 16);
		g.setColor(Color.GREEN);
		Text.print(g, 392, 480, String.format("%02d", getCredits()));
	}
	/**
	 * Update the lives display
	 * @param number of lives to display
	 */
	public static void showLives(int lives) {
		Graphics g = Window.getPlayAreaGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(16, 480, 374, 16);
		g.setColor(Color.GREEN);
		Text.print(g, 16, 480, String.format("%d", lives));
		if (lives > 1) {
			for (int i = 1; i < lives; ++i) {
				g.drawImage(base.getSubimage(0, 0, 16, 8), i * 48, 480, 32, 16, null);
			}
		}
	}
	
}
