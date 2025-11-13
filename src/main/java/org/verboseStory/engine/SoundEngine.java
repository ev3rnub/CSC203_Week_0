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
                    someClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the audio continuously
                    someClip.open(audioInputStream); // open/play the audio

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
        someClip.start();
    }
}