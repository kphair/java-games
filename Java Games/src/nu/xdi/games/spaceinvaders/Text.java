package nu.xdi.games.spaceinvaders;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * @author Kevin Phair
 * @date 18 Jul 2015
 * @version 1.0.0
 */
public class Text {

	private static BufferedImage fontSheet;

	/**
	 * Load the font sheet from disk
	 * 
	 * @return true if load from file succeeded, false otherwise
	 */
	public static boolean loadFont() {
		
		fontSheet = new ImageRes().loadImage("resources/font.png");
		if (fontSheet == null) {
			System.out.println("Font not available!");
			return false;
		}
		return true;
	}

	/**
	 * Print a character from the font sheet
	 * 
	 * @param graphics context for the area to draw in
	 * @param x
	 * @param y
	 * @param character (zero-based) to look up from sheet
	 */
	public static void printChar(Graphics g, int x, int y, int c) {
		BufferedImage charImage = fontSheet.getSubimage(c * 8, 0, 8, 8);
		int w = charImage.getWidth();
		int h = charImage.getHeight();

		for (int cy = 0; cy < h; ++cy) {
			for (int cx = 0; cx < w; ++cx) {
				if ((charImage.getRGB(cx, cy) & 0xffffff) > 0) {
					g.fillRect(x + cx * 2, y + cy * 2, 2, 2);
				}
			}
		}
	}
	
	/**
	 * Print a string using the font in the font sheet with a delay between each character
	 * 
	 * @param graphics context for area to draw in
	 * @param x
	 * @param y
	 * @param text to print
	 * @param delay in frames between characters
	 */
	public static void print(Graphics g, int x, int y, String text, int delay) {
		char c;
		
		for (int i = 0; i < text.length(); ++i) {
			c = text.charAt(i);
			if (c >= '0' && c <= '9') {
				printChar(g, x, y, c - '0');
				x += 16;
			} else if (c >= 'A' && c <= 'Z') {
				printChar(g, x, y, c - 'A' + 10);
				x += 16;
				
			} else {
				switch(c) {
					case '<': printChar(g, x, y, 36); break;
					case '>': printChar(g, x, y, 37); break;
					case '?': printChar(g, x, y, 38); break;
					case '-': printChar(g, x, y, 39); break;
					case '=': printChar(g, x, y, 40); break;
					case '*': printChar(g, x, y, 41); break;
					case 'y': printChar(g, x, y, 42); break;
				}
				x += 16;
			}
			if (delay > 0) InvadersApp.pause(delay);
		}
	}
	
	/**
	 * Print a string with specified colour
	 * @param graphics context for area to draw in
	 * @param x
	 * @param y
	 * @param text to print
	 * @param colour of text
	 */
	public static void print(Graphics g, int x, int y, String text, Color c) {
		g.setColor(c);
		print(g, x, y, text, 0);
	}

	/**
	 * Print a string with current colour
	 * @param graphics context for area to draw in
	 * @param x
	 * @param y
	 * @param text to print
	 */
	public static void print(Graphics g, int x, int y, String text) {
		print(g, x, y, text, 0);
	}

}
