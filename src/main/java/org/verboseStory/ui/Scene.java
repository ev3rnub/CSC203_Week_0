package org.verboseStory.ui;
import org.verboseStory.engine.GameEngine;
import java.awt.Color;

/**
 * connector for SceneWindow
 * window setter
 * setWindow( <SceneWindow> someWindow)
 * window getter
 * SceneWindow someWindow = getWindow();
 *
 * append to GameWindow
 */
public final class Scene {
    private static SceneWindow window;
    // stores reference for our scene window
    public static void setWindow(SceneWindow w) { window = w;}
    public static SceneWindow getWindow() { return window; }
    // this method appends some message to our output queue.
    public static void updateSceneWindow(Color color, String message) {
        if (window != null) window.appendScene(color, message);
        else GameEngine.printOutput(Color.RED, "SCENE_CONN", "Scene Window NULL");
    }
}