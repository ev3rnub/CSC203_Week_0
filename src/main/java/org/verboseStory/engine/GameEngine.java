package org.verboseStory.engine;

// my classes
import com.google.gson.*;
import org.verboseStory.api.LocalOllama_API;
import org.verboseStory.model.Student;
import org.verboseStory.ui.Game;
import org.verboseStory.ui.Scene;
import org.verboseStory.ui.Inventory;
import org.verboseStory.api.Xai_Api;
import org.verboseStory.model.Character;

// std
import java.awt.*;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//My simple game engine. It displays the title, and generates the welcome text that follows. It also receives player input from the UI via and sends to the XAI API to be processed.

public final class GameEngine {

    // working vars
    public static volatile boolean STARTED = false;
    private static volatile boolean DIALOGSTARTED = false;
    public static volatile boolean INITIAL = true;
    private static String TARGET = "EXTERNALAPI";
    public static volatile String playerKey = "";
    public static HttpClient currentClient;
    private static String geTitle = "GAMEENGINE";

//    players main character
    public static volatile Character playerCharacter = null;
//    A place to store all created characters
    public static volatile ArrayList<Character> playerGroup = new ArrayList<>();

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
            get_key_word("welcome");
            get_key_word("CREATECHARACTER");//displays welcome text
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        while (STARTED) {
            try {
                if (!DIALOGSTARTED) {
                    String someResponse = new_dialog(""); // starts player game/interaction
                    printOutput(Color.CYAN, "StoryMaster: ", someResponse);
                }else{
                    printOutput(Color.RED, geTitle, "INPUT required--------------------");
                    String someInput = readLine();
                    printOutput(Color.YELLOW,  "", "\n\n\n");
                    printOutput(Color.YELLOW,  playerKey, someInput);
                    printOutput(Color.YELLOW,  "", "\n\n\n");
                    String someResponse = new_dialog(someInput);
                    printOutput(Color.CYAN, geTitle, someResponse);

                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // used to display, the title welcome/intro text #FutureRefactor
    public void get_key_word(String aType) throws Exception {
        //if string matches welcome, run case "welcome"
        switch (aType) {
            case "welcome" -> {
                printOutput(Color.CYAN, geTitle, "V   VEEEEERRRR BBBB  OOO  SSS EEEEE     H   H OOO M   M III N   N III DDDD       SSS U   UN   NH   H OOO M   MEEEEE  1   333 ");
                printOutput(Color.CYAN, geTitle," V  VE    R   RB   BO   OS    E         H   HO   OMM MM  I  NN  N  I  D   D  :  S    U   UNN  NH   HO   OMM MME     11      3");
                printOutput(Color.CYAN, geTitle," V V EEE  RRRR BBBB O   O SSS EEE       HHHHHO   OM M M  I  N N N  I  D   D      SSS U   UN N NHHHHHO   OM M MEEE    1    33 ");
                printOutput(Color.CYAN, geTitle,"  V  E    R R  B   BO   O   S E         H   HO   OM   M  I  N  NN  I  D   D  :     S U   UN  NNH   HO   OM   ME      1      3");
                printOutput(Color.CYAN, geTitle,"  V  EEEEER  R BBBB  OOO SSS  EEEEE     H   H OOO M   M III N   N III DDDD      SSS   UUU N   NH   H OOO M   MEEEEE11111 333 ");
                printOutput(Color.CYAN, geTitle,"\n\n");
                printOutput(Color.CYAN, geTitle,"Welcome to Verbose Hominid: Sunhome13 (VHSH13), a Science Fiction/Fantasy text based adventure in a fictional hominid world! Work-In-Progress");
                printOutput(Color.CYAN, geTitle,"\n\n");
                printOutput(Color.GREEN, geTitle,"************* Note Board **************");
                printOutput(Color.GREEN, geTitle,"Note0: There is currently no in game music, use your favorite non vocal music playlist.");
                printOutput(Color.GREEN, geTitle,"Note1: Account and account Phrase is placeholder, so enter what you will");
                printOutput(Color.GREEN, geTitle,"Note2: A xAI api key is required, VHSH13 looks for environmental variable xAI_API_KEY.");
                printOutput(Color.GREEN, geTitle,"Note3: You can use your words instead of using the options given. Use your imagination.");
                printOutput(Color.GREEN, geTitle,"Note4: If you want to command the Story Master use, Command: What is currently in my inventory.");
                printOutput(Color.GREEN, geTitle,"Note5: If you want to question the Story Master use, Question: Where am I, what time is it in the world?");
                printOutput(Color.GREEN, geTitle,"\n\n");
                printOutput(Color.YELLOW, geTitle,"************* Notice Board **************");
                printOutput(Color.YELLOW, geTitle,"1. No save as of yet, and I've not tested long enough to determine if everything works.");
                printOutput(Color.YELLOW, geTitle,"2. Started working on the 'game' part of app. Creating a base Character Class");
                printOutput(Color.YELLOW, geTitle,"\n\n");
                printOutput(Color.YELLOW, geTitle,"************* INPUT Required *************");
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
                playerGroup.addFirst(somePlayerCharacter);
                printOutput(
                        Color.CYAN,
                        "GameEngine",
                        "Created Character ------------------------------------" + playerKey + ".\n");
                printOutput(Color.CYAN, geTitle, "\n\n\n\n\n\n");
                listCharacter();
                STARTED = true;
            }
            default -> printOutput(Color.RED, geTitle, "Invalid Input, received: " + aType);
        }
    }

    // this invokes our LLM API, default is External API or XAI or whatever TARGET is set to.
    public String new_dialog(String someInput) throws InterruptedException {
        String currentResponse = "";
        // get the player character, which is always 0 index, as index 1+ is fpr NPC that are part of
        // the players group.
        // NOTE: Index 1 is reserved for the minion/monster pet mini game.
        Character somePlayerCharacter = playerGroup.getFirst();
        JsonObject parsedPC = new JsonObject();
        parsedPC.addProperty("Title", somePlayerCharacter.getTitle());
        parsedPC.addProperty("FirstName", somePlayerCharacter.getFirstName());
        parsedPC.addProperty("LastName", somePlayerCharacter.getLastName());
        parsedPC.addProperty("Class", somePlayerCharacter.getCharacterClass());
        parsedPC.addProperty("BackGround", somePlayerCharacter.getBackground());
        switch (TARGET) {
            case "EXTERNALAPI" -> {
                printOutput(Color.CYAN, "GameEngine", "CharacterSheet: " + parsedPC.getAsJsonObject().toString() + "UserInput: " + someInput);
                currentResponse = Xai_Api.invokeResponseFromGrok(
                        "CharacterSheet: " + parsedPC.getAsJsonObject().toString() + "UserInput: " + someInput);
                        DIALOGSTARTED = true;
            }
            case "INTERNALAPI" -> {
                currentResponse = LocalOllama_API.invokeResponseFromLocal(
                        "CharacterSheet: " + parsedPC.getAsJsonObject().toString() + "UserInput: " + someInput); // LOCAL: Local Ollama instance
                DIALOGSTARTED = true;
            }
            default -> {
                printOutput(Color.CYAN, "GameEngine", "ERROR---------------- " + TARGET + "not implemented yet./Invalid Input");
            }
        }
        return currentResponse;
    }

    public static void printOutput(Color someColor, String from, String someOutput){
        Game.updateChatWindow(someColor, from + ": " + someOutput);
    }

    // Prints out a list of the players generated character stats (While functional
    // its not fully implemented/working fully. The LLM may or may not use the stats.)

    //Character creation.
    // Required input/data to create a player character in VerboseHominid:SunHome13.
    public Character createCharacter() throws InterruptedException {
        Game.updateChatWindow(Color.YELLOW, "------------------------------------------------");
        Game.updateChatWindow(Color.YELLOW, "Enter Character First Name:---------------------");
        String firstName = readLine();
        playerKey = firstName.trim();
        Game.updateChatWindow(Color.YELLOW, "Enter Character Last Name:---------------------");
        String lastName = readLine();
        listRaces();
        Game.updateChatWindow(Color.YELLOW, "\n\n");
        Game.updateChatWindow(Color.YELLOW, "Choose your Character's Race:------------------");
        String someRace = readLine();
        Game.updateChatWindow(Color.YELLOW, "\n\n");
        listClasses();
        Game.updateChatWindow(Color.YELLOW, "\n\n");
        Game.updateChatWindow(Color.YELLOW, "\n\n");
        Game.updateChatWindow(Color.YELLOW, "Choose your Character's Class:-----------------");
        String someCharacterClass = readLine().toLowerCase();
        String someSubject;
        // if professor class, ask for specific professor class inputs. This is not presented as a playable character
        // class :).
        if (someCharacterClass.equals("professor")) {
            listEDUSubjects();
            Game.updateChatWindow(Color.YELLOW, "Define your Subject:-----------------------");
            someSubject = readLine();
            Game.updateChatWindow(Color.YELLOW, "Subject embedded-----------------------");
        }
        Game.updateChatWindow(Color.YELLOW, "Define your Background--------------------\n");
        Game.updateChatWindow(Color.YELLOW, "Example:---Define keywords; poor, dead beat dad,\n");
        Game.updateChatWindow(Color.YELLOW, "--------------------------quest to find a staff.\n");
        Game.updateChatWindow(Color.YELLOW, "------------------------------------------\n");
        String someBackground = readLine();
        Game.updateChatWindow(Color.YELLOW, "Background embedded-----------------------");
        Game.updateChatWindow(Color.YELLOW, "\n\n\n\n\n");

        Student someCharacter = null;
        switch(someCharacterClass.toLowerCase()) {
            case "student":
                someCharacter = new Student(firstName, lastName, someBackground, someRace);
            default:
                Game.updateChatWindow(Color.YELLOW, "\n\n");
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
        races.append(" Humakin:------------------------------------------------\n")
                .append("\n")
                .append(" The majority of the planet is inhabited by a human‑like race called Humakin, they are essentially human like in nature, full of inovation and driven by curosity, subject matter experts on lying, they generally vary in regards to body composition. Humakin live on average to 70 years. They have no special skills and are relatively good at everything. Jack of all trades, Master of none.\n")
                .append("\n\n")
                .append(" Katakin:------------------------------------------------\n")
                .append("\n")
                .append(" There are very few humanoid‑cat hybrids from the 1st moon Kata, they are called Kata. They are taken from birth from a moon by powerful Geomancers to be trained to participate in the Kata Games in the main Capital of Zirrin; The majority are in captivity, a group did break free and thrive in places of the wild, young male wild Kata who were born on Arin usually break from their group and seek adventure, or revenge. While they have a long lifespan due to the stresses of Arin, Arin born Kata only live an average of 100 years. They are generally very lean and muscular and about 7 feet tall, covered in fur and look essentially like a humanoid cat. Kata have the capability to use “purring” or sonics to heal/mend broken bones and injuries over a short period of time, to others and themselves. Kata are warriors and are direct in communication. They can speak Kata (A series of clicks and tones, almost like singing meows in a deep bass tone) and Humakin. Katakin can climb almost anything, except Tanic. Katakin can wield any weapon or dawn any armor they choose. They mostly perfer light armor that doesn't make much noise.\n")
                .append("\n\n")
                .append(" Latonians:------------------------------------------------\n")
                .append("\n")
                .append(" NOTE: Latonians are curious by nature, fun, playful. Yet they tend to remain hidden from strangers in a demeanor made from the very interaction with the stranger. They can tell when beings lie, and can influence without trying. \n")
                .append(" A race of super highly intelligent small humanoids that live in a ring of lush warm vegetation located in the North Pole of Arin, they are called the Latonians. Their body composition is generally small of stature but also very fit, muscular and strong for their physical size. They resemble Humakin children when fully garbed. They range from 3 to 5 feet in height. They are essentially unknown to all of the other races and often only leave the ring's higher gravity well in 1-9 years at a time. If they do leave their ring, they pretend to be Parentless Humakin Children, however they can fight if required, but only as a last resort. Latonians have an innate ability to understand,figure out any technology, problem, challenge or language given enough time. They also have the ability to influence other less intelligence minds to do their bidding, using their natural tonality of their voice, they can involuntary influence any living entity, some even say they can control Drokin. They are generally non malicious in nature, however not much is known of their demeanor, as most Latonians generally keep to themselves even when in groups, only showing their true demeanor around kin. The Latonians designed, built and deployed SunHome13 Space Station in recent years.\n")
                .append("\n\n")
                .append(" Malilarians:------------------------------------------------\n")
                .append("\n")
                .append(" NOTE: Malilarians while violent, only resort to violence as a last resort. As the longer they go without violence ths stronger in a commanding presence they become.\n")
                .append(" A race from the Wildlands, some call them Wraiths, Extraordinary deadly fighters who have learned how to\n")
                .append(" harvest and process Tanic Ore into a powder form. Poisonous to the rest of the races on Arin, but Malilarians\n")
                .append(" were developed to resist its poisonous affects over untold generations. Malilarians are supposedly genetically modified Humakin, some say developed\n")
                .append(" by the Drokin as a Brutally effective Droken Military Force. From creation, modification, to birth, it is marked in some way shape or form, using Tanic. In the form of Tattoo's gives a Malilarian unnatural strength and dexterity and the\n")
                .append(" ability to move so extremely fast they can pierce the Veil of Reality; Unlike VeilWalkers who can pass through solid material, a Malilarians can pass through the very atmosphere that surrounds every entity. They only use black pigment and by the age of 18 all Maliliarn bodies\n")
                .append(" are essentially covered in a tribal design that shimmers in the very dark places of Arin. What they lack in knowledge and intelligence they more then make up for in extreme movement speed, extreme strength and dexterity.\n")
                .append(" Malilarians use a single weapon, their body is their weapon.\n")
                .append(" Some Malilarians are capable of using a chosen weapon, however Malilarians only bond with the special weapons of power, lost to the current age;\n")
                .append(" Whereas many other races would perish on contact with a true weapon of power a Malilarian would become almost godlike in certain aspects.\n")
                .append(" Malilarian's are generally very muscular and lean regardless of build, they are generally quiet unless spoken to, and even then they say few words.\n")
                .append(" The words they speak invoke an almost involuntary compliance and respect from the other races except for the Katakin and Latonian. The first Malilarians\n")
                .append(" were said to have defeated or escaped their slave masters hold. No one really knows except for what is passed down from generation to generation. Some desperate Hominids hunt Malilarian folk for profit. \n");
        Game.updateChatWindow(Color.YELLOW, races.toString());
    }

    public static void listClasses(){
        StringBuilder classes = new StringBuilder();
        classes.append("CLASSES------------------------------------------------\n")
                .append("\n\n")
                .append("Student:------------------------------------------------\n")
                .append("\n")
                .append(" Students in the world of Arin learn from the professors that teach within SunHome13's Orbital College\n")
                .append(" You'll start on a shuttle on your way to SunHome13 Space Station. You'll attend a complete day, 10ish hours, 10 turns, have fun.")
                .append(" NOTE:-------------------------- This class is a work in progress")
                .append("\n\n")
                .append(" Geomancer:------------------------------------------------\n")
                .append("\n")
                .append(" Geomancer in the world of Arin draw their power from the planet, atmospheric energies and or life force\n")
                .append(" from living entities. Capable of using energetic forces to throw, move or manipulate earth, stone, and\n")
                .append(" sometimes if the Geomancer is strong enough even astroids or comets from space. The great Geomancer of old could even manipulate the planets themselves with the\n")
                .append(" aid of some ancient artifacts of power. Geomancer are masters of energy manipulation, arcane knowledge and technology.\n")
                .append(" They are the most powerful class in the world of Arin, but they are also the weakest in regards to defense, hit-points, and strength.\n")
                .append("\n\n")
                .append(" VeilWalkers:------------------------------------------------\n")
                .append("\n")
                .append(" VeilWalkers in the world of Arin draw their power from the astral plane, the plane of the unseen. They are able to travel and manipulate the astral plane which directly affects the material plane. The farther they travel away from their material body, the less accurate they see the material plane, and more of the astral plane. They make great explorers, scouts, assasins who can essentially remain invisible using their astral body, most inhabitants in the world of Arin cannot observe a Veilwalker however some can 'feel' their presence.\n")
                .append("\n\n")
                .append(" SoulKeepers:------------------------------------------------\n")
                .append("\n")
                .append(" SoulKeepers in the world of Arin draw their power from the souls of living entities, ghosts. They can catch souls of the entities they kill in combat, absorbing any powers, skills or Knowledge in the process both mental or physical. SoulKeepers store souls in a physical object of meaning and must be wielding or wearing said object to capture the soul. SoulKeepers can also release all the souls they've captured releasing a shockwave that damages any enemies around their person, and also releases all their skills. SoulKeepers can briefly reanimate the dead, can attach energy to living beings to make the entity heal, cure and or injure, kill by touch.\n")
                .append("\n\n")
                .append(" Tanic Knights:------------------------------------------------\n")
                .append("\n")
                .append(" Tanic Knights in the world of Arin draw their power from the Tanic infused armor they wear. Tanic Knights are trained from birth to be warriors. At the age of 5 they are seperated from their parents and train until they are 18 years of age, at which point they get awarded a weapon type. Each year they wear heavier and heavier weighted clothes or armor. They are extremely strong and their Tanic Armor increases their strength, agility and dexterity. If a Tanic Knight removes his armor(compressed process tanic ore) has their strength and dexterity increased 2 times. However If they are Malilarian they gain incredible strength and dexterity increase of 50 times.\n")
                .append("\n\n")
                .append(" Wraiths:------------------------------------------------\n")
                .append("\n")
                .append(" Wraiths specialize in hand to hand combat, martial arts and grappling. They weld no weapons, only their bodies as weapons. Wraiths are generally only Malilarian however there are reports of Latonian, Humakin and Drokin Wraiths, however they dont have the same strength or dexterity as a Malilarian.\n");
        Game.updateChatWindow(Color.YELLOW, classes.toString());
    }

    public static void listCharacter() {
        List<Character> someGroup = playerGroup;
        Game.updateChatWindow(Color.YELLOW, "Character Sheet:---------------------------- v000");
        Game.updateChatWindow(Color.YELLOW, "Character Title:--------------------------- " + someGroup.get(0).getTitle());
        Game.updateChatWindow(Color.YELLOW, "First Name:-------------------------------- " + someGroup.get(0).getFirstName());
        Game.updateChatWindow(Color.YELLOW, "Last Name:--------------------------------- " + someGroup.get(0).getLastName());
        Game.updateChatWindow(Color.YELLOW, "Race:-------------------------------------- " + someGroup.get(0).getRace());
        Game.updateChatWindow(Color.YELLOW, "Class:------------------------------------- " + someGroup.get(0).getCharacterClass());
        Game.updateChatWindow(Color.YELLOW, "Character Description:--------------------- " + someGroup.get(0).getBackground());
        Game.updateChatWindow(Color.YELLOW, "Location:---------------------------------- " + someGroup.get(0).getLocation());
        Game.updateChatWindow(Color.YELLOW, "Posture:----------------------------------- " + someGroup.get(0).getCurrentPosture());
        Game.updateChatWindow(Color.YELLOW, "Demeanor:---------------------------------- " + someGroup.get(0).getDemeanor());
        Game.updateChatWindow(Color.YELLOW, "HP:---------------------------------------- " + someGroup.get(0).getHitPoints());
        Game.updateChatWindow(Color.YELLOW, "MP:---------------------------------------- " + someGroup.get(0).getManaPoints());
        Game.updateChatWindow(Color.YELLOW, "AP:---------------------------------------- " + someGroup.get(0).getActionPoints());
        Game.updateChatWindow(Color.YELLOW, "EP:---------------------------------------- " + someGroup.get(0).getExhaustionPoints());
        Game.updateChatWindow(Color.YELLOW, "Int:--------------------------------------- " + someGroup.get(0).getIntelligence());
        Game.updateChatWindow(Color.YELLOW, "Str:--------------------------------------- " + someGroup.get(0).getStrength());
        Game.updateChatWindow(Color.YELLOW, "Sta:--------------------------------------- " + someGroup.get(0).getStamina());
        Game.updateChatWindow(Color.YELLOW, "Dex:--------------------------------------- " + someGroup.get(0).getDexterity());
        Game.updateChatWindow(Color.YELLOW, "Wis:--------------------------------------- " + someGroup.get(0).getWisdom());
        Game.updateChatWindow(Color.YELLOW, "Cha:--------------------------------------- " + someGroup.get(0).getCharisma());
    }

    // List the Sunhome13's newly established College.
    public static void listEDUSubjects(){
        StringBuilder subjects = new StringBuilder();
        subjects.append("CURRENT Educational Subjects----------------------------------------")
                .append("Magic--------------------------------------------------------------A")
                .append("\n\n")
                .append("I.----------------------------------------- Geomancy Fundamentals:\n")
                .append("II.------------------------------------------------ Soul Dynamics:\n")
                .append("III.------------------------------------------------- Astral Theory:")
                .append("IV.------------------- Advanced Astral-Physical Manipulation Theory:")
                .append("\n\n")
                .append("Technology---------------------------------------------------------B")
                .append("\n\n")
                .append("I.-------------------------------------------- Tanic Technologies:\n")
                .append("II.-------------------------------- Tanic Harvesting Technologies:\n")
                .append("III.------------------------------- Tanic Processing Technologies:\n")
                .append("IV.---- Advanced Astral-Physical Tanic Manipulation Technologies:\n:")
                .append("\n\n")
                .append("Mythology--------------------------------------------------------C")
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