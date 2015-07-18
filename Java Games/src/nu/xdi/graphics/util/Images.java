package nu.xdi.graphics.util;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

/**
 * @author Kevin phair
 *
 * An image class which will provide various useful functions for handling
 * images and loading them from disk
 */
public class Images {

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

	/**
	 * Extract a tile from a BufferedImage and create a new BufferedImage to hold a copy of it
	 * 
	 * @param 	tileSheet	A BufferedImage object referencing the tile sheet
	 * @param	x			The X co-ordinate to start extracting from
	 * @param	y			The Y co-ordinate to start extracting from
	 * @param	xStep		The width to step over to get a new tile
	 * @param	yStep		The height to step down to get a new tile
	 * @param	tileX		How many tiles across the target is
	 * @param	tileY		How many tiles down the target is
	 * 
	 */
	public BufferedImage extractTile(BufferedImage tileSheet, int x, int y, int xStep, int yStep, int tileX, int tileY) {

		return tileSheet.getSubimage(x + xStep * tileX, y + yStep * tileY, xStep, yStep);
	}

}
