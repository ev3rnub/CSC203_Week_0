package org.verboseStory;

import org.verboseStory.api.XaiApi;
import org.verboseStory.engine.GameEngine;
import org.verboseStory.engine.GameEngineStaticHolder;
import org.verboseStory.ui.GameWindow;
import org.verboseStory.ui.InventoryWindow;
import org.verboseStory.ui.Inventory;
import org.verboseStory.ui.SceneWindow;
import org.verboseStory.ui.Scene;
import org.verboseStory.ui.ChatWindow;

import javax.swing.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Application entry point – builds the UI, wires the queues,
 * starts the engine in a background thread.
 */
public class Main {

    public static void main(String[] args) {
        // One queue is enough for all UI → engine communication.
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        // Build UI on the EDT.
        SwingUtilities.invokeLater(() -> {
            GameWindow gameWindow = new GameWindow(queue);
            ChatWindow.setWindow(gameWindow);
            gameWindow.setVisible(true);

            SceneWindow sceneWindow = new SceneWindow(queue);
            Scene.setWindow(sceneWindow);
            sceneWindow.setVisible(false);

            InventoryWindow inventoryWindow = new InventoryWindow(queue, "Inventory");
            Inventory.setWindow(inventoryWindow);
            inventoryWindow.setVisible(false);

            GameEngine engine = new GameEngine(queue);
            GameEngineStaticHolder.engine = engine;

            // Run engine on its own thread (daemon so JVM can exit cleanly).
            Thread engineThread = new Thread(engine::run, "GameEngine");
            engineThread.setDaemon(true);
            engineThread.start();
        });
    }
}