package nu.xdi.games.spaceinvaders;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import nu.xdi.graphics.util.*;

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
	
	private static BufferedImage invExplode;
	private static BufferedImage base;
	private static BufferedImage baseShot;
	private static BufferedImage shield;
	private static BufferedImage missileHit;
	
	private static BufferedImage saucer;
	private static BufferedImage saucerExplode;
	private static BufferedImage[] missiles;
	
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
		
		invExplode = spriteSheet.getSubimage(0, 16, 16, 8);
		base = spriteSheet.getSubimage(48, 0, 16, 24);
		baseShot = spriteSheet.getSubimage(104, 0, 8, 8);
		shield = spriteSheet.getSubimage(64, 0, 24, 16);
		missileHit = spriteSheet.getSubimage(64, 16, 8, 8);
		
		saucer = spriteSheet.getSubimage(72, 16, 16, 8);
		saucerExplode = spriteSheet.getSubimage(64, 24, 24, 8);
		missiles = new BufferedImage[] { spriteSheet.getSubimage(88, 0, 8, 32),
										spriteSheet.getSubimage(96, 0, 8, 32),
										spriteSheet.getSubimage(104, 0, 8, 32)
		};
		
		
		for (int i = 0; i < 5; ++i) {
			for (int j = 0; j < 11; ++j) {
				invaders[i * 11 + j] = new Invader(40 + j * 32, 128 + i * 32, i + 1, image[i]);
			}
		}
		Game.setDirection(1);
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

			// Draw the shields
			g.drawImage(shield, 64, 384, 48, 32, null);
			g.drawImage(shield, 154, 384, 48, 32, null);
			g.drawImage(shield, 244, 384, 48, 32, null);
			g.drawImage(shield, 334, 384, 48, 32, null);
			
			// Draw the line at the bottom
			g.setColor(Color.GREEN);
			g.fillRect(0, 478, 446, 2);
			
			Text.print(g, 16, 16, "SCORE<1> HI-SCORE SCORE<2>");
			Text.print(g, 48, 48, "0000    0000");
			Text.print(g, 16, 480, "3");
			Text.print(g, 280, 480, "CREDIT 00");

			for (Invader inv : invaders) {
				inv.redraw(g);
			}
			
			/*
			 * Step from left to right across the current row of invaders
			 * moving up one row if the end is reached
			 */
			while (true) {
				Invader currentInv = invaders[currentRow * 11 + currentCol]; 
				currentInv.moveAcross();
				if (moveDown) currentInv.moveDown();
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
				/*
				 * If the invader just processed was active, let the game run for another cycle
				 * This way, only one invader gets updated per cycle, but inactive slots are passed
				 * over. This emulates the behaviour of the game speeding up as invaders get
				 * destroyed.
				 */
				if (currentInv.getType() > 0) {
					break;
				}
			}
		}
	}

	/**
	 * Update movement of the player and invaders
	 * 
	 * @param window that contains the game
	 */
	public static void movement(Container window) {

		if (Controls.getLeft() && baseX > 0) baseX-= 2;								// LEFT
		if (Controls.getRight() && baseX < window.getWidth() - 32) baseX += 2;		// RIGHT
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

	public static void changeDirection() {
		changeDirection = true;
	}

	public static boolean getChangeDirection() {
		return changeDirection;
	}
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
