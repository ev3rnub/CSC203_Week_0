package org.verboseStory.engine;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.verboseStory.ui.ChatWindow;
import org.verboseStory.ui.Scene;
import org.verboseStory.ui.Inventory;
import org.verboseStory.api.XaiApi;
import java.awt.Color;
import java.util.concurrent.BlockingQueue;

import static org.verboseStory.ui.ChatWindow.updateChatWindow;

/**
 * Core game loop.  It receives user input from the UI (via a
 * {@link BlockingQueue}) and talks to the XAI API accordingly.
 */
public final class GameEngine {

    // ----- colour‑escape constants kept for backward compatibility -----
    private static final String WHITE   = "\u001B[37m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String BLUE    = "\u001B[34m";
    private static final String GREEN   = "\u001B[32m";
    private static final String RED     = "\u001B[31m";
    private static final String BLACK   = "\u001B[30m";

    // ----- mutable state -----
    public static volatile boolean STARTED = false;
    public static volatile String playerKey = "";

    public final BlockingQueue<String> inputQueue;

    public GameEngine(BlockingQueue<String> inputQueue) {
        this.inputQueue = inputQueue;
    }

    // ----- UI helpers ---------------------------------------------------
    private String readLine() throws InterruptedException {
        return inputQueue.take();
    }

    // ----- public entry point --------------------------------------------
    public void run() {
        try {
            get_key_word("welcome");
            dialog_start();
        } catch (Exception e) {
            red_chat_output("UNHANDLED EXCEPTION: " + e);
            e.printStackTrace();
        }
    }

    // ----- conversation flow ---------------------------------------------
    public void get_key_word(String aType) throws Exception {
        switch (aType) {
            case "welcome" -> {
                white_chat_output("Welcome to Verbose Hominid, a text based adventure in a fictional hominid world!");
                white_chat_output("Enter your Character's First Name, or what Verbose Hominid inhabitants can call you?");
                String keyWord = readLine();
                magenta_chat_output("Your Character Name is: " + keyWord);
                green_chat_output("Is this CORRECT? Y/N: ");
                String confirmed = readLine();
                if (confirmed.equalsIgnoreCase("y") || confirmed.equalsIgnoreCase("yes")) {
                    STARTED = true;
                    playerKey = keyWord;
                    red_chat_output(playerKey + ", Your simulation is starting!");
                } else {
                    STARTED = false;
                    get_key_word("welcome");   // retry
                }
            }
            default -> red_chat_output("Invalid Input, received: " + aType);
        }
    }

    // ----- start the first API call ---------------------------------------
    public void dialog_start() {
        XaiApi.invokeResponseFromGrok("BEGIN_GAME");
    }

    // ----- colour‑coded helpers (static, used by UI façade) ---------------
    public static void blue_chat_output(String s)   { updateChatWindow(Color.BLUE,   s); }
    public static void red_chat_output(String s)    { updateChatWindow(Color.RED,    s); }
    public static void green_chat_output(String s)  { updateChatWindow(Color.GREEN,  s); }
    public static void white_chat_output(String s)  { updateChatWindow(Color.WHITE,  s); }
    public static void magenta_chat_output(String s){ updateChatWindow(Color.MAGENTA,s); }

    public static void blue_scene_output(String s)   { Scene.updateSceneWindow(Color.BLUE,   s); }
    public static void red_scene_output(String s)    { Scene.updateSceneWindow(Color.RED,    s); }
    public static void green_scene_output(String s)  { Scene.updateSceneWindow(Color.GREEN,  s); }
    public static void white_scene_output(String s)  { Scene.updateSceneWindow(Color.WHITE,  s); }
    public static void magenta_scene_output(String s){ Scene.updateSceneWindow(Color.MAGENTA,s); }

    public static void blue_inventory_output(String s)   { Inventory.updateInventoryWindow(Color.BLUE,   s); }
    public static void red_inventory_output(String s)    { Inventory.updateInventoryWindow(Color.RED,    s); }
    public static void green_inventory_output(String s)  { Inventory.updateInventoryWindow(Color.GREEN,  s); }
    public static void white_inventory_output(String s)  { Inventory.updateInventoryWindow(Color.WHITE,  s); }
    public static void magenta_inventory_output(String s){ Inventory.updateInventoryWindow(Color.MAGENTA,s); }
}