package org.verboseStory.ui;
import org.verboseStory.engine.GameEngine;
import java.awt.Color;

/**
 * Facade that holds a reference to the {@link SceneWindow}
 * and forwards colour‑coded messages.
 */
public final class Scene {
    private static SceneWindow window;

    private Scene() {}

    public static void setWindow(SceneWindow w) { window = w; new GameEngine.DelayedMessage.SomeMessage(500, ".ChatWindow");}
    public static SceneWindow getWindow() { return window; }

    public static void updateSceneWindow(Color color, String message) {
        if (window != null) window.appendScene(color, message);
        else GameEngine.red_chat_output("Scene Window NULL");
    }
}