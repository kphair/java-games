package nu.xdi.games.spaceinvaders;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import nu.xdi.graphics.util.Images;

/**
 * Game function code
 * 
 * This class manages the tracking and updating of all game objects

 * @author Kevin Phair
 * @date 15 Jul 2015
 */
public class Game {
	private static int baseX = 240;
	private static int shotX, shotY;
	private static int shotExplode = 0;
	private static int saucerX;
	private static int playerShotCount;
	
	private static int direction = 0;
	private static boolean changeDirection = false;
	private static boolean moveDown = false;
	private static Invader[] invaders = new Invader[55];
	private static boolean gameRunning = false;
	private static int currentRow = 4;
	private static int currentCol = 0;

	private static BufferedImage spriteSheet;
	
	private static BufferedImage row4Sprite; 
	private static BufferedImage row2Sprite; 
	private static BufferedImage row1Sprite;
	private static BufferedImage[] image;
	
	private static BufferedImage shotExplosion;
	static BufferedImage invExplosion;
	private static BufferedImage cheekySprite;
	private static BufferedImage base;
	private static BufferedImage missileHit;
	public static BufferedImage shield;
	
	private static BufferedImage saucer;
	private static BufferedImage saucerExplosion;

	/* 
	 * Invaders can have up to three missiles in play
	 * One of them gets updated per frame. currentMissile is used to keep
	 * track of which one will be updated in the current frame and it constantly
	 * loops around to 0
	 */
	private static BufferedImage[] missiles;
	private static int currentMissile;
	private static int[] missileX = new int[3];
	private static int[] missileY = new int[3];
	private static int[] missileF = new int[3];
	private static int[] missileExplode = new int[3];
	
	
	/**
	 * Start the game by resetting the score and initialising a new array of invaders
	 * 
	 */
	public static void StartGame() {

		spriteSheet = new Images().loadImage("/nu/xdi/games/spaceinvaders/SpaceInvadersSpriteSheet.png");
		if (spriteSheet == null) {
			System.out.println("Sprite Sheet not available!");
			return;
		}

		row4Sprite = spriteSheet.getSubimage(0, 0, 16, 16); 
		row2Sprite = spriteSheet.getSubimage(16, 0, 16, 16); 
		row1Sprite = spriteSheet.getSubimage(32, 0, 16, 16);
		image = new BufferedImage[] { row1Sprite, row2Sprite, row2Sprite, row4Sprite, row4Sprite };
		
		invExplosion = spriteSheet.getSubimage(0, 16, 16, 8);
		cheekySprite = spriteSheet.getSubimage(16, 16, 32, 8);
		base = spriteSheet.getSubimage(0, 24, 48, 8);
		shield = spriteSheet.getSubimage(48, 0, 24, 16);
		
		shotExplosion = spriteSheet.getSubimage(48, 24, 8, 8);
		saucer = spriteSheet.getSubimage(56, 24, 16, 8);
		saucerExplosion = spriteSheet.getSubimage(48, 16, 24, 8);
		missiles = new BufferedImage[] { spriteSheet.getSubimage(72, 0, 8, 32),
										spriteSheet.getSubimage(80, 0, 8, 32),
										spriteSheet.getSubimage(88, 0, 8, 32)
		};

		baseX = 240;
		shotExplode = 0;
		saucerX = -1;
		playerShotCount = 0;
		
		direction = 0;
		changeDirection = false;
		moveDown = false;
		currentRow = 4;
		currentCol = 0;

		shotX = -1;
		baseX = 36;
		for (int i = 0; i < missileX.length; ++i) {
			missileX[i] = -1;
			missileY[i] = 0;
			missileF[i] = 0;
			missileExplode[i] = 0;
		}
		currentMissile = 0;
		
		for (int i = 0; i < 5; ++i) {
			for (int j = 0; j < 11; ++j) {
				invaders[i * 11 + j] = new Invader(40 + j * 32, 128 + i * 32, i + 1, image[i]);
			}
		}
		Game.setDirection(1);
		
		Window.drawPlayArea();
		
		gameRunning = true;
	}
	
	/**
	 * Redraw the enemies, player, barriers, bullets and score.
	 * 
	 * @param graphics context of drawable area
	 */
	public static void update(Graphics g) {
		
		if (gameRunning) {

			// Draw player base
			g.drawImage(base.getSubimage(0, 0, 16, 8), baseX, 432, 32, 16, null);

			// If player's shot is active (x is positive) then redraw it
			if (shotX >= 0) {
				if (shotExplode > 0) {
					g.drawImage(shotExplosion, shotX, shotY, 16, 16, null);
					if (--shotExplode == 0) {
						g.setXORMode(Color.BLACK);
						g.drawImage(shotExplosion, shotX, shotY, 16, 16, null);
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
					} else {
						g.setColor(Color.WHITE);
						g.fillRect(shotX + 6, shotY, 2, 8);
					}
				}
			}
			
			/*
			 * Invader missile handler
			 */
			if (missileX[currentMissile] > 0) {

				
				// If missile active, perform its actions
				if (missileExplode[currentMissile] <= 0) {
					
					// Remove it from the screen
					if (missileExplode[currentMissile] == 0) {
						g.drawImage(missiles[currentMissile].getSubimage(0, missileF[currentMissile] * 8, 8, 8), missileX[currentMissile], missileY[currentMissile], 16, 16, null);
						g.setXORMode(Color.BLACK);
						g.drawImage(missiles[currentMissile].getSubimage(0, missileF[currentMissile] * 8, 8, 8), missileX[currentMissile], missileY[currentMissile], 16, 16, null);
						g.setPaintMode();
					} else {
						missileExplode[currentMissile] = 0;
					}

					// Advance to the next frame of animation
					if (++missileF[currentMissile] == 4) missileF[currentMissile] = 0;

					// Move missile down and draw it
					missileY[currentMissile] += 8;
					if (missileY[currentMissile] > 457) {
						missileExplode[currentMissile] = 8;
					} else {
						g.drawImage(missiles[currentMissile].getSubimage(0, missileF[currentMissile] * 8, 8, 8), missileX[currentMissile], missileY[currentMissile], 16, 16, null);
					}
				// Make it explode
				} else if (missileExplode[currentMissile] == 8) {
					g.drawImage(shotExplosion, missileX[currentMissile], missileY[currentMissile], 16, 16, null);
					missileExplode[currentMissile]--;
					System.out.println(missileExplode[currentMissile]);
				// Remove from screen and mark it for reuse
				} else if (--missileExplode[currentMissile] == 0) {
					System.out.println(missileExplode[currentMissile]);
					g.drawImage(shotExplosion, missileX[currentMissile], missileY[currentMissile], 16, 16, null);
					g.setXORMode(Color.BLACK);
					g.drawImage(shotExplosion, missileX[currentMissile], missileY[currentMissile], 16, 16, null);
					g.setPaintMode();
					missileX[currentMissile] = -1;
				}
			}
			
			// Move to the next missile slot
			if (++currentMissile == 3) currentMissile = 0;
			
			Text.print(g, 48, 48, "0000");
			Text.print(g, 176, 48, "0000");
			Text.print(g, 336, 48, "0000");
			Text.print(g, 16, 480, "3");
			Text.print(g, 392, 480, "00");

			marchInvaders(g);
		}
	}


	/**
	 * Step from left to right across the current row of invaders
	 * moving up one row if the end is reached
	 */
	public static void marchInvaders(Graphics g) {
		int i;
		
		while (true) {
			Invader currentInv = invaders[currentRow * 11 + currentCol];
			if (currentInv.getType() > 0) {
				currentInv.testExplode(shotX, shotY);
				currentInv.moveAcross();
				if (moveDown) {
					currentInv.undraw(g);
					currentInv.moveDown();
				}
				currentInv.redraw(g);
				if (currentInv.getExplode() > 0) break;

				// Give the current invader a chance to fire
				if (new Random().nextInt(100) > 92) {
					// Scan down to make sure it isn't above another invader
					for (i = currentRow; i <= 4; ++i)  {
						if (i == 4) break;
						if (invaders[(i + 1) * 11 + currentCol].getType() > 0) {
							break;
						}
					}
					// if i == 4 then all clear. Use the next free missile slot if one available
					if (i == 4) {
						for (i = 0; i < 3; ++i) {
							System.out.print(missileX[i] + " ");
							if (missileX[i] < 0) {
								missileX[i] = currentInv.getX() + 15;
								missileY[i] = currentInv.getY() + 8;
								missileExplode[i] = -1;
								System.out.println("Shots fired!");
								break;
							} else {
								System.out.println("No missile slots");
							}
						}
					}
				}

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
				}
				break;
			} // end of active invader's activity
		}
	}


	/**
	 * Update movement of the player and their projectile (if active)
	 * 
	 * @param window that contains the game
	 */
	public static void movement(Container window) {

		if (Controls.getLeft() && baseX > 36) baseX-= 2;								// LEFT
		if (Controls.getRight() && baseX < 372) baseX += 2;		// RIGHT
		// Fire key has to be released before another shot can be fired.
		if (!Controls.getFire() && shotX == -2) {
			shotX = -1;
		}
		if (Controls.getFire() && shotX == -1) {
			shotX = baseX + 10	;
			shotY = 432;
		}
	}

	
	/**
	 * Set the direction of the Invaders
	 * 
	 * When the direction is +/-2 (moving down and right/left) it will be changed to +/-1 when
	 * the entire squadron has been redrawn
	 * 
	 * @param newDirection - -2 = moving down and left, -1 = left, 1 = right, 2 = moving down and right
	 * 
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
	 * 
	 * @return -2 = moving down and left, -1 = left, 1 = right, 2 = moving down and right
	 */
	public static int getDirection() {
		return direction;
	}
}
