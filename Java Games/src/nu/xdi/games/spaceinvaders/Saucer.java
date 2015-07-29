package nu.xdi.games.spaceinvaders;

import java.awt.Color;
import java.awt.Graphics;

/**
 * @author Kevin Phair
 * @date 28 Jul 2015
 */
public class Saucer {

	private int x = 0;
	private int y = 0;
	private int active = 0;
	private int explodeTimer = 0;
	private int ticker = 0;
	private int score = 0;

	Saucer () {
		super ();
	}
	
	Saucer (int score) {
		this.x = 0;
		this.y = 80;
		this.active = 1;
		this.explodeTimer = 0;
		this.ticker = 0;
		this.score = score;

		// Erase the last drawn image
		Graphics g = Window.getPlayAreaGraphics();
		g.drawImage(Game.saucerSprite, x, y, null);
		Sound.saucer.play();
	}
	
	public void update() {

		if (active > 0 && ++this.ticker == 3) {
			this.ticker = 0;

			Graphics g = Window.getPlayAreaGraphics();

			/*
			 * Saucer is progressing across the screen
			 */
			if (explodeTimer == 0) {
				// Erase the last drawn image
				g.drawImage(Game.saucerSprite, x, y, 32, 16, null);
				g.setXORMode(Color.BLACK);
				g.drawImage(Game.saucerSprite, x, y, 32, 16, null);
				g.setPaintMode();
	
				// Draw it in the new position
				x += 4;
				if (x < Window.getPlayArea().getWidth()) {
					g.drawImage(Game.saucerSprite, x, y, 32, 16, null);
				} else {
					active = 0;
					Sound.saucer.stop();
				}
				
			/*
			 * Saucer has just been hit!
			 */
			} else if (explodeTimer++ == 1) {

				Sound.saucer.stop();
				Sound.saucerExplode.play();

				// Erase the last drawn image
				g.drawImage(Game.saucerSprite, x, y, 32, 16, null);
				g.setXORMode(Color.BLACK);
				g.drawImage(Game.saucerSprite, x, y, 32, 16, null);
				g.setPaintMode();
	
				// Draw the explosion in its place
				g.drawImage(Game.saucerExplosion, x, y, 48, 16, null);
				
			/*
			 * Saucer is exploding
			 */
			} else if (explodeTimer++ > 1) {
				// Erase the explosion and show the points from hitting the saucer
				if (explodeTimer == 60) {
					// Erase the explosion
					g.drawImage(Game.saucerExplosion, x, y, 48, 16, null);
					g.setXORMode(Color.BLACK);
					g.drawImage(Game.saucerExplosion, x, y, 48, 16, null);
					g.setPaintMode();
		
					Text.print(g, x, y, "" + score, Color.RED);
				} else if (explodeTimer == 240) {
					g.setColor(Color.BLACK);
					g.fillRect(x, y, 48, 16);
					explodeTimer = 0;
					active = 0;
				}
			}
			
		}
	}
	
	/*
	 * Tell the saucer to explode
	 */
	public void explode() {
		explodeTimer = 1;
	}
	
	public int getX () {
		return this.x;
	}

	public int getY () {
		return this.y;
	}
	
	public int getScore () {
		return this.score;
	}
	
	public boolean isActive () {
		return active != 0;
	}
	
	public boolean isAlive () {
		return (active != 0 && explodeTimer == 0);
	}

}
