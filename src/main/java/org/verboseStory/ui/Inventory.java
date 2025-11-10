package org.verboseStory.ui;
import org.verboseStory.engine.GameEngine;
import java.awt.Color;

//Connector for InventoryWindow.
public final class Inventory {
    private static InventoryWindow window;
    // stores reference for our inventory window
    public static void setWindow(InventoryWindow w) { window = w; }
    public static InventoryWindow getWindow() { return window; }
    // this method appends some message to our output queue.
    public static void updateInventoryWindow(Color color, String message) {
        if (window != null) window.appendToWindow(color, message);
        else GameEngine.red_chat_output("Inventory Window NULL");
    }
}