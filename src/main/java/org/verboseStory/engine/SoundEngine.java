package org.verboseStory.engine;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class SoundEngine {

    static Clip someClip = null;

    public void run() {
                try {
                    File audioFile = new File("src/main/resources/sound/music/tickTock.wav");
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
                    someClip = AudioSystem.getClip();
                    someClip.open(audioInputStream);
                    someClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the audio continuously

                    // Keep the program alive to allow the audio to play
                    // You might want to add a mechanism to stop the loop, e.g., user input
                    Thread.sleep(Long.MAX_VALUE);

                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore the interrupted status
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

    public static void playMusic() {
        someClip.start();
    }
}