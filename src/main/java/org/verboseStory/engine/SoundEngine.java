package org.verboseStory.engine;
import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
/**
 * This is my sound engine, it plays one .wav file using javaax.sound.sampled, using Clip and AudioInputStream
 * to play my .wav file for music.
 * It has a start/stop, its supposed to repeat. I plan on adding additional functionality.
 * */

public class SoundEngine {
    private static long currentFrameTime = 0;
    private static long startFrameTime = 0;
    private static Clip someClip = null;
    private static AudioInputStream audioInputStream = null;

    // reads the .wave file into an InputStream object, then adds it to a BufferedInputStream, define a
    // someClip, open the audio input stream, tell it to loop continously and then start it.
    public void run() {
                try {
                    InputStream audioStream = getClass().getResourceAsStream("/sound/music/tickTock_8b.wav");
                    if (audioStream == null) {
                        throw new IOException("Audio file not found in resources");
                    }
                    BufferedInputStream bufferedStream = new BufferedInputStream(audioStream);
                    audioInputStream = AudioSystem.getAudioInputStream(bufferedStream);
                    someClip = AudioSystem.getClip();
                    someClip.open(audioInputStream);
                    someClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the audio continuously
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                    e.printStackTrace();
                }
    }
    // stop the clip and make note of the current frame time we stoped at.
    public static void stopMusic() {
        if (someClip != null) {
            if (someClip.isRunning()){
                someClip.stop();
                currentFrameTime = someClip.getFramePosition();
            }
        }
    }

    // shuts down someClip
    public static void shutdown() {
        if (someClip != null) {
            if (someClip.isRunning()){
                someClip.stop();
                someClip.close();
            }
            someClip = null;
        }
    }
    //plays music from the currentFrameTime.
    public static void playMusic() {
        if (someClip != null && !someClip.isRunning()) {
            someClip.setFramePosition((int) currentFrameTime);
            someClip.loop(Clip.LOOP_CONTINUOUSLY);
            someClip.start();
        }
    }
}