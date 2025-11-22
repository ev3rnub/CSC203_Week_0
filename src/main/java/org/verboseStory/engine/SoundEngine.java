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

    private static long currentFrameTime = 0;
    private static long startFrameTime = 0;
    private static Clip someClip = null;
    private static AudioInputStream audioInputStream = null;

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
                    someClip.start();
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                    e.printStackTrace();
                }
    }

    public static void stopMusic() {
        if (someClip != null) {
            if (someClip.isRunning()){
                someClip.stop();
                currentFrameTime = someClip.getFramePosition();
            }
        }
    }

    public static void shutdown() {
        if (someClip != null) {
            if (someClip.isRunning()){
                someClip.stop();
                someClip.close();
            }
            someClip = null;
        }
    }

    public static void playMusic() {
        if (someClip != null && !someClip.isRunning()) {
            someClip.setFramePosition(0);
            someClip.loop(Clip.LOOP_CONTINUOUSLY);
            someClip.start();
        }
    }
}