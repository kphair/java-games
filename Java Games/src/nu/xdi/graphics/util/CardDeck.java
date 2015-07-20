package nu.xdi.graphics.util;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.imageio.ImageIO;

/**
 * @author Kevin phair
 * @date 18 Jul 2015
 * 
 * A card class which will load in an image of a card deck and provide 
 * methods to extract the cards from the image for use in an application
 * 
 */
public class CardDeck{

	Card[] deck;
	
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
	 * Extract a whole deck from one image, given the number of cards across
	 * and the number down. It will return a new Card[] with the images
	 * set for each card.
	 * It also takes a total number of cards parameter to account for cases
	 * where there are spare slots at the end of image if the number of
	 * cards doesn't make for an exact multiple of the width and height
	 * 
	 * @param a BufferedImage containing all the cards
	 * @param number of cards across
	 * @param number of cards down
	 * @param number of cards in total
	 * 
	 */
	public Card[] extractDeck(BufferedImage cardSheet, int xNum, int yNum, int maxCards) {
		
		Card[] d = new Card[50];
		int numCards = 0;
		
		for (int x = 0; x < xNum; ++x) {
			for (int y = 0; y < yNum; ++y) {
				// Check to see if the array needs to be made bigger
				if (numCards > d.length) {
					d = Arrays.copyOf(d, d.length + 50);
				}
				d[numCards++].setImage(extractCard(cardSheet, 0, 0, cardSheet.getWidth() / xNum, cardSheet.getHeight() / yNum, x, y));
				if (numCards == maxCards) break;
			}
		}
		// Trim the array to the exact size for the number of cards found.
		return Arrays.copyOf(d, numCards);
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
	public BufferedImage extractCard(BufferedImage cardSheet, int x, int y, int xStep, int yStep, int tileX, int tileY) {

		return cardSheet.getSubimage(x + xStep * tileX, y + yStep * tileY, xStep, yStep);
	}

}
