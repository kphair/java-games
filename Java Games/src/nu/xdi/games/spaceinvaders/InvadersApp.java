package nu.xdi.games.spaceinvaders;

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
 * Worth 300 points after the first 23 shots and every 15th shot thereafter
 * (including the shot that hits the saucer)
 * 
 */
public class InvadersApp {

	public static void main(String[] args ) {
		
		new Window("Space Invaders", 452,540);

		Text.loadFont();
		Game.StartGame();
		
		//System.exit(0);
	}

}
