package org.verboseStory.engine;
import org.verboseStory.api.LocalOllama_API;
import org.verboseStory.ui.Scene;
import org.verboseStory.ui.Inventory;
import org.verboseStory.api.Xai_Api;
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
    public class green_title {
        public static void printAsciiArt() {
            green_chat_output("V   VEEEEERRRR BBBB  OOO  SSS EEEEE     H   H OOO M   M III N   N III DDDD       SSS U   UN   NH   H OOO M   MEEEEE  1   333 ");
            green_chat_output(" V  VE    R   RB   BO   OS    E         H   HO   OMM MM  I  NN  N  I  D   D  :  S    U   UNN  NH   HO   OMM MME     11      3");
            green_chat_output(" V V EEE  RRRR BBBB O   O SSS EEE       HHHHHO   OM M M  I  N N N  I  D   D      SSS U   UN N NHHHHHO   OM M MEEE    1    33 ");
            green_chat_output("  V  E    R R  B   BO   O   S E         H   HO   OM   M  I  N  NN  I  D   D  :     S U   UN  NNH   HO   OM   ME      1      3");
            green_chat_output("  V  EEEEER  R BBBB  OOO SSS  EEEEE     H   H OOO M   M III N   N III DDDD      SSS   UUU N   NH   H OOO M   MEEEEE11111 333 ");
            green_chat_output("\n\n");
        }
    }
    // ----- conversation flow ---------------------------------------------
    public void get_key_word(String aType) throws Exception {
        switch (aType) {
            case "welcome" -> {
                green_title aTitle = new green_title();
                aTitle.printAsciiArt();
                white_chat_output("Welcome to Verbose Hominid, a Science Fiction/Fantasy text based adventure in a fictional hominid world! Work-In-Progress");
                blue_chat_output("Note0: There is currently no in game music, use your favorite non vocal music playlist.");
                blue_chat_output("Note1: Account and account Phrase is placeholder, so enter what you will");
                green_chat_output("Note2: You can use your words instead of using the options given. Use your imagination.");
                white_chat_output("\n");
                white_chat_output("Enter an Account Name, this will be used to access your session.");
                String keyWord = readLine();
                magenta_chat_output("Your Account Name is: " + keyWord);
                white_chat_output("Now enter a phrase to tie to your account name. Example: The quick brown fox jumps over the lazy Story Master");
                white_chat_output("This phrase will be used as a password to access the account name of " + keyWord);
                String keyPhrase = readLine();
                magenta_chat_output("Your Account Name is: " + keyWord + " and Account Phrase is " + keyPhrase + ". Is this Correct? Y/N");
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

    public static class DelayedMessage {
        public static class SomeMessage implements Runnable {

            private final long delayMs;      // how long we wait
            private final String message;   // what we print afterwards

            /**
             * Build a SomeMessage.
             * Display it after some time.
             * @param someTime   delay in **milliseconds** (you can pass seconds * 1000)
             * @param someString text that will be printed after the delay
             *
             *             ##EXAMPLE
             *             // 2‑second delay, then print "Hello, world!"
             *             SomeMessage dm = new SomeMessage(2000, "Hello, world!");
             *             dm.start();
             *
             *             // You can fire‑and‑forget more messages …
             *             new SomeMessage(500, "First quick message").start();
             *             new SomeMessage(1500, "Second message after 1.5s").start();
             *
             *             // Keep main alive long enough to see the output (optional)
             *             try { Thread.sleep(2500); } catch (InterruptedException ignored) {}
             */
            public SomeMessage(int someTime, String someString) {
                if (someTime < 0) {
                    throw new IllegalArgumentException("Delay must be non‑negative");
                }
                this.delayMs = someTime;
                this.message = someString;
            }

            /** executed in a separate thread. */
            @Override
            public void run() {
                try {
                    Thread.sleep(delayMs);
                    white_chat_output(message);
                } catch (InterruptedException e) {
                    // Preserve the interrupt status and give a helpful note
                    Thread.currentThread().interrupt();
                    red_chat_output("SomeMessage was interrupted before printing.");
                }
            }

            /** Convenience helper – starts the background thread immediately. */
            public void start() {
                Thread t = new Thread(this, "SomeMessage-" + delayMs + "ms");
                t.setDaemon(true);           // optional: makes JVM exit even if still sleeping
                t.start();
            }
        }
    }
    // ----- start the first API call ---------------------------------------
    public void dialog_start() {
        Xai_Api.invokeResponseFromGrok("BEGIN_GAME"); // XAI API
        //LocalOllama_API.invokeResponseFromGrok("BEGIN_GAME");  //Local Ollama instance #Future
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