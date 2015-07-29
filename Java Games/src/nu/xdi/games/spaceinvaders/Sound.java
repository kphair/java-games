/**
 * Sound
 * @author Kevin Phair
 * @date 23 Jul 2015
 * @version 1.0.0
 */
package nu.xdi.games.spaceinvaders;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * @author Kevin Phair
 * @date 23 Jul 2015
 */
public class Sound {
	public static Sound coindDrop = loadSound("resources/coinDrop.wav");
	public static Sound baseFire = loadSound("resources/baseFire.wav");
	public static Sound baseExplosion = loadSound("resources/baseExplode.wav");
	public static Sound invaderExplosion = loadSound("resources/invaderExplode.wav");
	public static Sound saucer = loadSound("resources/saucer.wav");
	public static Sound saucerExplode = loadSound("resources/saucerExplode.wav");
	public static Sound[] steps = {	loadSound("resources/step1.wav"),
									loadSound("resources/step2.wav"),
									loadSound("resources/step3.wav"),
									loadSound("resources/step4.wav")
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
	
	public void preload() {
		this.setMute(true);
		this.play();
		this.setMute(false);
	}
	
	public void setLevel(float newGain) {
		FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		gain.setValue(newGain);
	}

	public void setMute(boolean newMute) {
		BooleanControl mute = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
		mute.setValue(newMute);
	}
	
	/**
	 * Play the clip referred to by this instance
	 */
	public void play() {
		
		Thread playback = new Thread() {
			public void run() {
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
		};
		playback.start();
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
