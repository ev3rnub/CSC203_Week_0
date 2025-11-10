// namespace
package org.verboseStory;

// my classes
import org.verboseStory.engine.GameEngine; //logic, api connector
import org.verboseStory.engine.GameEngineStaticHolder; // reference
import org.verboseStory.ui.GameWindow; // Main game window
import org.verboseStory.ui.InventoryWindow; // hidden inventory window
import org.verboseStory.ui.Inventory; // inventory connector
import org.verboseStory.ui.SceneWindow; // hidden scene history window
import org.verboseStory.ui.Scene; // scene history connector
import org.verboseStory.ui.ChatWindow; // chat/dialog window.

// import swing libraries along with concurrency utilities.
import javax.swing.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

 //Main gets executed first. This main builds the UI, defines a linked blocking queue, and uses swings EDT invokeLater
 //and makes said queue available to the rest of the JFrames.
 //finally start the GameEngine in a background thread within the EDT.
public class Main {
    public static void main(String[] args) {
        // One queue is enough for all UI → engine communication.
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        // Define UI on a Event Dispatch Thread using Swing Utilities invokeLater.
        SwingUtilities.invokeLater(() -> {
            // Future ME: Refactor window generation.
            // Main GUI Instance
            GameWindow gameWindow = new GameWindow(queue);
            ChatWindow.setWindow(gameWindow);
            gameWindow.setVisible(true);

            // Scene History GUI Instance
            SceneWindow sceneWindow = new SceneWindow(queue);
            Scene.setWindow(sceneWindow);
            sceneWindow.setVisible(false);

            // Inventory GUI Instance
            InventoryWindow inventoryWindow = new InventoryWindow(queue, "Inventory");
            Inventory.setWindow(inventoryWindow);
            inventoryWindow.setVisible(false);

            //Game Engine Instance
            GameEngine engine = new GameEngine(queue);
            // a reference so that any other part of my code can interact with GameEngine without passing it around.
            GameEngineStaticHolder.engine = engine;

            //Spawn a new thread, label it as an engineThread, set daemon property to true, allowing the JVM to exit cleanly,
            // and then start the thread within swings EDT.
            Thread engineThread = new Thread(engine::run, "GameEngine");
            engineThread.setDaemon(true);
            engineThread.start();
        });
    }
}