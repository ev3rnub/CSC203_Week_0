package org.verboseStory.ui;

import org.verboseStory.engine.RegexEngine;
import org.verboseStory.engine.GameEngine;
import java.awt.Color;
import javax.swing.SwingUtilities;
import javax.swing.text.StyledDocument;

/**
 * A tiny connector that forwards colored messages to the {@link GameWindow}.
 * It is deliberately package‑private – only UI code should call it.
 */
public final class ChatWindow {
    private static GameWindow window;

    private ChatWindow() { }

    public static void setWindow(GameWindow w) { window = w; }

    public static void updateChatWindow(Color color, String message) {
        if (window != null) {
            window.appendChat(color, message);
            // Let the regex engine parse.
            RegexEngine.parseOutput(message);
        } else {
            // Fallback – still visible if UI never started.
            GameEngine.red_chat_output(message);
        }
    }
}