/**
 * Sound
 * @author Kevin Phair
 * @date 23 Jul 2015
 * @version 1.0.0
 */
package nu.xdi.games.spaceinvaders;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * @author Kevin Phair
 * @date 23 Jul 2015
 */
public class Sound {
	public static Sound coindDrop = loadSound("/snd/spaceinvaders/coinDrop.wav");
	public static Sound baseFire = loadSound("/snd/spaceinvaders/baseFire.wav");
	public static Sound baseExplosion = loadSound("/snd/spaceinvaders/baseExplode.wav");
	public static Sound invaderExplosion = loadSound("/snd/spaceinvaders/invaderExplode.wav");
	public static Sound saucer = loadSound("/snd/spaceinvaders/saucer.wav");
	public static Sound[] steps = {	loadSound("/snd/spaceinvaders/step1.wav"),
									loadSound("/snd/spaceinvaders/step2.wav"),
									loadSound("/snd/spaceinvaders/step3.wav"),
									loadSound("/snd/spaceinvaders/step4.wav")
	};
	private Clip clip;
	
	/**
	 * Open the sample from filename and store reference in clip variable
	 * @param filename
	 */
	public static Sound loadSound(String filename) {
		Sound sound = new Sound();
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(Sound.class.getResource(filename));
			Clip clip = AudioSystem.getClip();
			clip.open(ais);
			sound.clip = clip;
		} catch (Exception e) {
			System.out.println("Problem loading sound from '" + filename + ";");
		}
		return sound;
	}
	
	/**
	 * Play the clip referred to by this instance
	 */
	public void play() {
		try {
			if (clip != null) {
				clip.stop();	
				clip.setFramePosition(0);
				clip.start();
			}
		} catch (Exception e) {
			System.out.println("Problem playing sound.");
		}
	}

	/**
	 * Stop playing the clip referred to by this instance
	 */
	public void stop() {
		try {
			if (clip != null) {
				clip.stop();	
			}
		} catch (Exception e) {
			System.out.println("Problem stopping sound.");
		}
	}
	
	
}
