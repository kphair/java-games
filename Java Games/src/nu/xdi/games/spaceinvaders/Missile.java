package nu.xdi.games.spaceinvaders;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Provide an object and methods to manage the three invader missiles
 * 
 * @author Kevin Phair
 * @date 19 Jul 2015
 * 
 */
public class Missile {

	// TASK: Refactor so we use Missile[0-2] in Game instead of Missile() using arrays.  
	
	private static int[] x = { -1, -1, -1 };
	private static int[] y = { 0, 0, 0 };
	private static int[] active = { 0, 0, 0 };
	private static int[] explode = { 0, 0, 0 };
//	private static BufferedImage[] bg = { null, null, null };
	private static int num = 0;
	private static int frame = 0;
	
	private BufferedImage spriteSheet;
	
	
	public Missile (BufferedImage image) {
		this.spriteSheet = image;
	}
	
	/**
	 * Check if the missile is active
	 * 
	 * @return true if active, false otherwise
	 */
	public boolean isActive() {
		return (active[num] != 0);
	}
	
	public void setInactive() {
		active[num] = 0;
	}
	
	/**
	 * Move to the next missile slot for processing
	 */
	public void tick () {
		if (++num == 3) {
			num = 0;
			if (++frame == 4) {
				frame = 0;
			}
		}
	}
	
	/**
	 * Drop a missile from X,Y
	 * @param x
	 * @param y
	 * @return true if succeeded, false if current slot not available
	 */
	public boolean dropMissile (int x, int y) {

		if (active[num] == 0) {
			Missile.x[num] = x;
			Missile.y[num] = y;
			active[num] = 1;
			explode[num] = 0;
			return true;
		} else {
			return false;
		}

	}
	
	public int getX() {
		return x[num];
	}
	
	public int getY() {
		return y[num];
	}
	
	private boolean checkCollision (BufferedImage image, int xPos, int yPos) {
				
		int w = image.getWidth();
		int h = image.getHeight();
		// The destination area to be written into for comparison
		BufferedImage destImg = Window.getPlayArea().getSubimage(xPos, yPos, w * 2, h * 2);

		// Scan the image and destination and flag a collision if both of them
		// have a pixel in the same position
		for (int x = 0; x < w; ++x) {
			for (int y = 0; y < h; ++y) {
				if (((image.getRGB(x, y) & 0xffffff) > 0) && ((destImg.getRGB(x * 2, y * 2) & 0xffffff) > 0)) {
					return true;
				}
			}
		}
		return false;
	}

	public void update() {

		Graphics g = Window.getPlayAreaGraphics();
		// Get the image for this particular missile
		BufferedImage mImg = spriteSheet.getSubimage(num * 3, frame * 8, 3, 8);
		
		if (isActive()) {

			int x = Missile.x[num];
			int y = Missile.y[num];
			
			if (explode[num] == 0) {
				// If missile not exploding, erase it and move down
				g.drawImage(mImg, x, y, 6, 16, null);
				g.setXORMode(Color.BLACK);
				g.drawImage(mImg, x, y, 6, 16, null);
				g.setPaintMode();
				if (y >= 454) {
					explode[num] = 8;
				} else {

					// Check for collision in the area the missile is about to enter
					if (checkCollision(mImg, x, y + 8)) {
						y += 8;
						Missile.y[num] = y;
						mImg = spriteSheet.getSubimage(num * 3, ((frame + 1) % 4) * 8, 3, 8);
						g.drawImage(mImg, x, y, 6, 16, null);
						explode[num] = 8;
						// Did missile hit our base?
						if (Missile.y[num] >= 420 && Missile.y[num] < 448) {
							if (Missile.x[num] > Game.getBaseX() + 2 && Missile.x[num] < Game.getBaseX() + 42) {
								Game.setBaseExplode();
							}
						}
					} else {
						y += 8;
						Missile.y[num] = y;
						mImg = spriteSheet.getSubimage(num * 3, ((frame + 1) % 4) * 8, 3, 8);
						g.drawImage(mImg, x, y, 6, 16, null);
					}
				}
			} else if (explode[num] == 8) {
				g.drawImage(mImg, x, y, 6, 16, null);
				g.setXORMode(Color.BLACK);
				g.drawImage(mImg, x, y, 6, 16, null);
				g.setPaintMode();
				y += 4;
				Missile.y[num] = y;
				g.drawImage(Game.missileExplosion, x - 6, y , 16, 16, null);
				explode[num]--;
			} else if (explode[num] > 0 && --explode[num] == 0) {
				setInactive();
				g.drawImage(Game.missileExplosion, x - 6, y , 16, 16, null);
				g.setXORMode(Color.BLACK);
				g.drawImage(Game.missileExplosion, x - 6, y , 16, 16, null);
				g.setPaintMode();
			}
		}
	}
}
