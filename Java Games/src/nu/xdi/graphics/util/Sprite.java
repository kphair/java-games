package nu.xdi.graphics.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * @author kevin
 *
 * A sprite class which can load from disk or 
 * take data from an existing image in memory
 */
public class Sprite {

	private int x = 0;
	private int y = 0;
	private int width = 0;
	private int height = 0;
	private int dx = 0;
	private int dy = 0;
	private double scale = 1.0d;
	private int frames = 1;
	private int currentFrame = 0;
	private BufferedImage spriteImage;

	/*
	 * Create a new single-frame sprite with the provided image
	 */
	public Sprite(BufferedImage image) {
		spriteImage = image;
		width = image.getWidth();
		height = image.getHeight();
	}
	/*
	 * Create a multi-frame sprite from the supplied image and frame count
	 */
	public Sprite(BufferedImage image, int frameCount) {
		spriteImage = image;
		frames = frameCount;
		width = spriteImage.getWidth() / frameCount;
		height = spriteImage.getHeight();
	}

	/*
	 * Create a new single-frame sprite from the filename supplied
	 */
	public Sprite load(String path) {
		spriteImage = new Images().loadImage(path);
		width = spriteImage.getWidth();
		height = spriteImage.getHeight();
		return this;
	}
	/* Create a new multi-frame sprite from the filename and frame count supplied
	 */
	public Sprite load(String path, int frameCount) {
		spriteImage = new Images().loadImage(path);
		width = spriteImage.getWidth() / frameCount;
		height = spriteImage.getHeight();
		return this;
	}
	
	/* Draw a BufferedImage on a Graphic using stored scale
	 */
	public void draw(Graphics g) {
		BufferedImage i;
		if (spriteImage != null) {
			if (frames != 1) {
				i = spriteImage.getSubimage(currentFrame * width, 0, width, height);
			} else {
				i = spriteImage;
			}
			g.drawImage(i, x, y, (int)(scale * i.getWidth()), (int)(scale * i.getHeight()), null);
		}
	}
	
	/* Get the number of animation frames in the sprite
	 */
	public int getNumFrames() {
		return frames;
	}
	/* Set the current animation frame in the sprite
	 */
	public void setFrame(int frameNum) {
		currentFrame = frameNum;
	}
	/* Get the current animation frame
	 */
	public int getFrame() {
		return currentFrame;
	}
	/* Advance to the next animation frame in the sequence
	 */
	public void nextFrame() {
		currentFrame = ++currentFrame % frames;
	}

	/* Add the DX and DY to the Sprite co=ordinates
	 */
	public void move() {
		x += dx;
		y += dy;
	}
	
	/* Set the X and Y co-ordinates of the sprite
	 */
	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/* Get the position of the sprite
	 */
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	
	/* Get the dimensions of the sprite
	 */
	public int getWidth() {
		return (int)(width * scale);
	}
	public int getHeight() {
		return (int)(height * scale);
	}

	/* Get and set the scaling value for the sprite
	 */
	public double getScale() {
		return scale;
	}
	public void setScale(double newScale) {
		this.scale = newScale;
	}

	/* Get and set the X and Y motions for the sprite
	 */
	public int getDX() {
		return dx;
	}
	public void setDX(int newDX) {
		this.dx = newDX;
	}
	public int getDY() {
		return dy;
	}
	public void setDY(int newDY) {
		this.dy = newDY;
	}

}
