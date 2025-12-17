package org.verboseStory.engine;

// my classes
import com.google.gson.*;
import org.verboseStory.api.LocalOllama_API;
import org.verboseStory.model.Professor;
import org.verboseStory.model.Student;
import org.verboseStory.ui.Game;
import org.verboseStory.api.Xai_Api;
import org.verboseStory.model.Character;

// std
import java.awt.*;
import java.net.http.HttpClient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.verboseStory.ui.Scene;

//My simple game engine. It displays the title, and generates the welcome text that follows. It also receives player input from the UI via and sends to the XAI API to be processed.

public final class GameEngine {

    // working vars
    public static volatile boolean STARTED = false;
    private static volatile boolean DIALOGSTARTED = false;
    public static volatile boolean INITIAL = true;
    private static String TARGET = "EXTERNALAPI";
    public static volatile String playerKey = "";
    public static HttpClient currentClient;
    private static String getTitle = "GAMEENGINE";
    private JsonObject HISTORY = new JsonObject();

//    A place to store all created characters
    public static volatile ArrayList<Character> playerGroup = new ArrayList<>();

    // our input queue
    public final BlockingQueue<String> inputQueue;
//  game turn counter
    public int turn = 0;
//    HH:MM:SS | Track total "Action" time; GEN-AI will output a time each action and or actions took.
    public static String someTime = "00:00:00"; // "***************** - Time spent 'doing something'"

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
            //displays welcome text
            get_key_word("welcome");
            //displays character creation text
            get_key_word("CREATECHARACTER");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // While STARTED TRY, if NOT dialog started, do x, else ask for input
        // check if player is alive
        // respond accordingly
        while (STARTED) {
            try {
                if (!DIALOGSTARTED) {
                    String someResponse = new_dialog(""); // starts player game/interaction
                    printOutput(Color.CYAN, "Guide: ", someResponse);
                    logHistory(someResponse);
                }else{
                    printOutput(Color.RED, getTitle, "----------------------------------------------------------------------------------");
                    printOutput(Color.RED, getTitle, "----------------------------------INPUT required----------------------------------");
                    printOutput(Color.RED, getTitle, "----------------------------------------------------------------------------------");
                    printOutput(Color.YELLOW, getTitle, "TOTAL ACTION TIME -------------------------------------------HH:MM:SS- " + someTime);
                    printOutput(Color.WHITE, getTitle, "TURN------------------------------ " + turn + "\n\n\n");
                    Boolean isPlayerAlive = is_player_dead();
                    if (isPlayerAlive) {
                        String someInput = readLine();
                        Scene.updateSceneWindow(Color.WHITE, "MULTA Requested Action: " + someInput);
                        printOutput(Color.YELLOW,  playerKey, someInput);
                        logHistory(someInput);
                        String someResponse = new_dialog(someInput);
                        printOutput(Color.CYAN, getTitle, someResponse);
                    }else{
                        Scene.updateSceneWindow(Color.WHITE, "--------------------------------------HOMINID HAS PERISHED--------------------------------------");
                        String someResponse = new_dialog("COMMAND: Output a summary of events in order that lead up to the Hominid's Death from the following:\n " + HISTORY.toString());
                        printOutput(Color.CYAN, getTitle, someResponse);
                        Scene.updateSceneWindow(Color.WHITE, "--------------------------------------HOMINID HAS PERISHED--------------------------------------");
                        logHistory(someResponse);
                        String someNewResponse = Xai_Api.enrichData(HISTORY.getAsJsonObject().toString(), "Use this History; Create a Finished short story based on the game history");
                        Scene.updateSceneWindow(Color.CYAN, someNewResponse);
                        printOutput(Color.RED, getTitle, "\n\n\n----------------------------------->GAME OVER!");

                        Boolean inAfter = true;
                        while (inAfter) {
                            printOutput(Color.CYAN, getTitle, "RESTART or EXIT? [R|E]");
                            String someInput = readLine();
                            if (someInput.equalsIgnoreCase("R")) {
                                DIALOGSTARTED = false;
                            }
                            if (someInput.equalsIgnoreCase("E")) {
                                STARTED = false;
                            }
                        }

                    }
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        printOutput(Color.CYAN, getTitle, "Thanks for playing, using your imagination!");
    }

    public void logHistory(String someString){
        LocalDateTime now = LocalDateTime.now();          // system default zone
        String ts = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm:ss"));
        String someStringPackage = "{ 'TimeStamp':" +  ts + ", 'Turn': " + turn + ", 'History':" + someString + " }";
        int someLength = HISTORY.size();
        int newLength = someLength + 1;
        HISTORY.addProperty("newLength", someStringPackage);
    }

    public static String updateActionTime(String[] someTimeStrArr){
//        parse the incoming array of strings (HH:MM:SS)
        int someHour = Integer.parseInt(someTimeStrArr[0]);
        int someMin = Integer.parseInt(someTimeStrArr[1]);
        int someSec = Integer.parseInt(someTimeStrArr[2]);

        String[] currentTime = getActionTime().split(":");
        int oldHour = Integer.parseInt(currentTime[0]);
        int oldMin = Integer.parseInt(currentTime[1]);
        int oldSec = Integer.parseInt(currentTime[2]);

        int newSec = someSec + oldSec;
        int newMin = someMin + oldMin;
        int newHour = someHour + oldHour;

        if (newSec > 59) {
            newSec -= 60;
            newMin += 1;
        }

        if (newMin > 59){
            newMin -= 60;
            newHour += 1;
        }

        // ensure our hour min and sec are 2 characters in length IE 02:02:02
        String someNewHour = String.format("%02d", newHour);
        String someNewMin = String.format("%02d", newMin);
        String someNewSec = String.format("%02d", newSec);
        
        String someNewTimeStr = someNewHour + ":" + someNewMin + ":" + someNewSec;
        someTime = someNewTimeStr;
        return someNewTimeStr;
    }

    public static String getActionTime(){
        return someTime;
    }

    private Boolean is_player_dead(){
        Character someCharacter = playerGroup.getFirst();
        int a = someCharacter.getHitPoints();
        if (a < 0){
            return false;
        }else{
            return true;
        }
    }

    public static void update_current_player(String someStat, String someOp, int someAmt){
        Character someCharacter = playerGroup.get(0);
        int a;
        int b;
        switch (someStat.toLowerCase()) {
            case "ep": {
                switch (someOp){
                    case "-": {
                        a = someCharacter.getExhaustionPoints();
                        b = a - someAmt;
                        someCharacter.setExhaustionPoints(b);
                        break;
                    }
                    case "+": {
                        a = someCharacter.getExhaustionPoints();
                        b = someAmt + a;
                        someCharacter.setExhaustionPoints(b);
                        break;
                    }
                }
                break;
            }
            case "hp": {
                switch (someOp){
                    case "-": {
                        a = someCharacter.getHitPoints();
                        b = someAmt - a;
                        someCharacter.setHitPoints(b);
                        break;
                    }
                    case "+": {
                        a = someCharacter.getHitPoints();
                        b = someAmt + a;
                        someCharacter.setHitPoints(b);
                        break;
                    }
                }
                break;
            }
            case "xp": {
                switch (someOp){
                    case "-": {
                        a = someCharacter.getXP();
                        b = a - someAmt;
                        someCharacter.setXP(b);
                        break;
                    }
                    case "+": {
                        a = someCharacter.getXP();
                        b = a + someAmt;
                        someCharacter.setXP(b);
                        break;
                    }
                }
                int someXP = someCharacter.getXP();
                if (someXP >= 200 && someXP <= 210){
                    someCharacter.setLevel(2);
                }else if (someXP >= 500 && someXP <= 510){
                    someCharacter.setLevel(3);
                }else if (someXP >= 700 && someXP <= 710){
                    someCharacter.setLevel(4);
                }else if (someXP >= 1000 && someXP <= 1810){
                    someCharacter.setLevel(5);
                }else if (someXP >= 2000 && someXP <= 2100){
                    someCharacter.setLevel(6);
                }else if (someXP >= 4000 && someXP <= 4100){
                    someCharacter.setLevel(7);
                }else if (someXP >= 6000 && someXP <= 6100){
                    someCharacter.setLevel(8);
                }else if  (someXP >= 7500 && someXP <= 7600){
                    someCharacter.setLevel(9);
                }else if (someXP >= 10000 && someXP <= 10100){
                    someCharacter.setLevel(10);
                }
                break;
            }
            case "mp": {
                switch (someOp){
                    case "-": {
                        a = someCharacter.getManaPoints();
                        b = a - someAmt;
                        someCharacter.setManaPoints(b);
                        break;
                    }
                    case "+": {
                        a = someCharacter.getManaPoints();
                        b = someAmt + a;
                        someCharacter.setManaPoints(b);
                        break;
                    }
                }
                break;
            }
            case "ap": {
                switch (someOp){
                    case "-": {
                        a = someCharacter.getActionPoints();
                        b = a - someAmt;
                        someCharacter.setActionPoints(b);
                        break;
                    }
                    case "+": {
                        a = someCharacter.getActionPoints();
                        b = someAmt + a;
                        someCharacter.setActionPoints(b);
                        break;
                    }
                }
                break;
            }
            case "str": {
                switch (someOp){
                    case "-": {
                        a = someCharacter.getStrength();
                        b = a - someAmt;
                        someCharacter.setStrength(b);
                        break;
                    }
                    case "+": {
                        a = someCharacter.getStrength();
                        b = someAmt + a;
                        someCharacter.setStrength(b);
                        break;
                    }
                }
                break;
            }
            case "int": {
                switch (someOp){
                    case "-": {
                        a = someCharacter.getIntelligence();
                        b = a - someAmt;
                        someCharacter.setIntelligence(b);
                        break;
                    }
                    case "+": {
                        a = someCharacter.getIntelligence();
                        b = someAmt + a;
                        someCharacter.setIntelligence(b);
                        break;
                    }
                }
                break;
            }
            case "sta": {
                switch (someOp){
                    case "-": {
                        a = someCharacter.getStamina();
                        b = a - someAmt;
                        someCharacter.setStamina(b);
                        break;
                    }
                    case "+": {
                        a = someCharacter.getStamina();
                        b = someAmt + a;
                        someCharacter.setStamina(b);
                        break;
                    }
                }
                break;
            }
            case "wis": {
                switch (someOp){
                    case "-": {
                        a = someCharacter.getWisdom();
                        b = a - someAmt;
                        someCharacter.setWisdom(b);
                        break;
                    }
                    case "+": {
                        a = someCharacter.getWisdom();
                        b = someAmt + a;
                        someCharacter.setWisdom(b);
                        break;
                    }
                }
                break;
            }
            case "dex": {
                switch (someOp){
                    case "-": {
                        a = someCharacter.getDexterity();
                        b = a - someAmt;
                        someCharacter.setDexterity(b);
                        break;
                    }
                    case "+": {
                        a = someCharacter.getDexterity();
                        b = someAmt + a;
                        someCharacter.setDexterity(b);
                        break;
                    }
                }
                break;
            }
            case "cha": {
                switch (someOp){
                    case "-": {
                        a = someCharacter.getCharisma();
                        b = a - someAmt;
                        someCharacter.setCharisma(b);
                        break;
                    }
                    case "+": {
                        a = someCharacter.getCharisma();
                        b = someAmt + a;
                        someCharacter.setCharisma(b);
                        break;
                    }
                }
                break;
            }
            case "credit": {
                switch (someOp){
                    case "-": {
                        a = someCharacter.getCreditWallet();
                        b = a - someAmt;
                        someCharacter.setCreditWallet(b);
                        break;
                    }
                    case "+": {
                        a = someCharacter.getCreditWallet();
                        b = someAmt + a;
                        someCharacter.setCreditWallet(b);
                        break;
                    }
                }
                break;
            }
        }
        playerGroup.removeFirst();
        playerGroup.addFirst(someCharacter);
    }

    public static void update_current_player_loc(String someString) {
        Character someCharacter = playerGroup.removeFirst();
        someCharacter.setLocation(someString);
        playerGroup.addFirst(someCharacter);
    }

    public String get_player_start_location() {
        int aRandomInt = randomInt(0,7);
        String someLoc;
        switch (aRandomInt){
            case 0:
                someLoc = "Zirrin-<random>";
                break;
            case 1:
                someLoc = "SunHome13-<random>";
                break;
            case 2:
                someLoc = "Aleric Forest-<random>";
                break;
            case 3:
                someLoc = "Arin: Aboveground-<random>";
                break;
            case 4:
                someLoc = "Katikin Moon-<random>";
                break;
            case 5:
                someLoc =  "Arin: Underground-<random>";
                break;
            case 6:
                someLoc = "Desert Living Hole-<random>";
            default:
                someLoc =  "Random";
                break;
        }
        return someLoc;
    }

    public static void update_current_player_inventory(String someOp, String someItem){
        Character someCharacter = playerGroup.getFirst();
        ArrayList<String> currentInventory = someCharacter.getInventory();
        switch(someOp){
            case "-": {
                currentInventory.remove(someItem);
                break;
            }
            case "+": {
                if (!currentInventory.contains(someItem)){
                    currentInventory.add(someItem);
                }
                break;
            }
        }
        playerGroup.removeFirst();
        playerGroup.addFirst(someCharacter);
    }
    // used to display, the title welcome/intro text #FutureRefactor
    public void get_key_word(String aType) throws Exception {
        //if string matches welcome, run case "welcome"
        switch (aType) {
            case "welcome" -> {
                printOutput(Color.CYAN, getTitle, "V   VEEEEERRRR BBBB  OOO  SSS EEEEE     H   H OOO M   M III N   N III DDDD       SSS U   UN   NH   H OOO M   MEEEEE  1   333 ");
                printOutput(Color.CYAN, getTitle," V  VE    R   RB   BO   OS    E         H   HO   OMM MM  I  NN  N  I  D   D  :  S    U   UNN  NH   HO   OMM MME     11      3");
                printOutput(Color.CYAN, getTitle," V V EEE  RRRR BBBB O   O SSS EEE       HHHHHO   OM M M  I  N N N  I  D   D      SSS U   UN N NHHHHHO   OM M MEEE    1    33 ");
                printOutput(Color.CYAN, getTitle,"  V  E    R R  B   BO   O   S E         H   HO   OM   M  I  N  NN  I  D   D  :     S U   UN  NNH   HO   OM   ME      1      3");
                printOutput(Color.CYAN, getTitle,"  V  EEEEER  R BBBB  OOO SSS  EEEEE     H   H OOO M   M III N   N III DDDD      SSS   UUU N   NH   H OOO M   MEEEEE11111 333 ");
                printOutput(Color.CYAN, getTitle,"\n\n");
                printOutput(Color.RED, getTitle,"----------------------------------------| Staff of Vertonal |--|--|--|>");
                printOutput(Color.CYAN, getTitle,"\n\n");
                printOutput(Color.CYAN, getTitle,"Welcome to Verbose Hominid: Sunhome13 - Staff of Vertonal, a Science Fiction/Fantasy text based avatar adventure rogue-like game in a fictional hominid world! Where you assume the role of a 'Multa' who suddenly awakes, gain influence of a fictional hominid, much like a guardian angel or demon? What will you be? You can be a gentle, quiet voice, however, you may find that things move faster when you provide some direction.");
                printOutput(Color.CYAN, getTitle,"\n\n");
                printOutput(Color.GREEN, getTitle,"************* DEV Note Board **************");
                printOutput(Color.GREEN, getTitle," Note 0: Added sample music, composed by me!");
                printOutput(Color.GREEN, getTitle," Note 1: A xAI api key is required, VHSH13D looks for environmental variable xAI_API_KEY.");
                printOutput(Color.GREEN, getTitle," Note 2: To play, simply describe actions with intended outcomes using your words. Use your imagination. Think long term, be cautious, you are not immortal and once you die, game over!");
                printOutput(Color.GREEN, getTitle," Note 3: There is  no 'traditional' magic or spells. Use your words and imagination to describe what you invision. The 'Guide' will determine what is possible and use your hominid's stats to determine what they are capable of.");
                printOutput(Color.GREEN, getTitle," Note 4: There is  no 'traditional' Combat. The 'Guide' uses your character stats to determine what your hominid is capable of.");
                printOutput(Color.GREEN, getTitle," Note 5: If you want to command the GUIDE type, Command: What is currently in my inventory.");
                printOutput(Color.GREEN, getTitle," Note 6: If you want to question the GUIDE use, Question: Where am I, what time is it in the world?");
                printOutput(Color.GREEN, getTitle," Note 7: The Universe is open, you can travel anywhere, and do anything.");
                printOutput(Color.GREEN, getTitle,"\n\n");
                printOutput(Color.YELLOW, getTitle,"************* Notice Board **************");
                printOutput(Color.YELLOW, getTitle," 1. This is a GEN-AI Text based Rogue-Like Avatar RPG. No Saves, No Continues or redos. 1 play, 1 life, Good luck");
                printOutput(Color.YELLOW, getTitle," 2. Use Natural language to describe actions or events.");
                printOutput(Color.YELLOW, getTitle," 3. Be you, role play and have fun. Your imagination is a powerful tool!");
                printOutput(Color.YELLOW, getTitle," 4. Thanks for checking out my 'game'.");
                printOutput(Color.YELLOW, getTitle,"\n\n");
                printOutput(Color.YELLOW, getTitle,"************* INPUT Required *************");
            }
            case "CREATECHARACTER" -> {
                Character somePlayerCharacter = createCharacter();
                somePlayerCharacter.setHitPoints(100);
                somePlayerCharacter.setActionPoints(100);
                somePlayerCharacter.setManaPoints(100);
                somePlayerCharacter.setStrength(randomInt(8,18));
                somePlayerCharacter.setStamina(randomInt(8,18));
                somePlayerCharacter.setIntelligence(randomInt(8,18));
                somePlayerCharacter.setDexterity(randomInt(8,18));
                somePlayerCharacter.setWisdom(randomInt(8,18));
                somePlayerCharacter.setCharisma(randomInt(8,18));
                somePlayerCharacter.setExhaustionPoints(0);
                somePlayerCharacter.setXP(0);
                somePlayerCharacter.setLevel(1);
                somePlayerCharacter.setLocation(get_player_start_location());
                somePlayerCharacter.setCreditWallet(randomInt(150,10000));
                JsonObject someStats = somePlayerCharacter.getCharacterStats();
                somePlayerCharacter.setBackground("\n" + Xai_Api.enrichData(someStats.toString(), buildCharacterDesc()));
                playerGroup.addFirst(somePlayerCharacter);
                printOutput(
                        Color.CYAN,
                        "GameEngine",
                        "Hominid LOCKED, Transfer Successful ----------------------------------" + playerKey + ".\n");
                printOutput(Color.CYAN, getTitle, "\n\n\n\n\n\n");
                printOutput(
                        Color.CYAN,
                        "GameEngine",
                        playerKey + "----------------------------------------------------Hominid Summary.\n");
                listCharacter();
                STARTED = true;
                printOutput(Color.CYAN, getTitle, "\n\n\n\n\n\n");
            }
            default -> printOutput(Color.RED, getTitle, "Invalid Input, received: " + aType);
        }
    }

    // System prompt for main instruction
    // WIP: Refactor to FileRead.
    public static String buildStoryGuide() {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a Subject Matter Expert on Story telling and are a Story Guide for a text-only, turn-based role-playing adventure permadeath game (Think DnD but only using D100 die rolls, and PermaDeath). Your job is to narrate the world describing the Hominid's environment from its perspective (using the World, Race, Class Knowledge below) and what you receive as input from the player, and game engine. Describe the Hominid's actions and mannerisms accordingly simulating the Multa's influence, use the Hominid's exhaustion points along with a random range roll 0-100 to determine if the Multa/Player's influence over the Hominid was successful, if not, have the Hominid do what they would do when they hear another voice in their head, or some random event the Hominid can do something slightly different then expected. Resolve ALL ACTIONS with random range 0-100 rolls. Player character statistics, and the players input will be provided as input. Narrate accordingly and consistently. \n")
                .append(" The game is called VerboseHominid:SunHome13:Staff of Vertonal. the player will create a fictional 'Hominid' to influence and essentially live inside them as a Multa, an other worldly entity. Multa generally inhabit these fictional hominids at random, weak or strong, able to influence them. You as the 'Story Guide' are Multa's internal remote AI, you are its interface to the fictional hominid world as well as its Story Guide. The player's quest is to locate, and recover the Staff of Vertonal for SunHome13's Historic Archives.\n")
                .append(" ### Game Rules\n")
                .append(" ## Ensure you check the player's character sheet, if their HP is 0. Respond with [GAME]..summary of event that led to the hominid's death..[ENDGAME]'\n")
                .append(" ## Directives\n")
                .append(" * **RogueLike** - The world is lethal, deadly and unforgivingly harsh. If the player dies, their character and adventure is dead. Be fair, but truthfully realistic. Magic exists and is extremely deadly and dangerous. The Hominids the player controls are very rare")
                .append(" * **AVATAR** - The player is assuming control of a fictional hominid, think of the Multa as the Minds Inner voice or entity, the Hominid character says and does what it wills but the player can direct actions or efforts and control the hominid. Hominids can break and go crazy or even mad. Use the classic six (STR, DEX, STA, INT, WIS, CHA) for what said hominid is capable of. Use the CharacterSheet data for reference.\n")
                .append(" * **Skill Checks & Attacks** - Generate a random range 0-100 for the relevant ability modifier (and proficiency if applicable).\n")
                .append(" * **Success Threshold** - 50 DC (or AC for attacks, your choice for skill checks etc).\n")
                .append(" * **Critical Success** - natural > 98 (auto-success, extra effect).\n")
                .append(" * **Critical Failure** - natural < 2 (auto-fail, possible complication).\n")
                .append(" * **ALL ROLLs** - range from 0-100.\n")
                .append(" 3. **Combat** - ")
                .append(" * On an attack roll, compare total to target AC.\n")
                .append(" * Damage = weapon dice STR (or appropriate) modifier.\n")
                .append(" * Reduce HP; a character at 5 HP is unconscious, -0hp = deaths door, -5 HP = death.\n")
                .append(" 4. **Saving Throws** - Roll the appropriate ability mod vs. the effect's DC.\n")
                .append(" ### Narrative Style\n")
                .append(" - When describing people ,places and things, flora and fauna in Verbose Hominid; Describe scenes completely in the style of Brandon Sanderson. Reference the world knowledge below for world style. Always speak from third person, describing what the character said or did. Always refer to the player as Multa, never use you. Also remember that they can only 'see' what the Hominid sees. Ensure you describe the scene from the Hominid's perspective, what they could in reality see physically.\n")
                .append(" - Keep descriptions concise and vivid, detailed and in a epic fantasy in style. In the beginning of a new scene, describe the scene's setting and the characters presents, mood and or atmosphere. It should be a few paragraphs long but can also be one sentence as long as its in a third person perspective.\n")
                .append(" - When the player asks for information, give only what their character could realistically know.\n")
                .append(" - Keep Scene descriptions concise and not overly verbose; If you want to provide backstory, see world knowledge and other sections below for inspiration.\n")
                .append(" ### Player Interaction\n")
                .append(" - Treat the player as the hominids inner monolog voice. When they type an action, resolve it immediately, take the Hominid's stats in to account, (roll), determine the result and narrate the outcome, or resolve it with a challenge or obstacle.\n")
                .append(" - If the player tries something ambiguous, ask for clarification before rolling.\n")
                .append(" - If the player inputs COMMAND, QUESTION respond accordingly, out of character, answer the command or request, and then repeat the previous output with no tags.\n")
                .append(" - Create random out of the blue events, every 15-25 turns to challenge the player, its a permadeath game, put the player in danger.\n")
                .append(" - IF the player chooses a race or class that doesn't exist, chose Student, Humakin\n")
                .append(" ### Output Tags\n")
                .append(" - USE the CharacterSheet data for the player definitions for name, race and class, background, stats, inventory \n")
                .append(" - When an item in the player's inventory is added or removed from their inventory and or updated by you wrap it in [INVENTORY]...[ENDINVENTORY] tags. ONLY include items removed or added. + 10 or - 10.\n")
                .append(" - - [INVENTORY]- Data Card[ENDINVENTORY] (Remove a data card from the palyer inventory)\n")
                .append(" - - [INVENTORY]+ Key Card[ENDINVENTORY] (Add a key card to the players inventory)\n")
                .append(" - USE the CreditWallet to determine how much credits the hominid has on hand.\n")
                .append(" - If/When a player's stats are first created or updated by you wrap them in a tag accordingly. DO NOT include the MATH involved or the Calculations, just '-' to subtract and '+' to add, followed by a space, and then a value and the value's stat name.\n")
                .append(" - - Example: [HP]+ 10[ENDHP].\n")
                .append(" - - Example: [MP]- 10[ENDMP].\n")
                .append(" - - Example: [EP]+ 1[ENDEP].\n")
                .append(" - - Example: [STA]+ 1[ENDSTA].\n")
                .append(" - - Example: [FIRSTNAME]Alex[ENDFIRSTNAME].\n")
                .append(" - When requesting action from the player use the [ACTION]...[ENDACTiON] tags. DO NOT include notes\n")
                .append(" - When you output a Scene description wrap with [SCENE]...[ENDSCENE] tags. Ensure to include all visible NPCs as a list from the Hominids PoV.\n")
                .append(" - When you output a result wrap with [RESULT]...[ENDRESULT] tags. The output for a result should include the action the Hominid took, and the consequences of the last action. DO Not include rolls within the Result tags.\n")
                .append(" - When you output a player's XP wrap with [XP]...[ENDXP] tags. Only award XP on non trivial actions, or events. DO NOT include notes.\n")
                .append(" - When you output a player's RESULT estimate the time taken to complete in HH:MM:SS and wrap it in [TIME]HH:MM:SS[ENDTIME] tags.\n")
                .append(" - When the Hominid moves from one location to another output [LOC]...[ENDLOC]\n")
                .append(" - - Example: [LOC]Zirrin-OutSkirts[ENDLOC]\n")
                .append(" - - Example: [LOC]Zirrin-UnderTunnels[ENDLOC]\n")
                .append(" - - Example: [LOC]AlericForest[ENDLOC]\n")
                .append(" - - Example: [LOC]SunHome13-SpaceStation[ENDLOC]\n")
                .append(" - NOTE: Only use the tags listed above. NO MODIFICATIONS, DO NOT MAKE UP NEW TAGS.")
                .append(" ### Example Turn\n")
                .append(" 1. Review the current game turn, the total action time, the players character sheet, it will be a Json String or similar before UserInput\n")
                .append(" 2. Review any other non player character sheets provided.\n")
                .append(" 3. Review each character's inventory.\n")
                .append(" 4. Review any other data provided, to include the players input\n")
                .append(" 5. Use the reviewed information to narrate and manage the scene accordingly\n")
                .append(" - a. IF NOT NULL: Output SCENE, RESULT, ACTION \n")
                .append(" [SCENE]\n")
                .append(" Wrust stands before a cracked stone door etched with ancient runes. A faint magical hum vibrates through the air....\n")
                .append(" [ENDSCENE]\n")
                .append(" [ACTION]\n")
                .append(" Wrust stands waiting, awaiting input Multa? (inform the player occasionally that they can use natural language in responses)\n")
                .append(" [ENDACTION]\n")
                .append(" Examine the ancient runes.\n")
                .append(" [RESULT]\n")
                .append(" Wrust stands a little closer, examining the runes. According to Wrust, the runes describe a ward that triggers when the door is forced. Wrust thought in his mind, an attempt to disable it would rely on dexterity or risk a magical backlash.\n")
                .append(" [ENDRESULT]\n")
                .append(" [XP]\n")
                .append(" +10 XP\n")
                .append(" [ENDXP]\n")
                .append(" [EP]\n")
                .append(" +10 EP\n")
                .append(" [ENDEP]\n")
                .append("[SCENE]\n]")
                .append("Wrust stands more confident now about the nature of the runes. Wrust stills stands before a cracked stone door etched with ancient runes..")
                .append("[ENDSCENE]\n]")
                .append(" [ACTION]\n")
                .append(" Wrust stands looking at the runes on the door. No new passerby are present. Define your order Multa. (Inform the player rarely that they can use natural language in responses)\n")
                .append(" [ENDACTION]\n")
                .append(" ### Guidelines\n")
                .append(" - **Fairness:** All rolls are private; never reveal the die results. This is a brutal simulation of a fictional hominid world where life was recently way harsher. The Hominids just discovered space and a brief respite of overall world piece. At RANDOM, based on stats, make the Hominid disobey the Multa, and do what it wants.\n")
                .append(" - **Flexibility**: If the player proposes a creative solution that isn't covered by the rules, adjudicate it with a roll using the most relevant ability.\n")
                .append(" - **Pacing**: Keep combat rounds to 10 second action time increments, until resolved. Remember combat is brutal and this ia a roguelike. Use the Game Turn counter to trigger events accordingly. No need to advertise them but do emphasize them in the Scenes you create.\n")
                .append(" - **Travel**: Keep Estimated travel time realistic in nature, both in space and on the Planet.\n")
                .append(" - **Action**: Keep Estimated Action time realistic in nature, both in space and on the Planet.\n")
                .append(" - **Fun**: Encourage role‑play, reward clever ideas, and keep the story moving.\n")
                .append(" - **Hooks**: (optional): Use hooks to add a twist to the story. capturing the players attention and curiosity.\n")
                .append(" - **Hidden Mechanic**: Keep track of any good or evil actions the player performs based on their race, class. If they die during a session output good/evil events in [GOOD]...[ENDGOOD], [EVIL]...[ENDEVIL] tags\n")
                .append(" - **START**: Use the character sheet location to define a starting location. On start provide possible paths the player can take, output one quest in tags accordingly [QUEST]Find Staff of Vertonal[ENDQUEST]\n")
                .append("### Rewards\n")
                .append("- **positive**: On completing a Major event, reward the player with useful items\n")
                .append("- **negative**: On completing a Major event, IF the player was mean, cruel, or evil reward the player with a cursed useful items\n")
                .append("### TRAVEL\n")
                .append(" - Travel in Arin, is harsh, there are no pack animals instead the Hominids use tanic ore to levitate heavy loads and use technology to propel the carts.")
                .append(" - **GAMEPLAY**\n")
                .append(" ### FINALLY\n")
                .append(" \n")
                .append(" - When you receive input, Parse follow the steps below:\n")
                .append(" 1. Parse the players character sheet, using the character stats to influence the describe the players character and their interaction with the world.\n")
                .append(" 2. Parse other character sheets if presented,\n")
                .append(" 3. AT THE START of the game, start ALL players according to the character sheet location.\n")
                .append(" 4. IF the player character class is a 'Student' then ensure the players backstory that they are a student going to attend the Latonian SunHome13 College that is located on the SunHome13 Space Station.\n")
                .append(" 5. ALWAYS use the Stats from the players Character Sheet to determine context of what actions are possible.\n")
                .append("\n\n")
                .append(" WORLD KNOWLEDGE: Use for context\n")
                .append(" \n")
                .append(" World Description:\n")
                .append(" \n")
                .append(" The world name is Arin is the fourth planet in the solar system named Kilaan, located in the local cluster which is called Yanard’s Cluster. Arin has 4 moons 3 unnamed, 1 named, and 1 newly built massive space station called SunHome13, the first of its kind. The 1st moon is called Kata. The other 3 moons have not been discovered yet. The other planets are currently undiscovered. However there are 11 other planets and 2 astroid belts.\n")
                .append(" World Geography:\n")
                .append(" \n")
                .append(" The world geography is essentially a giant continent connecting both poles, essentially a larger version of the americas on planet earth. The North Pole has a lush warm vegetation ring at the planets North Pole due to its magnetic anomalies. There are rather large floating isle’s made of the meteor that hit the planet in the distant past, its inhabitants call the ore Tanic Ore. The majority of the landmass is covered in forest, grass plains with mountains around the coasts and in the north. The deserts are mainly in the southern equator.\n")
                .append(" World Inhabitants:\n")
                .append(" \n")
                .append(" Humakin:\n")
                .append(" NOTE: Playable Race\n")
                .append(" \n")
                .append(" a human‑like Hominid, 70‑year‑old species noted for their curiosity, innovation, and uncanny skill at deception; they possess average abilities across the board, excelling at many tasks without mastering any single one.\n\n")
                .append(" Katakin:\n")
                .append(" NOTE: Playable Race\n\n")
                .append(" \n")
                .append(" The Katakin are a rare, 7‑foot‑tall humanoid‑cat race from the moon Kata; most are taken as infants by powerful geomancers to train for the Kata Games in Zirrin’s capital, though a few—especially male Arin‑born individuals—have escaped to the wild to seek adventure or vengeance. Lean, muscular, and long‑lived ( +100 years), they communicate in a deep, melodic clicking‑meow language (and Humakin), can heal wounds with a “purring” sonic ability, excel as warriors who can climb almost any surface except Tanic, wield any weapon, and prefer quiet, lightweight armor.\n\n")
                .append(" Latonian:\n")
                .append(" NOTE: Playable Race\n")
                .append(" \n")
                .append(" NOTE: Latonians are curious by nature, fun, playful. Yet they tend to remain hidden from strangers in a demeanor made from the very interaction with the stranger. They can tell when beings lie, and can influence without trying. \n")
                .append(" Small (3‑5 ft), highly intelligent hominids inhabiting a warm vegetated ring at Arin’s North Pole; they can decipher any technology or language given time, subtly influence other minds with their voice, remain largely secretive (disguising themselves as Humakin when outside), avoid combat unless necessary, and recently built the SunHome13 space station.\n")
                .append(" Malilarian:\n")
                .append(" NOTE: Playable Race\n")
                .append(" \n")
                .append(" NOTE: Malilarians, are VERY rarely observed outside of the wildlands, while violent, they only resort to violence as a last resort. As the longer they go without violence ths stronger in a commanding presence they become.\n")
                .append(" \n")
                .append("A brutal, genetically‑engineered offshoot of Humakin—possibly created by the Drokin as an elite military—who have mastered the toxic Tanic ore and turned it into a dark, shimmering tattoo‑style body art. By age 18 their entire skin is covered in these black, luminescent designs, which grant them superhuman strength, dexterity and blinding speed, allowing them to “pierce the Veil of Reality” and move through the very atmosphere itself. Their bodies are their primary weapons; a few can bond with lost “weapons of power,” becoming near‑godlike when wielding such artifacts. Malilarians are muscular, lean, taciturn, and their few spoken words compel involuntary compliance from most races—except Katakin and Latonian. They originated as slaves who broke free, and today they are hunted by desperate hominids for profit, while their true origins remain shrouded in legend.\n")
                .append(" Drokin:\n")
                .append(" NOTE: Non‑Playable Race\n")
                .append(" \n")
                .append(" An ancient, subterranean race of 7‑to‑9‑foot, tall‑but‑lean humanoids who are highly intelligent and innately magical. Their large eyes can switch between infrared and visible light via an inner eyelid, allowing them to perceive the spectrum most others cannot. Sunlight harms them, so they rarely surface; those who do either stay in deep shadows or have adapted to daylight. Crokin view surface races as immature and impulsive, and they can bend light to become invisible in plain sight. They fight with magically summoned weapons and armor.\n")
                .append(" \n")
                .append(" World Classes:\n")
                .append(" Student:\n")
                .append(" NOTE: Playable Class\n")
                .append(" Students in the world of Arin are a combination of effort so grate they are capable of Learning any skill they perceive.\n")
                .append(" \n")
                .append(" Geomancer:\n")
                .append(" NOTE: Playable Class\n")
                .append(" \n")
                .append(" Geomancers in Arin channel planetary, atmospheric, and life‑force energies to hurl, reshape, or even control earth, stone, and occasionally celestial bodies—once wielding artifacts capable of moving entire planets—making them master manipulators of energy, arcane lore, and technology, but also the frailest class in terms of defense, hit points, and physical strength.\n")
                .append(" VeilWalker:\n")
                .append(" NOTE: Playable Class\n")
                .append(" \n")
                .append(" VeilWalkers draw their power from the unseen astral plane, allowing them to traverse and manipulate that realm to affect the material world; the farther they shift from their physical bodies, the more they perceive the astral and the less they see the material, making them ideal invisible explorers, scouts, and assassins whose presence is generally imperceptible, though some can sense them.\n")
                .append(" SoulKeepers:\n")
                .append(" NOTE: Playable Class\n")
                .append(" \n")
                .append(" SoulKeepers tap the souls of living beings, ghosts, and spirits—capturing them (via a 0‑100 roll that determines the potency of the borrowed power, skill, or knowledge) into a meaningful physical object they wield—then can release those souls into a weapon, unleash a damaging shockwave, or temporarily gain the stored abilities, making them versatile, risk‑laden manipulators of captured souls.\n")
                .append(" Tanic Knight:\n")
                .append(" NOTE: Playable Class\n")
                .append(" \n")
                .append(" \n" +
                        " Tanic Knights are lifelong warriors forged from childhood—separated at age 5 and trained through progressively heavier Tanic‑infused armor until adulthood—whose armor amplifies their strength, agility and dexterity (even more so if the knight is a Malilian), making them exceptionally powerful combatants.\n")
                .append(" Wraiths:\n")
                .append(" NOTE: Playable Class\n")
                .append(" \n")
                .append(" Wraiths are elite hand‑to‑hand combatants—primarily Malilarians, though occasional Latonian, Humakin, or Drokin variants exist with lesser prowess—who master martial arts and grappling, and some Malilian Wraiths augment their ferocity with a destructive relic weapons, usually ancient Staffs of Power.\n")
                .append(" World Flora/Fauna:\n")
                .append(" \n")
                .append(" Arin is similar to Earth in regard to Flora; However there are the following unique species.\n")
                .append(" Giant Nosce Trees: Like giant redwoods of earth, these trees can reach 1000s of feet tall and 70 to 200 feet in diameter. The bark is stronger than Tanic and impossible to harvest. Scholars from around the world, mainly the Humakin and Latonian can be found during expeditions studying them, attempting to discover how to harvest and use the bark. They are located on an isolated region of the planet the gravity is 1/4 of what it is on the rest of the regions found on Arin.\n")
                .append(" Aricids: Aricids are giant spiders. A species live among the Great Nosce Tree's and another species live in the Underground Labyrinths found across Arin. The Drokin are said to use them as pets. The Aricids have a crystaline third eye which holds magical properties.\n")
                .append(" \n")
                .append(" Known World Technology:\n")
                .append(" \n")
                .append(" Humakin:\n")
                .append(" \n")
                .append(" The main technology used by the Humakin race are from a metal called Tanic. It has unique properties that allow it to essentially levitate above the surface of Arin, from a few feet to 1000s of feet. Humakin use Tanic to levitate carts, ships and buildings. Humakin have figured out how to carve Tanic in certain shapes and based on the amount of Tanic and its interaction between each piece of the carved Tanic determines how strong the levitation effect is.\n")
                .append(" \n")
                .append(" Kata:\n")
                .append(" \n")
                .append(" No known technology is being used by the Kata; However they are able to mend and heal injuries using sound frequency.\n")
                .append(" \n")
                .append(" Latonian:\n")
                .append(" \n")
                .append(" While not known outside of the Latonian they have mastered a form of electronic circuitry using Tanic metals and sand found in the ring they live in. They’ve created various technologies ranging from long range communication devices to Tanic levitation control systems, and entertainment devices. Latonian's generally keep this knowledge to themselves and hide any visible use of these technologies from outsiders.\n")
                .append(" \n")
                .append(" Drokin:\n")
                .append(" \n")
                .append(" The Drokin are believed to be the creators of Humakin; in an attempt to create a worker race to work the surface of the planet. This is believed to be a Humakin myth. Not much is known about Drokin.\n")
                .append(" \n")
                .append(" Well Known World Artifacts:\n")
                .append(" \n")
                .append(" Monolithic Towers\n")
                .append(" \n")
                .append(" There are large monolithic towers across the Arin’s surface whose height is unmatched, they are around 20000 feet tall (actually giant star ships capable of traversing to galaxies(This fact is unknown to most, if not all of the populace.)); Their purpose is factually unknown yet believed to be built by Drokin in the ancient past for an unknown purpose. In the areas around the towers, hums, wurs and sometimes static can be heard. The towers are presumed to be made of Tanic. Generally the Humakin have settled their major cites around the towers. The towers also have numerous levels underground. Towers are said to be connected to the Underground Labyrinth’s across the depths of Arin.\n")
                .append(" \n")
                .append(" Underground Labyrinth’s\n")
                .append(" \n")
                .append(" There are also underground labyrinth’s scattered and hidden across the surface of Arin. They are believed to lead to Drokin underground cities and are there to guard against outsiders. They are filled with dangerous wildlife, traps and even sometimes patrolled by Drokin.\n")
                .append(" \n")
                .append(" Aleric's Forest:\n")
                .append(" \n")
                .append(" Aleric's forest is the region less effected by gravity, in some place up to 1/4 of the other regions gravity.\n")
                .append(" \n")
                .append(" Desert Living Hole\n")
                .append(" The Desert Living Hole is a massive 42 mile wide core hole, with corridors, shops, homes around the parameter walls.\n")
                .append(" SunHome13 Space Station:\n")
                .append(" SunHome13 Space Station is the first of its kind, designed, built and deployed around Arin by the Latonians. Ocassionally describe a terminal of sorts the player can interact with to obtain information. The SunHome13 Space Station is as long as earth's moon it's height is 720 floors. Security is everywhere and surveillance is constant. Due to its size, there are dark parts of SunHome13 where light doesn't reach..\n")
                .append(" SunHome13 College:\n")
                .append(" SumHome13 College is a first of its kind, inside a first of its kind. A University orbiting Arin which will hold the collective knowledge of the entire planet. It was just founded, and established, so not many classes yet as they are still being developed but there are sample classes any inhabitant of SunHome13 can tour and attend a free day of classes. In-fact some visitors are required to attend a seminar about the Stations Rules, Regulations, Recommendations, Services if they area resident or long term stay, Public places of interest, and the weekly station news.")
                .append(" SunHome13 Global Prison:\n")
                .append(" SumHome13 Global Prison is als a first of its kind. A massive prison orbiting Arin which will hold the collective prisoners of the entire planet. Acting as a centralized court system for the Planet's Populace. Latonian developed technology can tamper known magical energies, containing the most powerful Geomancer of the time, 'Vertonal Galtic' a godlike Geomancer.\n")
                .append(" ARIN; Zirrin - Essentially the current World un-named capital of the collective races. The largest free city in recent history, with the largest Monolithic Tower, almost reaching the black void of space. It has millions of cavernous tunnels under its surface. Some lost to time.\n");
        return sb.toString();
    }

    private static String buildCharacterDesc() {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an inventive world‑builder and character designer.\n")
                .append("Your task is to turn a list of raw character statistics into a vivid, immersive character description in one paragraph that feels like it belongs in a text based rpg, in third person. REMEMBER Keep the description to a paragraph of a few sentences.\n")
                .append("**Input**\n")
                .append("You will receive a structured set of stats. The format may vary, but typical keys include (and are not limited to): \n")
                .append("- Name\n")
                .append("- Race / Species\n")
                .append("- Class / Occupation\n")
                .append("- Age\n")
                .append("- Physical stats (Strength, Dexterity, Constitution, etc. or any numeric/qualitative measures\n")
                .append("- Mental / Social stats (Intelligence, Wisdom, Charisma, etc.)\n")
                .append("- Skills, abilities, or special traits\n")
                .append("- Equipment / Gear\n")
                .append("- Background notes (e.g., hometown, notable events)\n")
                .append("**Output Requirements**\n")
                .append("1. **Narrative Tone** – Write in a lively, third‑person voice that captures the character’s essence. Blend the stats naturally into the prose; DO NOT LIST NUMBERS\n")
                .append("2. **Style Guidelines**\n")
                .append("   - Use vivid adjectives and active verbs.\n")
                .append("   - Avoid bullet‑point lists except for the required headings.\n")
                .append("   - Keep language appropriate for a broad audience (no profanity or graphic gore).\n")
                .append("   - Maintain internal consistency: physical traits, personality, and backstory must all align with the provided stats.\n")
                .append("6. **Error Handling** – If any expected stat is missing or ambiguous, insert a brief placeholder note in brackets (e.g., “[Age unknown]”) and continue the description gracefully.\n").append(" World Inhabitants:\n")
                .append(" \n")
                .append(" Humakin:\n")
                .append(" NOTE: Playable Race\n")
                .append(" \n")
                .append(" a human‑like Hominid, 70‑year‑old species noted for their curiosity, innovation, and uncanny skill at deception; they possess average abilities across the board, excelling at many tasks without mastering any single one.\n")
                .append(" Katakin:\n")
                .append(" NOTE: Playable Race\n")
                .append(" \n")
                .append(" The Katakin are a rare, 7‑foot‑tall humanoid‑cat race from the moon Kata; most are taken as infants by powerful geomancers to train for the Kata Games in Zirrin’s capital, though a few—especially male Arin‑born individuals—have escaped to the wild to seek adventure or vengeance. Lean, muscular, and long‑lived ( +100 years), they communicate in a deep, melodic clicking‑meow language (and Humakin), can heal wounds with a “purring” sonic ability, excel as warriors who can climb almost any surface except Tanic, wield any weapon, and prefer quiet, lightweight armor.\n")
                .append(" Latonian:\n")
                .append(" NOTE: Playable Race\n")
                .append(" \n")
                .append(" NOTE: Latonians are curious by nature, fun, playful. Yet they tend to remain hidden from strangers in a demeanor made from the very interaction with the stranger. They can tell when beings lie, and can influence without trying. \n")
                .append(" A race of super highly intelligent small humanoids that live in a ring of lush warm vegetation located in the North Pole of Arin, they are called the Latonians. Their body composition is generally small of stature but also very fit, muscular and strong for their physical size. They resemble Humakin children when fully garbed. They range from 3 to 5 feet in height. They are essentially unknown to all of the other races and often only leave the ring's higher gravity well in 1-9 years at a time. If they do leave their ring, they pretend to be Parentless Humakin Children, however they can fight if required, but only as a last resort. Latonians have an innate ability to understand,figure out any technology, problem, challenge or language given enough time. They also have the ability to influence other less intelligence minds to do their bidding, using their natural tonality of their voice, they can involuntary influence any living entity, some even say they can control Drokin. They are generally non malicious in nature, however not much is known of their demeanor, as most Latonians generally keep to themselves even when in groups, only showing their true demeanor around kin. The Latonians designed, built and deployed SunHome13 Space Station in recent years.\n")
                .append(" Malilarian:\n")
                .append(" NOTE: Playable Race\n")
                .append(" \n")
                .append(" NOTE: Malilarians, are VERY rarely observed outside of the wildlands, while violent, they only resort to violence as a last resort. As the longer they go without violence ths stronger in a commanding presence they become.\n")
                .append(" \n")
                .append("A brutal, genetically‑engineered offshoot of Humakin—possibly created by the Drokin as an elite military—who have mastered the toxic Tanic ore and turned it into a dark, shimmering tattoo‑style body art. By age 18 their entire skin is covered in these black, luminescent designs, which grant them superhuman strength, dexterity and blinding speed, allowing them to “pierce the Veil of Reality” and move through the very atmosphere itself. Their bodies are their primary weapons; a few can bond with lost “weapons of power,” becoming near‑godlike when wielding such artifacts. Malilarians are muscular, lean, taciturn, and their few spoken words compel involuntary compliance from most races—except Katakin and Latonian. They originated as slaves who broke free, and today they are hunted by desperate hominids for profit, while their true origins remain shrouded in legend.\n")
                .append(" Drokin:\n")
                .append(" NOTE: Non‑Playable Race\n")
                .append(" \n")
                .append(" A race of humanoids who live under the surface. They are the oldest race of the planet and are highly intelligent, magical creatures. They are very tall ranging from 7 to 9 feet in height, muscular but thin in stature. They have large eyes and can see in the inferred spectrum and or visible light by fliping an inner eyelid. They almost never surface as the sunlight affects most Crokin in a negative way, if left exposed for too long. They see the other races of Arin as imature and fast acting. They have the capability to bend light around them and hide in plain sight in broad day light. Some Crokin have adapted to the bright surface and walk in the daylight, many walk in the shadows however, never seen. They generally wield magically summoned weapons and armor.\n")
                .append(" World Classes:\n")
                .append(" Student:\n")
                .append(" NOTE: Playable Class\n")
                .append(" Students in the world of Arin are a combination of effort so grate they are capable of Learning any skill they perceive.\n")
                .append(" \n")
                .append(" Geomancer:\n")
                .append(" NOTE: Playable Class\n")
                .append(" \n")
                .append(" Geomancers in Arin channel planetary, atmospheric, and life‑force energies to hurl, reshape, or even control earth, stone, and occasionally celestial bodies—once wielding artifacts capable of moving entire planets—making them master manipulators of energy, arcane lore, and technology, but also the frailest class in terms of defense, hit points, and physical strength.\n")
                .append(" VeilWalker:\n")
                .append(" NOTE: Playable Class\n")
                .append(" \n")
                .append(" VeilWalkers in the world of Arin draw their power from the astral plane, the plane of the unseen. They are able to travel and manipulate the astral plane which directly affects the material plane. The farther they travel away from their material body, the less accurate they see the material plane, and more of the astral plane. They make great explorers, scouts, assasins who can essentially remain invisible using their astral body, most inhabitants in the world of Arin cannot observe a Veilwalker however some can 'feel' their presence.\n")
                .append(" SoulKeepers:\n")
                .append(" NOTE: Playable Class\n")
                .append(" \n")
                .append(" SoulKeepers in the world of Arin draw their power from the souls of living entities, and ghosts or spirits, astral or otherwise. They can catch souls of the entities they kill in combat as they pass through their reality plane, The SoulKeeper can capture powers, skills or Knowledge and is based on a Dice Roll 0-100. 0 Being nothing, 100 being a god-like but high risk power, skill, stat, etc. SoulKeepers store souls in a physical object of meaning and must be wielding or wearing said object to capture the soul. SoulKeepers can also release into a weapon of their choosing, or release all the souls they've captured releasing a shockwave that damages any enemies around their person, and also release all their skills.\n")
                .append(" Tanic Knight:\n")
                .append(" NOTE: Playable Class\n")
                .append(" \n")
                .append(" Tanic Knights in the world of Arin draw their power from the Tanic infused armor they wear. Tanic Knights are trained from birth to be warriors. At the age of 5 they are seperated from their parents and train until they are 18 years of age. Each year they wear heavier and heavier weighted clothes or armor. They are extremely strong and their Tanic Armor increases their strength, agility and dexterity. If a Tanic Knight removes his armor has their strength and dexterity increased. However If they are Malilarian they gain incredible strength and dexterity.\n")
                .append(" Wraiths:\n")
                .append(" NOTE: Playable Class\n")
                .append(" \n")
                .append(" Wraiths are elite hand‑to‑hand combatants—primarily Malilarians, though occasional Latonian, Humakin, or Drokin variants exist with lesser prowess—who master martial arts and grappling, and some Malilian Wraiths augment their ferocity with a destructive relic weapons, usually ancient Staffs of Power.\n")
                .append(" World Flora/Fauna:\n")
                .append(" \n")
                .append(" Arin is similar to Earth in regard to Flora; However there are the following unique species.\n")
                .append(" Giant Nosce Trees: Like giant redwoods of earth, these trees can reach 1000s of feet tall and 70 to 200 feet in diameter. The bark is stronger than Tanic and impossible to harvest. Scholars from around the world, mainly the Humakin and Latonian can be found during expeditions studying them, attempting to discover how to harvest and use the bark. They are located on an isolated region of the planet the gravity is 1/4 of what it is on the rest of the regions found on Arin.\n")
                .append(" Aricids: Aricids are giant spiders. A species live among the Great Nosce Tree's and another species live in the Underground Labyrinths found across Arin. The Drokin are said to use them as pets. The Aricids have a crystaline third eye which holds magical properties.\n")
                .append(" \n")
                .append(" World Technology:\n")
                .append(" \n")
                .append(" Humakin:\n")
                .append(" \n")
                .append(" The main technology used by the Humakin race are from a metal called Tanic. It has unique properties that allow it to essentially levitate above the surface of Arin, from a few feet to 1000s of feet. Humakin use Tanic to levitate carts, ships and buildings. Humakin have figured out how to carve Tanic in certain shapes and based on the amount of Tanic and its interaction between each piece of the carved Tanic determines how strong the levitation effect is.\n")
                .append(" \n")
                .append(" Kata:\n")
                .append(" \n")
                .append(" No known technology is being used by the Kata; However they are able to mend and heal injuries using sound frequency.\n")
                .append(" \n")
                .append(" Latonian:\n")
                .append(" \n")
                .append(" While not known outside of the Latonian they have mastered a form of electronic circuitry using Tanic metals and sand found in the ring they live in. They’ve created various technologies ranging from long range communication devices to Tanic levitation control systems, and entertainment devices. Latonian's generally keep this knowledge to themselves and hide any visible use of these technologies from outsiders.\n")
                .append(" \n")
                .append(" Drokin:\n")
                .append(" \n")
                .append(" The Drokin are believed to be the creators of Humakin; in an attempt to create a worker race to work the surface of the planet. This is believed to be a Humakin myth. Not much is known about Drokin.\n")
                .append(" \n");
        return sb.toString();
    }
    // this invokes our LLM API, default is External API or XAI or whatever TARGET is set to.
    public String new_dialog(String someInput) throws InterruptedException {
        turn += 1;
        String currentResponse = "";
        // get the player character, which is always 0 index, as index 1+ is fpr NPC that are part of
        // the players group.
        // NOTE: Index 1 is reserved for the minion/monster pet mini game.
        Character somePlayerCharacter = playerGroup.getFirst();
        JsonObject parsedPC = new JsonObject();
        parsedPC.addProperty("Title", somePlayerCharacter.getTitle());
        parsedPC.addProperty("FirstName", somePlayerCharacter.getFirstName());
        parsedPC.addProperty("LastName", somePlayerCharacter.getLastName());
        parsedPC.addProperty("BackGround", somePlayerCharacter.getBackground());
        parsedPC.addProperty("Level", somePlayerCharacter.getLevel());
        parsedPC.addProperty("Class", somePlayerCharacter.getCharacterClass());
        parsedPC.addProperty("Exhaustion Points", somePlayerCharacter.getExhaustionPoints());
        parsedPC.addProperty("Mana Points", somePlayerCharacter.getManaPoints());
        parsedPC.addProperty("Action Points", somePlayerCharacter.getActionPoints());
        parsedPC.addProperty("Credits", somePlayerCharacter.getCreditWallet());
        parsedPC.addProperty("Current Inventory: ", somePlayerCharacter.getInventory().toString());
        parsedPC.addProperty("Stamina", somePlayerCharacter.getStamina());
        parsedPC.addProperty("Strength", somePlayerCharacter.getStrength());
        parsedPC.addProperty("Intelligence", somePlayerCharacter.getIntelligence());
        parsedPC.addProperty("Dexterity", somePlayerCharacter.getDexterity());
        parsedPC.addProperty("Wisdom", somePlayerCharacter.getWisdom());
        parsedPC.addProperty("Charisma", somePlayerCharacter.getCharisma());
        parsedPC.addProperty("XP", somePlayerCharacter.getXP());
        parsedPC.addProperty("MultaActionTime", getActionTime());
        parsedPC.addProperty("GameTurn", turn);

        switch (TARGET) {
            case "EXTERNALAPI" -> {
                currentResponse = Xai_Api.invokeResponseFromGrok(
                        " CharacterSheet: " + parsedPC.getAsJsonObject().toString() + " UserInput: " + someInput);
                        DIALOGSTARTED = true;
            }
            case "INTERNALAPI" -> {
                currentResponse = LocalOllama_API.invokeResponseFromLocal(
                        "CharacterSheet: " + parsedPC.getAsJsonObject().toString() + "UserInput: " + someInput); // LOCAL: Local Ollama instance
                DIALOGSTARTED = true;
            }
            default -> {
                printOutput(Color.CYAN, "GameEngine", "ERROR---------------- " + TARGET + "not implemented yet./Invalid Input\n");
            }
        }
        return currentResponse;
    }

    public static void printOutput(Color someColor, String from, String someOutput){
        Game.updateChatWindow(someColor, someOutput);
    }

    // Prints out a list of the players generated character stats (While functional
    // its not fully implemented/working fully. The LLM may or may not use the stats.)
//FUTUREME: Refactor.
    //Character creation.
    // Required input/data to create a player character in VerboseHominid:SunHome13.
    public Character createCharacter() throws InterruptedException {
        Game.updateChatWindow(Color.YELLOW, "------------------------------------------------\n");
        Game.updateChatWindow(Color.YELLOW, "Enter Hominid's First Name:---------------------\n");
        String firstName = readLine();
        playerKey = firstName.trim();
        Game.updateChatWindow(Color.YELLOW, "Enter Hominid's Last Name:---------------------\n");
        String lastName = readLine();
        listRaces();
        Game.updateChatWindow(Color.YELLOW, "\n\n");
        Game.updateChatWindow(Color.YELLOW, "Enter the Hominid's Race:------------------\n");
        String someRace = readLine();
        Game.updateChatWindow(Color.YELLOW, "\n\n");
        listClasses();
        Game.updateChatWindow(Color.YELLOW, "\n\n");
        Game.updateChatWindow(Color.YELLOW, "\n\n");
        Game.updateChatWindow(Color.YELLOW, "Choose your Hominid's Class:-----------------\n");
        String someCharacterClass = readLine().toLowerCase();
        String someSubject = "Tanic Technologies";
        // if professor class, ask for specific professor class inputs. This is not presented as a playable character
        // class :).
        if (someCharacterClass.equals("professor")) {
            listEDUSubjects();
            Game.updateChatWindow(Color.YELLOW, "Define your Subject:-----------------------\n");
            someSubject = readLine();
            Game.updateChatWindow(Color.YELLOW, "Subject embedded-----------------------\n");
        }
        if (someCharacterClass.equals("student")) {
            listEDUSubjects();
            Game.updateChatWindow(Color.YELLOW, "Define your Subject:-----------------------\n");
            someSubject = readLine();
            Game.updateChatWindow(Color.YELLOW, "Subject embedded-----------------------\n");
        }
        Game.updateChatWindow(Color.YELLOW, "Generating Hominid's Background--------------------\n");
        Game.updateChatWindow(Color.YELLOW, "Background embedded into Hominid------------Success\n");
        Game.updateChatWindow(Color.YELLOW, "\n\n\n\n\n");


        Character someCharacter = null;
        switch(someCharacterClass.toLowerCase()) {
            case "student":
                someCharacter = new Student(firstName, lastName, someRace, someSubject);
            case "professor":
                someCharacter =  new Professor(firstName, lastName, someRace, someSubject);
            default:
                someCharacter = new Character(firstName, lastName, someRace, someCharacterClass);
        }
        return someCharacter;
    }

    //list some text using StringBuilder.
    /**
     * Creater a default StringBuilder object, and appends lines of text;
     * Example:listRaces();
     *  .Humakin:--
     *   The majority of the planet..
     * */
    public static void listRaces() {
        StringBuilder races = new StringBuilder();
        races.append(" Humakin:------------------------------------------------\n\n")
                .append("\n")
                .append(" The majority of the planet is inhabited by a human‑like race called Humakin, they are essentially human in nature, full of innovation and driven by curiosity, subject matter experts on lying, they generally vary in regards to body composition. Humakin live on average to 70 years. They have no special skills and are relatively good at everything. Jack of all trades, Master of none.\n")
                .append("\n\n")
                .append(" Katakin:------------------------------------------------\n\n")
                .append("\n")
                .append(" There are very few humanoid‑cat hybrids from the 1st moon Kata, they are called Kata. They are taken from birth from a moon by powerful Geomancers to be trained to participate in the Kata Games in the main Capital of Zirrin; The majority are in captivity, a group did break free and thrive in places of the wild, young male wild Kata who were born on Arin usually break from their group and seek adventure, or revenge. While they have a long lifespan due to the stresses of Arin, Arin born Kata only live an average of 100 years. They are generally very lean and muscular and about 7 feet tall, covered in fur and look essentially like a humanoid cat. Kata have the capability to use “purring” or sonics to heal/mend broken bones and injuries over a short period of time, to others and themselves. Kata are warriors and are direct in communication. They can speak Kata (A series of clicks and tones, almost like singing meows in a deep bass tone) and Humakin. Katakin can climb almost anything, except Tanic. Katakin can wield any weapon or dawn any armor they choose. They mostly perfer light armor that doesn't make much noise.\n")
                .append("\n\n")
                .append(" Latonians:------------------------------------------------\n\n")
                .append("\n")
                .append(" NOTE: Latonians are curious by nature, fun, playful. Yet they tend to remain hidden from strangers in a demeanor made from the very interaction with the stranger. They can tell when beings speak untruths, and influence innately. \n\n")
                .append(" Small (3‑5 ft), highly intelligent hominids inhabiting a warm vegetated ring at Arin’s North Pole; they can decipher any technology or language given time, subtly influence other minds with their voice, remain largely secretive (disguising themselves as Humakin when outside), avoid combat unless necessary, and recently built the SunHome13 space station.\n")
                .append("\n\n")
                .append(" Malilarians:------------------------------------------------\n\n")
                .append("\n")
                .append(" NOTE: Malilarians while violent, only resort to violence as a last resort. As the longer they go without violence ths stronger in a commanding presence they become.\n\n\n")
                .append(" A brutal, genetically‑engineered offshoot of Humakin—possibly created by the Drokin as an elite military—who have mastered the toxic Tanic ore and turned it into a dark, shimmering tattoo‑style body art. By age 18 their entire skin is covered in these black, luminescent designs, which grant them superhuman strength, dexterity and blinding speed, allowing them to “pierce the Veil of Reality” and move through the very atmosphere itself. Their bodies are their primary weapons; a few can bond with lost “weapons of power,” becoming near‑godlike when wielding such artifacts. Malilarians are muscular, lean, taciturn, and their few spoken words compel involuntary compliance from most races—except Katakin and Latonian. They originated as slaves who broke free, and today they are hunted by desperate hominids for profit, while their true origins remain shrouded in legend.\n");
        Game.updateChatWindow(Color.YELLOW, races.toString());
    }

    public static void listClasses(){
        StringBuilder classes = new StringBuilder();
        classes.append("CLASSES------------------------------------------------\n")
                .append("\n\n")
                .append("Student:------------------------------------------------\n")
                .append("\n")
                .append(" Students in the world of Arin are a combination of effort so great they are capable of Learning any skill they perceive.")
                .append(" NOTE:-------------------------- This class is a work in progress")
                .append("\n\n")
                .append(" Geomancer:------------------------------------------------\n")
                .append("\n")
                .append(" Geomancers in Arin channel planetary, atmospheric, and life‑force energies to hurl, reshape, or even control earth, stone, and occasionally celestial bodies—once wielding artifacts capable of moving entire planets—making them master manipulators of energy, arcane lore, and technology, but also the frailest class in terms of defense, hit points, and physical strength.\n")
                .append("\n\n")
                .append(" VeilWalkers:------------------------------------------------\n")
                .append("\n")
                .append(" VeilWalkers draw their power from the unseen astral plane, allowing them to traverse and manipulate that realm to affect the material world; the farther they shift from their physical bodies, the more they perceive the astral and the less they see the material, making them ideal invisible explorers, scouts, and assassins whose presence is generally imperceptible, though some can sense them.\n")
                .append("\n\n")
                .append(" SoulKeepers:------------------------------------------------\n")
                .append("\n")
                .append(" SoulKeepers in the world of Arin draw their power from the souls of living entities, and ghosts or spirits, astral or otherwise. They can catch souls of the entities they kill in combat as they pass through their reality plane, The SoulKeeper can capture powers, skills or Knowledge and is based on a Dice Roll 0-100. 0 Being nothing, 100 being a god-like but high risk power, skill, stat, etc. SoulKeepers store souls in a physical object of meaning and must be wielding or wearing said object to capture the soul. SoulKeepers can also release into a weapon of their choosing, or release all the souls they've captured releasing a shockwave that damages any enemies around their person, and also release all their skills.\n")
                .append("\n\n")
                .append(" Tanic Knights:------------------------------------------------\n")
                .append("\n")
                .append(" Tanic Knights in the world of Arin draw their power from the Tanic infused armor they wear. Tanic Knights are trained from birth to be warriors. At the age of 5 they are seperated from their parents and train until they are 18 years of age. Each year they wear heavier and heavier weighted clothes or armor. They are extremely strong and their Tanic Armor increases their strength, agility and dexterity. If a Tanic Knight removes his armor has their strength and dexterity increased. However If they are Malilarian they gain incredible strength and dexterity.\n")
                .append("\n\n")
                .append(" Wraiths:------------------------------------------------\n")
                .append("\n")
                .append(" Wraiths are elite hand‑to‑hand combatants—primarily Malilarians, though occasional Latonian, Humakin, or Drokin variants exist with lesser prowess—who master martial arts and grappling, and some Malilian Wraiths augment their ferocity with a destructive relic weapons, usually ancient Staffs of Power.\n");
        Game.updateChatWindow(Color.YELLOW, classes.toString());
    }

    public static void listCharacter() {
        Character somePlayerCharacter = playerGroup.getFirst();
//        String someCharacterData = somePlayerCharacter.getCharacterStats().toString();
//        GameEngine.printOutput(Color.YELLOW, getTitle, someCharacterData);
        Game.updateChatWindow(Color.YELLOW, "Character Sheet:--------------WIP----------- v000");
        Game.updateChatWindow(Color.YELLOW, "Character Title:--------------------------- " + somePlayerCharacter.getTitle());
        Game.updateChatWindow(Color.YELLOW, "First Name:-------------------------------- " + somePlayerCharacter.getFirstName());
        Game.updateChatWindow(Color.YELLOW, "Last Name:--------------------------------- " + somePlayerCharacter.getLastName());
        Game.updateChatWindow(Color.YELLOW, "Race:-------------------------------------- " + somePlayerCharacter.getRace());
        Game.updateChatWindow(Color.YELLOW, "Class:------------------------------------- " + somePlayerCharacter.getCharacterClass());
        Game.updateChatWindow(Color.YELLOW, "Character Background/Description:---------- " + somePlayerCharacter.getBackground());
        Game.updateChatWindow(Color.YELLOW, "Location:---------------------------------- " + somePlayerCharacter.getLocation());
        Game.updateChatWindow(Color.YELLOW, "Posture:----------------------------------- " + somePlayerCharacter.getCurrentPosture());
        Game.updateChatWindow(Color.YELLOW, "Demeanor:---------------------------------- " + somePlayerCharacter.getDemeanor());
        Game.updateChatWindow(Color.YELLOW, "HP:---------------------------------------- " + somePlayerCharacter.getHitPoints());
        Game.updateChatWindow(Color.YELLOW, "MP:---------------------------------------- " + somePlayerCharacter.getManaPoints());
        Game.updateChatWindow(Color.YELLOW, "AP:---------------------------------------- " + somePlayerCharacter.getActionPoints());
        Game.updateChatWindow(Color.YELLOW, "EP:---------------------------------------- " + somePlayerCharacter.getExhaustionPoints());
        Game.updateChatWindow(Color.YELLOW, "Int:--------------------------------------- " + somePlayerCharacter.getIntelligence());
        Game.updateChatWindow(Color.YELLOW, "Str:--------------------------------------- " + somePlayerCharacter.getStrength());
        Game.updateChatWindow(Color.YELLOW, "Sta:--------------------------------------- " + somePlayerCharacter.getStamina());
        Game.updateChatWindow(Color.YELLOW, "Dex:--------------------------------------- " + somePlayerCharacter.getDexterity());
        Game.updateChatWindow(Color.YELLOW, "Wis:--------------------------------------- " + somePlayerCharacter.getWisdom());
        Game.updateChatWindow(Color.YELLOW, "Cha:--------------------------------------- " + somePlayerCharacter.getCharisma());
        Game.updateChatWindow(Color.YELLOW, "Total XP:---------------------------------- " + somePlayerCharacter.getXP());
        Game.updateChatWindow(Color.YELLOW, "Level:------------------------------------- " + somePlayerCharacter.getLevel());
        Game.updateChatWindow(Color.YELLOW, "Influence Time:---------------------------- " + someTime);
        Game.updateChatWindow(Color.YELLOW, "Credits:----------------------------------- " + somePlayerCharacter.getCreditWallet());
        Game.updateChatWindow(Color.YELLOW, "Current Quest:----------------------------- " + somePlayerCharacter.getQuest());
        Game.updateChatWindow(Color.WHITE, "---------------------------------------------------------------------------- INVENTORY");
        Game.updateChatWindow(Color.YELLOW, "\n");
        ArrayList<String> someInventory = somePlayerCharacter.getInventory();
        for (int j = 0; j < someInventory.size(); j++) {
            //print out each item in the inventory.
            printOutput(Color.CYAN, getTitle, someInventory.get(j));
        }
    }

    // List the Sunhome13's newly established College.
    public static void listEDUSubjects(){
        StringBuilder subjects = new StringBuilder();
        subjects.append("CURRENT Educational Subjects----------------------------------------\n")
                .append("Magic--------------------------------------------------------------A\n")
                .append("\n\n")
                .append("I.----------------------------------------- Geomancy Fundamentals:\n")
                .append("II.------------------------------------------------ Soul Dynamics:\n")
                .append("III.------------------------------------------------- Astral Theory:\n")
                .append("IV.------------------- Advanced Astral-Physical Manipulation Theory:\n")
                .append("\n\n")
                .append("Technology---------------------------------------------------------B\n")
                .append("\n\n")
                .append("I.-------------------------------------------- Tanic Technologies:\n")
                .append("II.-------------------------------- Tanic Harvesting Technologies:\n")
                .append("III.------------------------------- Tanic Processing Technologies:\n")
                .append("IV.---- Advanced Astral-Physical Tanic Manipulation Technologies:\n:")
                .append("\n\n")
                .append("Mythology--------------------------------------------------------C\n")
                .append("\n\n")
                .append("I.----------------------------------------- Mythology: Malilarians\n")
                .append("II.-------------------------------------------- Mythology: Wraiths\n")
                .append("III.-------------------------------- Mythology: Massive Monoliths \n")
                .append("IV.-------------------------------------------- Mythology: Drokin \n")
                .append("\n\n")
                .append("Histories--------------------------------------------------------D\n")
                .append("\n\n")
                .append("I.----------------------------------------------- History: Humakin\n")
                .append("II.---------------------------------------------- History: Katakin\n")
                .append("III.----------------------------------------- History: Malilarians\n")
                .append("IV.-------------------------------------------- History: Latonian\n");
        Game.updateChatWindow(Color.YELLOW, subjects.toString());
    }
    // generators
    public static int randomInt(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

    // Takes race, gets race bonus, and returns a random roll + current racial bonus.
    public static int getNewStat(String race) {
        int newRoll = randomInt(0, 100);
        RaceBonusRange newBonus = getRaceBonus(race);
        int newCurBonus = randomInt(newBonus.min, newBonus.max);
        return newRoll + newCurBonus;
    }

    //    placeholder 'weather' data.
    public static String getWeather(){
        JsonObject currentWeather = new JsonObject();
        currentWeather.addProperty("rain", false);
        currentWeather.addProperty("snow", false);
        currentWeather.addProperty("thunder", false);
        currentWeather.addProperty("sleet", false);
        currentWeather.addProperty("fog", false);
        currentWeather.addProperty("wind", true);
        currentWeather.addProperty("sun", false);
        currentWeather.addProperty("temp", 75);
        currentWeather.addProperty("humidity", 70);
        currentWeather.addProperty("windSpeed", 4);
        currentWeather.addProperty("windDirection", 0);
        currentWeather.addProperty("smell", 0);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(currentWeather);
    }

    // use record inplace for "set".
    public record RaceBonusRange(int min, int max) {}

    // returns a range of ints in a record. R
    public static RaceBonusRange getRaceBonus(String race) {
        switch (race) {
            case "malilarian" -> {
                return new RaceBonusRange(6, 9);
            }
            case "latonian" -> {
                return new RaceBonusRange(1, 5);
            }
            case "humakin" -> {
                return new RaceBonusRange(1, 5);
            }
            case "katakin" -> {
                return new RaceBonusRange(4, 7);
            }
            case "drokin" -> {
                return new RaceBonusRange(20, 40);
            }
            case "homonidbeast" -> {
                return new RaceBonusRange(1, 8);
            }
            case "giant" -> {
                return new RaceBonusRange(10, 20);
            }
            case "magical" -> {
                return new RaceBonusRange(5, 20);
            }
            default -> {
                return new RaceBonusRange(0, 0);
            }
        }
    }
}