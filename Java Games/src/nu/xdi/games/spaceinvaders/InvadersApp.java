package nu.xdi.games.spaceinvaders;

import nu.xdi.graphics.util.Images;

/**
 * Space Invaders game in Java
 * 
 * @author Kevin Phair
 * @date 14 Jul 2015
 * 
 * The original game specs are:
 * 2MHz 8080 CPU
 * SN76477 sound processor
 * 260 x 224 (rotated 90 degrees left) @ 60Hz
 * 
 * Start with three lives. Bonus life every 1500 points
 * 
 * Scoring:
 * Row 1 (top),  30 points
 * Rows 2 and 3, 20 points
 * Rows 4 and 5, 10 points
 * Saucer (appears every 25 seconds). Score is 50, 100, 150 or 300 points.
 * Worth 300 points on the 23rd and every 15th shot thereafter
 * (including the shot that hits the saucer)
 * 
 */
public class InvadersApp {

	public static void main(String[] args ) {
		
		int i = 0;
		
		new Window("Space Invaders", 452,540);

		Text.loadFont();
		loadSprites();

		Window.attractModeScreen1();

		while (true) {
			while (true) {
				pause(1);
				if (Controls.getCoin()) {
					while (Controls.getCoin()) {
						pause(1);
					}
					System.out.println("Credit added");
					Game.addCredit();
					Game.showCredits();
				} else if (Controls.getP1Start()) {
					break;
				}
			}
			System.out.println("Attract mode ended");
		
			Game.StartGame();

			while (true) {
				pause(20);		// wait 1/3 seconds
				if (Game.getLives1() == 0) break;
			
			}
		}
		
		//System.exit(0);
	}
	
	/**
	 * Wait for a specified number of frames (specifically, timer events)
	 * @param frames
	 */
	public static void pause(int frames) {
		try {
			Thread.sleep(frames * 1000 / 60);
		} catch (Exception e) {
			
		}
	}

	/**
	 * Load the sprite sheet and extract the sprites into their separate BuffereImage objects
	 */
	private static void loadSprites() {

		Game.spriteSheet = new Images().loadImage("/nu/xdi/games/spaceinvaders/SpaceInvadersSpriteSheet.png");
		if (Game.spriteSheet == null) {
			System.out.println("Sprite Sheet not available!");
			return;
		}
		Game.base = Game.spriteSheet.getSubimage(0, 24, 48, 8);
		Game.shotExplosion = Game.spriteSheet.getSubimage(32, 16, 8, 8);
		Game.shield = Game.spriteSheet.getSubimage(48, 0, 24, 16);
		Game.cheekySprite = Game.spriteSheet.getSubimage(16, 16, 32, 8);
		Invader.sprites = Game.spriteSheet.getSubimage(0, 0, 48, 24);
		Invader.explosion = Game.spriteSheet.getSubimage(0, 16, 16, 8);
		Game.missileExplosion = Game.spriteSheet.getSubimage(48, 24, 8, 8);
		Game.saucerExplosion = Game.spriteSheet.getSubimage(48, 16, 24, 8);
		Game.saucer = Game.spriteSheet.getSubimage(56, 24, 16, 8);
		Game.missile = new Missile(Game.spriteSheet.getSubimage(72, 0, 9, 32));
		new Invader();
		
	}
}
