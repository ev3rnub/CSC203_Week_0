package org.verboseStory.engine;
import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class SoundEngine {

    static Clip someClip = null;
    static AudioInputStream audioInputStream = null;
    public void run() {
                try {
                    InputStream audioStream = getClass().getResourceAsStream("/sound/music/tickTock_8b.wav");
                    if (audioStream == null) {
                        throw new IOException("Audio file not found in resources");
                    }
                    BufferedInputStream bufferedStream = new BufferedInputStream(audioStream);
                    audioInputStream = AudioSystem.getAudioInputStream(bufferedStream);
                    someClip = AudioSystem.getClip();
                    someClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the audio continuously
                    someClip.open(audioInputStream); // open the audio
                    someClip.start();
                    // Keep the program alive to allow the audio to play
                    //Thread.sleep(Long.MAX_VALUE);
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                    e.printStackTrace();
                }
    }

    public static void stopMusic() {
        if (someClip != null) {
            if (someClip.isRunning()){
                someClip.stop();
            }
        }
    }

    public static void endMusic() {
        if (someClip != null) {
            if (someClip.isRunning()){
                someClip.stop();
                someClip.close();
            }
        }
    }

    public static void playMusic() {
        if (someClip != null) {
            if (!someClip.isRunning()){
                someClip.start();
            }
        }
    }
}