package org.verboseStory.ui;
import org.verboseStory.engine.GameEngine;
import java.awt.Color;

/**
 * Connector for {@link InventoryWindow}.
 */
public final class Inventory {
    private static InventoryWindow window;

    private Inventory() {}

    public static void setWindow(InventoryWindow w) { window = w; }
    public static InventoryWindow getWindow() { return window; }

    public static void updateInventoryWindow(Color color, String message) {
        if (window != null) window.appendToWindow(color, message);
        else GameEngine.red_chat_output("Inventory Window NULL");
    }
}