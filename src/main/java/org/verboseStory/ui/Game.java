package org.verboseStory.ui;
//my classes
import org.verboseStory.engine.RegexEngine;

//std
import java.awt.Color;

/**
 * A tiny connector that forwards messages to the GameWindow.
 */
public final class Game {
    private static GameWindow window;
    // stores reference for our game window
    public static void setWindow(GameWindow w) { window = w; }
    // this class appends some message to our output queue. Then passes the message to RegexEngine to be parsed.
    public static void updateChatWindow(Color color, String message) {
        if (window != null) {
            // appendChat to our game window
            window.appendChat(color, message);
            // regex engine parse strings.
            RegexEngine.parseOutput(message);
        }
    }
}