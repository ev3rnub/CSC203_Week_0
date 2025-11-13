package org.verboseStory.engine;

// my classes
import org.verboseStory.ui.Scene;
import org.verboseStory.ui.Inventory;
import org.verboseStory.api.Xai_Api;
import static org.verboseStory.ui.Game.updateChatWindow;

// std
import java.awt.Color;
import java.util.concurrent.BlockingQueue;

//My simple game engine. It displays the title, and generates the welcome text that follows. It also receives player input from the UI via and sends to the XAI API to be processed.

public final class GameEngine {

    // working vars
    public static volatile boolean STARTED = false;
    public static volatile String playerKey = "";
    public static volatile String playerPhrase = "";
    // our input queue
    public final BlockingQueue<String> inputQueue;

    public GameEngine(BlockingQueue<String> inputQueue) {
        this.inputQueue = inputQueue;
    }

//    return the first string from inputQueue.
    private String readLine() throws InterruptedException {
        return inputQueue.take();
    }

    // the start of it all.
    public void run() {
        try {
            get_key_word("welcome"); //displays welcome text
            dialog_start(); // starts player game/interaction
        } catch (Exception e) {
            red_chat_output("UNHANDLED EXCEPTION: " + e);
            e.printStackTrace();
        }
    }
//    TITLE text in ascii art #FutureRefactor
    public class cyan_title {
        public static void printAsciiArt() {
            cyan_chat_output("V   VEEEEERRRR BBBB  OOO  SSS EEEEE     H   H OOO M   M III N   N III DDDD       SSS U   UN   NH   H OOO M   MEEEEE  1   333 ");
            cyan_chat_output(" V  VE    R   RB   BO   OS    E         H   HO   OMM MM  I  NN  N  I  D   D  :  S    U   UNN  NH   HO   OMM MME     11      3");
            cyan_chat_output(" V V EEE  RRRR BBBB O   O SSS EEE       HHHHHO   OM M M  I  N N N  I  D   D      SSS U   UN N NHHHHHO   OM M MEEE    1    33 ");
            cyan_chat_output("  V  E    R R  B   BO   O   S E         H   HO   OM   M  I  N  NN  I  D   D  :     S U   UN  NNH   HO   OM   ME      1      3");
            cyan_chat_output("  V  EEEEER  R BBBB  OOO SSS  EEEEE     H   H OOO M   M III N   N III DDDD      SSS   UUU N   NH   H OOO M   MEEEEE11111 333 ");
            cyan_chat_output("\n\n");
        }
    }

    // used to display, the title welcome/intro text #FutureRefactor
    public void get_key_word(String aType) throws Exception {
        //if string matches welcome, run case "welcome"
        switch (aType) {
            case "welcome" -> {
                cyan_title aTitle = new cyan_title();
                aTitle.printAsciiArt();
                white_chat_output("Welcome to Verbose Hominid: Sunhome13 (VHSH13), a Science Fiction/Fantasy text based adventure in a fictional hominid world! Work-In-Progress");
                cyan_chat_output("\n\n");
                cyan_chat_output("************* Note Board **************");
                green_chat_output("Note0: There is currently no in game music, use your favorite non vocal music playlist.");
                green_chat_output("Note1: Account and account Phrase is placeholder, so enter what you will");
                green_chat_output("Note2: A xAI api key is required, VHSH13 looks for environmental variable xAI_API_KEY.");
                green_chat_output("Note3: You can use your words instead of using the options given. Use your imagination.");
                green_chat_output("Note4: If you want to command the Story Master use, Command: What is currently in my inventory.");
                green_chat_output("Note5: If you want to question the Story Master use, Question: Where am I, what time is it in the world?");
                cyan_chat_output("\n\n");
                cyan_chat_output("************* Notice Board **************");
                green_chat_output("NOTICE: No save as of yet, and I've not tested long enough to determine if everything works.");
                white_chat_output("\n\n");
                cyan_chat_output("************* INPUT Required *************");
                white_chat_output("Enter an Account Name, this will be used to access your session.");
                String keyWord = readLine();
                cyan_chat_output("Your Account Name is: " + keyWord);
                white_chat_output("Now enter a phrase to tie to your account name. Example: The quick brown fox jumps over the lazy Story Master");
                white_chat_output("This phrase will be used as a password to access the account name of " + keyWord);
                String keyPhrase = readLine();
                cyan_chat_output("Your Account Name is: " + keyWord + " and Account Phrase is " + keyPhrase + ". Is this Correct? Y/N");
                String confirmed = readLine();
                if (confirmed.equalsIgnoreCase("y") || confirmed.equalsIgnoreCase("yes")) {
                    STARTED = true;
                    playerKey = keyWord;
                    playerPhrase = keyPhrase;
                    red_chat_output(playerKey + ", Your simulation is starting!");

                } else {
                    STARTED = false;
                    get_key_word("welcome");   // retry
                }
            }
            default -> red_chat_output("Invalid Input, received: " + aType);
        }
    }

    public void class SaveGame extends Thread {
        String playerKey;
        String playerPhrase;

    }
    // this invokes our LLM API, default is remote. #FutureRefactor
    public void dialog_start() {
        Xai_Api.invokeResponseFromGrok("BEGIN_GAME"); // REMOTE DEFAULT: XAI API
        //LocalOllama_API.invokeResponseFromLocal("BEGIN_GAME"); // LOCAL: Local Ollama instance
    }

    //methods to define color output to game windows. #FutureRefactor
    public static void blue_chat_output(String s)   { updateChatWindow(Color.BLUE,   s); }
    public static void red_chat_output(String s)    { updateChatWindow(Color.RED,    s); }
    public static void green_chat_output(String s)  { updateChatWindow(Color.GREEN,  s); }
    public static void white_chat_output(String s)  { updateChatWindow(Color.WHITE,  s); }
    public static void cyan_chat_output(String s){ updateChatWindow(Color.CYAN,   s); }

    public static void blue_scene_output(String s)   { Scene.updateSceneWindow(Color.BLUE,   s); }
    public static void red_scene_output(String s)    { Scene.updateSceneWindow(Color.RED,    s); }
    public static void green_scene_output(String s)  { Scene.updateSceneWindow(Color.GREEN,  s); }
    public static void white_scene_output(String s)  { Scene.updateSceneWindow(Color.WHITE,  s); }
    public static void cyan_scene_output(String s){ Scene.updateSceneWindow(Color.CYAN,s); }

    public static void blue_inventory_output(String s)   { Inventory.updateInventoryWindow(Color.BLUE,   s); }
    public static void red_inventory_output(String s)    { Inventory.updateInventoryWindow(Color.RED,    s); }
    public static void green_inventory_output(String s)  { Inventory.updateInventoryWindow(Color.GREEN,  s); }
    public static void white_inventory_output(String s)  { Inventory.updateInventoryWindow(Color.WHITE,  s); }
    public static void cyan_inventory_output(String s){ Inventory.updateInventoryWindow(Color.CYAN,s); }
}