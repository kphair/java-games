package nu.xdi.games.spaceinvaders;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

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
	private static int playerShotCount = 0;
	private static int baseExplode = 0;
	private static int baseExplodeTimer = 0;
	
	static boolean gameRunning = false;

	static BufferedImage spriteSheet;
	
	static BufferedImage cheekySprite;
	static BufferedImage shotExplosion;
	static BufferedImage base;
	static BufferedImage missileExplosion;
	static BufferedImage shield;
	
	static BufferedImage saucerSprite;
	static BufferedImage saucerExplosion;
	private static int saucerTimer;
	private static int saucerScore = 100;			// Start at 7 so that #23 gets 300 for saucer
	

	static Missile missile;
	private static int credits = 0;
	private static int score1 = 0;
	private static int score2 = 0;
	private static int hiScore = 0;
	private static int lives1 = 3;
	private static int lives2 = 3;
	private static int baseInactive = 0;
	private static int waveEndTimer = 0;
	static int currentWave = 0;
	static int lastLifeAt = 0;
	private static Saucer saucer;
	private static boolean invadersWin = false;
	
	/**
	 * Start the game by resetting the score and initialising a new array of invaders
	 */
	public static void StartGame() {

		shotX = -1;
		baseX = 36;
		shotExplode = 0;
		playerShotCount = 8;
		saucerScore = 150;
		saucerTimer = 0;
		
		lives1 = 3;
		lives2 = 3;
		score1 = 0;
		score2 = 0;

		Window.drawPlayArea();
		new Invader();
		showScores();
		showCredits();
		showLives(lives1);
		baseInactive = 120;
		waveEndTimer = 0;
		currentWave = 0;
		invadersWin = false;
		gameRunning = true;
		lastLifeAt = score1;
		
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
			if (baseExplode == 0 && baseExplodeTimer == 0) {
				if (baseInactive == 0) {
					g.drawImage(base.getSubimage(0, 0, 16, 8), baseX, 432, 32, 16, null);
				} else {
					baseInactive--;
				}
			} else {
				if (baseExplode > 0) {
					baseExplode = 0;
					baseExplodeTimer = 1;
					Sound.baseExplosion.play();
				} else {
					if (baseExplodeTimer % 10 == 1) {
						g.drawImage(base.getSubimage(16, 0, 16, 8), baseX, 432, 32, 16, null);
					} else if (baseExplodeTimer % 10 == 6) {
						g.drawImage(base.getSubimage(32, 0, 16, 8), baseX, 432, 32, 16, null);
					} 
					if (baseExplodeTimer++ == 90) {
						g.drawImage(base.getSubimage(32, 0, 16, 8), baseX, 432, 32, 16, null);
						g.setXORMode(Color.BLACK);
						g.drawImage(base.getSubimage(32, 0, 16, 8), baseX, 432, 32, 16, null);
						g.setPaintMode();
						baseExplodeTimer = 0;
						baseInactive = 120;
						baseX = 36;
						lives1--;
						showLives(lives1);
						if (lives1 == 0 || invadersWin) {
							if (score1 > hiScore) hiScore = score1;
							showScores();
							Sound.saucer.stop();
							if (invadersWin) lives1 = 0;
							gameRunning = false;
						}
					}
				}
			}

			// TASK: Extract this block out into its own class
			
			// If player's shot is active (x is positive) then redraw it
			if (shotX >= 0) {
				if (shotExplode > 0) {
					if (shotExplode == 8) {
						g.drawImage(shotExplosion, shotX - 6, shotY + 4, 16, 16, null);
						Sound.baseFire.stop();
					}
					if (--shotExplode == 0) {
						g.drawImage(shotExplosion, shotX - 6, shotY + 4, 16, 16, null);
						g.setXORMode(Color.BLACK);
						g.drawImage(shotExplosion, shotX - 6, shotY + 4, 16, 16, null);
						g.setPaintMode();
						shotX = -2;					// Set to -2. See movement() further down
					}
				} else {
					// Erase old shot
					g.setColor(Color.BLACK);
					g.clearRect(shotX, shotY, 2, 8);
					shotY -= 8;
					
					// Test if it's reached the top of the screen or can be redrawn in new position
					if (shotY <= 64) {
						shotY = 64;
						shotExplode = 8;
					} else if (checkCollision(shotX, shotY)) {
						// Shot will collide with something
						shotY -= 8;
						shotExplode = 8;
						// Is it an invader?
						// Iterate through the invader array to see which one it is
						for (Invader inv : Invader.invaders ) {
							if (inv.getType() > 0) {
								// is it within the hitbox?
								if (shotX >= inv.getX() && shotX <= inv.getX() + 32) {
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

										if (score1 - lastLifeAt >= 1500) {
											lives1++;
											showLives(lives1);
											lastLifeAt = score1;
										}
										shotExplode = 0;
										Sound.baseFire.stop();
										break;
									}
								}
							}
						}
						// No hit on invader, check if it hit the saucer
						if (shotX >= 0 && saucer != null && saucer.isAlive()) {
							if (shotY <= saucer.getY() + 16) {
								if (shotX - 6 >= saucer.getY()) {
									saucer.explode();
									shotExplode = 0;
									shotX = -2;
									score1 += saucer.getScore();
								}
							}
						}
					} else {
						// otherwise just draw it in new position
						g.setColor(Color.WHITE);
						g.fillRect(shotX, shotY, 2, 8);
					}
				}
			} // End of shot handler
			
			/*
			 * Saucer handler
			 */
			if (saucer != null && saucer.isActive()) {
				saucer.update();
			}
			
			
			/*
			 * Invader missile handler
			 */
			if (missile.isActive()) {
				missile.update();
			}
			// Move to the next missile slot
			missile.tick();
		
			/*
			 * Check for end of current wave
			 */
			if (baseExplodeTimer == 0) {
				if (Invader.marchInvaders(g) == false && waveEndTimer == 0) {
					waveEndTimer = 60;
				} else if (waveEndTimer > 0) {
					if (--waveEndTimer == 0) {
						missile = new Missile(spriteSheet.getSubimage(72, 0, 9, 32));
						new Invader();
						Invader.setDirection(1);
						Window.drawPlayArea();
						showScores();
						showCredits();
						showLives(lives1);
						saucerScore = 100;
						playerShotCount = 7;
						currentWave++;
						if (currentWave == 14) currentWave = 11;		// Wave 6 is as low as the invaders can be
						System.out.println("New wave " + currentWave);
					}
				}
			}
			
			/*
			 * See if the saucer needs to be launched
			 */
			if (saucerTimer++ == 60 * 25) {
				saucerTimer = 0;
				if (saucer == null || !saucer.isActive()) {
					saucer = new Saucer(saucerScore);
				}
			}
		}
	}




	/**
	 * Update movement of the player and their projectile (if active)
	 * @param window that contains the game
	 */
	public static void movement(Container window) {

		if (baseInactive == 0 && baseExplodeTimer == 0) {
			if (Controls.getLeft() && baseX > 36) baseX -= 2;								// LEFT
			if (Controls.getRight() && baseX < 372) baseX += 2;		// RIGHT

			// Fire key has to be released before another shot can be fired.
			if (!Controls.getFire() && shotX == -2) {
				shotX = -1;
			}
			if (Controls.getFire() && shotX == -1 && Invader.getFreeze() == 0) {
				shotX = baseX + 16	;
				shotY = 432;
				Sound.baseFire.play();
				playerShotCount++;
				switch (playerShotCount % 15) {
					case 0: saucerScore = 50; break;
					case 6: saucerScore = 100; break;
					case 12: saucerScore = 150; break;
					case 14: saucerScore = 300; break;
				}
			}
		}
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
	 * Use up a number of credits
	 */
	public static void useCredit(int num) {
		if (num >= getCredits()) {
			credits = 0;
		} else {
			credits--;
		}
	}
	/**
	 * Get the number of lives left for player 1
	 * @return player 1 lives left
	 */
	public static int getLives1 () {
		return lives1;
	}
	/**
	 * Get the number of lives left for player 2
	 * @return player 2 lives left
	 */
	public static int getLives2 () {
		return lives2;
	}
	
	/**
	 * Update the score display
	 */
	public static void showScores () {
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
	public static void showCredits () {
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
	public static void showLives (int lives) {
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

	public static int getBaseX () {
		return baseX;
	}
	
	public static void setBaseExplode () {
		baseExplode = 1;
	}
	
	public static boolean isBaseInactive () {
		if (baseInactive > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void invadersWin() {
		invadersWin = true;
		setBaseExplode();
	}
	
}
