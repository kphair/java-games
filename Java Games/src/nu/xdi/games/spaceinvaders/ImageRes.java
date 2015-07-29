/**
 * Images
 * @author Kevin Phair
 * @date 29 Jul 2015
 * @version 1.0.0
 */
package nu.xdi.games.spaceinvaders;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

/**
 * @author Kevin Phair
 * @date 29 Jul 2015
 */
public class ImageRes {

	/**
	 * Load the sprite sheet and extract the sprites into their separate BuffereImage objects
	 */
	public static void loadSprites() {

		Game.spriteSheet = new ImageRes().loadImage("resources/SpaceInvadersSpriteSheet.png");
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
		Game.saucerSprite = Game.spriteSheet.getSubimage(56, 24, 16, 8);
		Game.missile = new Missile(Game.spriteSheet.getSubimage(72, 0, 9, 32));
		
	}
	
	/**
	 * Loads an image and returns that image
	 * @param path to image
	 * @return reference to image object in memory
	 */
	public BufferedImage loadImage(String path) {
		BufferedImage b = null;
		try {
			b = ImageIO.read(getClass().getResourceAsStream(path));
		} catch (Exception e) {
			System.out.println("Error loading graphics from: " + (path));
			System.out.println(e.getMessage() + ", " + e.getCause());
		}
		return b;
	}



}
