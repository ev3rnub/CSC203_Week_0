//Chad Verbus
//CSC203
// 11052025


package org.verboseStory;

/* -------------------------------------------------------------
 *  AWT – Core GUI components & graphics utilities
 * ------------------------------------------------------------- */
import java.awt.*;                // General AWT classes (Component, LayoutManager, Color, Font)
import java.awt.event.*;          // Event‑handling interfaces & adapters (ActionListener, MouseEvent)
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
/* -------------------------------------------------------------
 *  I/O & networking utilities
 * ------------------------------------------------------------- */
import java.io.IOException;       // Checked exception thrown by many I/O operations
import java.net.URI;              // Represents a Uniform Resource Identifier
import java.net.http.*;           // HTTP client API (HttpClient, HttpRequest, HttpResponse)

/* -------------------------------------------------------------
 *  Core collections framework
 * ------------------------------------------------------------- */
import java.util.*;               // General utilities (Collections, Map, Set, Date, etc.)
import java.util.List;            // Specific import – you probably need List only
import java.util.concurrent.*;   // Concurrency utilities (ExecutorService, Future, CompletableFuture)

/* -------------------------------------------------------------
 *  Swing – richer GUI components built on top of AWT
 * ------------------------------------------------------------- */
import javax.swing.*;             // Swing components (JFrame, JPanel, JButton, JTable)
import javax.swing.text.*;        // Text package (Document, StyledDocument, AttributeSet.)

/* -------------------------------------------------------------
 *  Third‑party JSON library (Google Gson)
 * ------------------------------------------------------------- */
import com.google.gson.*;         // Gson core classes (Gson, JsonElement, JsonObject, JsonParser)
/* -------------------------------------------------------------
*   Regex
* -------------------------------------------------------------- */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerboseHominid {

    // JFrame Window to display Scene Text
    private static final class SceneWindow extends JFrame {
        // create a JTextPane named scenePane, this will hold our scene text.
        private final JTextPane scenePane;
        private final BlockingQueue<String> sceneQueue;

        SceneWindow(BlockingQueue<String> sceneQueue) {
            super("Verbose Hominid v0.0.1: SCENE History");
            this.sceneQueue = sceneQueue;

            //setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(680, 420);
            setLocationRelativeTo(null);
            setBackground(Color.BLACK);

            JPanel main = new JPanel(new BorderLayout(5, 5));
            main.setBackground(Color.BLACK);
            setContentPane(main);

            /* ---------- Scene text pane ---------- */
            scenePane = new JTextPane();
            scenePane.setEditable(false);
            scenePane.setBackground(Color.BLACK);
            scenePane.setForeground(Color.WHITE);
            scenePane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
            scenePane.setBorder(BorderFactory.createLineBorder(Color.WHITE));
            JScrollPane scroll = new JScrollPane(scenePane);
            scroll.getViewport().setBackground(Color.BLACK);
            main.add(scroll, BorderLayout.CENTER);
        }

        void appendScene(Color color, String text) {
            SwingUtilities.invokeLater(() -> {
                StyledDocument doc = scenePane.getStyledDocument();
                Style style = scenePane.addStyle("color", null);
                StyleConstants.setForeground(style, color);
                try {
                    doc.insertString(doc.getLength(), text + "\n", style);
                    scenePane.setCaretPosition(doc.getLength());
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }


    // JFrame Main Game Window
    private static final class GameWindow extends JFrame {

        private final JTextPane logPane;
        private final JTextField inputField;
        private final JButton sendButton;
        private final JButton sceneButton;
        private final BlockingQueue<String> inputQueue;

        GameWindow(BlockingQueue<String> inputQueue) {
            super("Verbose Hominid v0.0.1: Story Master");
            this.inputQueue = inputQueue;

            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(1200, 780);
            setLocationRelativeTo(null);
            setBackground(Color.BLACK);

            JPanel main = new JPanel(new BorderLayout(5, 5));
            main.setBackground(Color.BLACK);
            setContentPane(main);

            /* ---------- Chat Log pane ---------- */
            logPane = new JTextPane();
            logPane.setEditable(false);
            logPane.setBackground(Color.BLACK);
            logPane.setForeground(Color.WHITE);
            logPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
            logPane.setBorder(BorderFactory.createLineBorder(Color.WHITE));
            JScrollPane scroll = new JScrollPane(logPane);
            scroll.getViewport().setBackground(Color.BLACK);
            main.add(scroll, BorderLayout.CENTER);

            /* ---------- Input line (field + button) ---------- */
            JPanel inputPanel = new JPanel();
            inputPanel.setBackground(Color.BLACK);
            inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
//            inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            inputPanel.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.WHITE));
            inputField = new JTextField();
            inputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            inputField.setBackground(Color.DARK_GRAY);
            inputField.setForeground(Color.GREEN);
            inputField.setCaretColor(Color.GREEN);
            inputField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

            sendButton = new JButton("Send");
            sendButton.setFocusPainted(false);
            sendButton.setBackground(new Color(0x2A2A2A));
            sendButton.setForeground(Color.BLACK);

            sceneButton = new JButton("Scene");
            sceneButton.setFocusPainted(false);
            sceneButton.setBackground(new Color(0x2A2A2A));
            sceneButton.setForeground(Color.BLACK);

            inputPanel.add(inputField);
            inputPanel.add(Box.createRigidArea(new Dimension(8, 0)));
            inputPanel.add(sendButton);
            inputPanel.add(Box.createRigidArea(new Dimension(8, 0)));
            inputPanel.add(sceneButton);
            main.add(inputPanel, BorderLayout.SOUTH);

            // UI Events Enter, send btn.
            ActionListener send = e -> {
                String line = inputField.getText().trim();
                if (!line.isEmpty()) {
                    //offer the string submitted to the input queue
                    inputQueue.offer(line);
                    // clear input field
                    inputField.setText("");
                }
            };

            // Add the action listener send to the sendButton and inputFields.
            sendButton.addActionListener(send);
            inputField.addActionListener(send);

            //Scene Button functionality, when pressed, display Scene Window, else hide.
            sceneButton.addActionListener(e -> {
                SceneWindow sceneWindow = Scene.getWindow();
                if (sceneWindow != null) {
                    sceneWindow.setVisible(!sceneWindow.isVisible());
                } else {
                    GameEngine.red_chat_output("SceneWindow not initilized");
                }
            });

            //Refocus input field
            addWindowListener(new WindowAdapter() {
                @Override public void windowOpened(WindowEvent e) {
                    inputField.requestFocusInWindow();
                }
            });
        }

        /** Append a colored line to the style document */
        void appendChat(Color color, String text) {
            SwingUtilities.invokeLater(() -> {
                StyledDocument doc = logPane.getStyledDocument();
                Style style = logPane.addStyle("color", null);
                StyleConstants.setForeground(style, color);
                try {
                    doc.insertString(doc.getLength(), text + "\n", style);
                    logPane.setCaretPosition(doc.getLength());
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    // Main Chat Text Window
    private static final class ChatWindow {
        private static GameWindow window;

        private ChatWindow() { }

        static void setWindow(GameWindow w) { window = w; }

        /** Log a line with the given colour. */
        static void updateChatWindow(Color color, String message) {
            if (window != null) {
                //parse output here
                window.appendChat(color, message);
                RegexEngine.parseOutput(message);
            } else {
                // Fallback – console only (should never happen after UI is up)
                GameEngine.red_chat_output(message);
            }
        }
    }
    // regex parser
    private static final class RegexEngine {
        private RegexEngine() { }
        static void parseOutput(String someString){
            Pattern scene = Pattern.compile("\\[SCENE\\](.*)\\[ENDSCENE\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            Pattern abilityScores = Pattern.compile("\\[ABILITYSCORES\\](.*)\\[ENDABILITYSCORES\\]", Pattern.CASE_INSENSITIVE);
            Pattern inventory = Pattern.compile("\\[INVENTORY\\](.*)\\[ENDINVENTORY\\]", Pattern.CASE_INSENSITIVE);
            Pattern stats = Pattern.compile("\\[STATS\\](.*)\\[ENDSTATS\\]", Pattern.CASE_INSENSITIVE);
            Pattern action = Pattern.compile("\\[ACTION\\](.*)\\[ENDACTION\\]", Pattern.CASE_INSENSITIVE);
            Pattern roll = Pattern.compile("\\[ROLL\\](.*)\\[ENDROLL\\]", Pattern.CASE_INSENSITIVE);
            Pattern result = Pattern.compile("\\[RESULT\\](.*)\\[ENDRESULT\\]", Pattern.CASE_INSENSITIVE);
            Pattern experience = Pattern.compile("\\[XP\\](.*)\\[ENDXP\\]", Pattern.CASE_INSENSITIVE);
            Pattern player = Pattern.compile("\\[PLAYER\\](.*)\\[ENDPLAYER\\]", Pattern.CASE_INSENSITIVE);
            Matcher sceneMatch = scene.matcher(someString);
            Matcher abilityMatch = abilityScores.matcher(someString);
            Matcher inventoryMatch = inventory.matcher(someString);
            Matcher statsMatch = stats.matcher(someString);
            Matcher actionMatch = action.matcher(someString);
            Matcher rollMatch = roll.matcher(someString);
            Matcher resultMatch = result.matcher(someString);
            Matcher xpMatch = experience.matcher(someString);
            Matcher playerMatch = player.matcher(someString);

            if (sceneMatch.find()) {
                GameEngine.green_scene_output(sceneMatch.group(1));
            }
        }
    }
    // Scene Text Window
    private static final class Scene {
        private static SceneWindow window;

        static void setWindow(SceneWindow w) { window = w;}
        static SceneWindow getWindow() {return window;};
        static void updateSceneWindow(Color color, String message) {
            if (window != null) {
                window.appendScene(color, message);
            }else{
                GameEngine.red_chat_output("Scene Window NULL");
            }
        }
    }

    // game engine
    private static final class GameEngine {

        // ----- colour‑escape constants (kept for compatibility) -----
        private static final String WHITE   = "\u001B[37m";
        private static final String MAGENTA = "\u001B[35m";
        private static final String BLUE    = "\u001B[34m";
        private static final String GREEN   = "\u001B[32m";
        private static final String RED     = "\u001B[31m";
        private static final String BLACK   = "\u001B[30m";

        // ----- game state -----
        private static Boolean STARTED = false;
        private static String playerKey = "";

        // ----- communication with UI -----
        private final BlockingQueue<String> inputQueue;


        GameEngine(BlockingQueue<String> inputQueue) {
            this.inputQueue = inputQueue;
        }

       // Sends event to take the next input string from the input queue.
        private String readLine() throws InterruptedException {
            return inputQueue.take();
        }

        //Start
        void run() {
            try {
                get_key_word("welcome");
                dialog_start();
            } catch (Exception e) {
                GameEngine.red_chat_output("UNHANDLED EXCEPTION: " + e);
                e.printStackTrace();
            }
        }

        //get_key_word
        public void get_key_word(String aType) throws Exception {
            switch (aType) {
                case "welcome":
                    GameEngine.white_chat_output("Welcome to Verbose Hominid, a text based adventure in a fictional hominid world!");
                    GameEngine.white_chat_output("Enter your Character's First Name, or what Verbose Hominid inhabitants can call you?");
                    String keyWord = readLine();
                    GameEngine.magenta_chat_output("Your Character Name is: " + keyWord);
                    GameEngine.green_chat_output("Is this CORRECT? Y/N: ");
                    String confirmed = readLine();
                    if (confirmed.equalsIgnoreCase("y") || confirmed.equalsIgnoreCase("yes")) {
                        STARTED = true;
                        playerKey = keyWord;
                        GameEngine.red_chat_output(playerKey + ", Your simulation has started!");
                    } else {
                        STARTED = false;
                        get_key_word("welcome");   // retry
                    }
                    break;
                default:
                    GameEngine.red_chat_output("Invalid Input, received: " + aType);
                    break;
            }
        }

        /*-------------------------- 3️⃣ dialog_start --------------------------*/
        public void dialog_start() {
            XaiApi.invokeResponseFromGrok("BEGIN_GAME");
        }

        /*-------------------------- Color Chat helpers --------------------------*/
        public static void blue_chat_output(String s)   { ChatWindow.updateChatWindow(Color.BLUE,   s); }
        public static void red_chat_output(String s)    { ChatWindow.updateChatWindow(Color.RED,    s); }
        public static void green_chat_output(String s)  { ChatWindow.updateChatWindow(Color.GREEN,  s); }
        public static void white_chat_output(String s)  { ChatWindow.updateChatWindow(Color.WHITE,  s); }
        public static void magenta_chat_output(String s){ ChatWindow.updateChatWindow(Color.MAGENTA,s); }
        /* ------------------------Color Scene Helpers ------------------------*/
        public static void blue_scene_output(String s)   { Scene.updateSceneWindow(Color.BLUE,   s); }
        public static void red_scene_output(String s)    { Scene.updateSceneWindow(Color.RED,    s); }
        public static void green_scene_output(String s)  { Scene.updateSceneWindow(Color.GREEN,  s); }
        public static void white_scene_output(String s)  { Scene.updateSceneWindow(Color.WHITE,  s); }
        public static void magenta_scene_output(String s){ Scene.updateSceneWindow(Color.MAGENTA,s); }
    }

   // Grok via xAI. Takes user input, and passes to Grok, awaits response
    private static final class XaiApi {

        private static final String API_BASE_URL = "https://api.x.ai/v1";
        private static final String MODEL = "grok-3";               // recommended model
        private static final List<JsonObject> messages = new ArrayList<>();

        /** Sends the initial prompt (or any later prompt) to Grok and prints the response. */
        public static void invokeResponseFromGrok(String initialPrompt) {
            String apiKey = System.getenv("xAI_API_KEY");
            if (apiKey == null || apiKey.isEmpty()) {
                GameEngine.red_chat_output("NO xAI_API_KEY, check if ENV variable exists!");
                return;
            }

            // ----- system instruction (static) -----
            String systemInstruction = buildSysInstruct();
            JsonObject systemMsg = new JsonObject();
            systemMsg.addProperty("role", "system");
            systemMsg.addProperty("content", systemInstruction);
            messages.add(systemMsg);

            HttpClient client = HttpClient.newHttpClient();
            Gson gson = new Gson();

            try {
                if (initialPrompt.equalsIgnoreCase("BEGIN_GAME")) {
                    JsonObject init = new JsonObject();
                    init.addProperty("role", "user");
                    init.addProperty("content", initialPrompt);
                    String resp = sendRequest(client, gson, apiKey);
                    GameEngine.white_chat_output("********** StoryMaster **********");
                    GameEngine.green_chat_output(resp);
                    JsonObject assistant = new JsonObject();
                    assistant.addProperty("role", "assistant");
                    assistant.addProperty("content", resp);
                    messages.add(assistant);
                }

                // ----- main loop – keep reading from console (now from UI) -----
                while (GameEngine.STARTED) {
                    // read a line from the UI‑provided queue
                    String userInput = GameEngineStaticHolder.engine.inputQueue.take();
                    if (userInput.equalsIgnoreCase("q") ||
                            userInput.equalsIgnoreCase("quit") ||
                            userInput.equalsIgnoreCase("exit")) {
                        GameEngine.STARTED = false;
                        break;
                    }
                    GameEngine.white_chat_output(GameEngine.playerKey + ": " + userInput);
                    JsonObject userMsg = new JsonObject();
                    userMsg.addProperty("role", "user");
                    userMsg.addProperty("content", userInput);
                    messages.add(userMsg);

                    String resp = sendRequest(client, gson, apiKey);
                    GameEngine.white_chat_output("********** StoryMaster **********");
                    GameEngine.green_chat_output(resp);

                    JsonObject assistantMsg = new JsonObject();
                    assistantMsg.addProperty("role", "assistant");
                    assistantMsg.addProperty("content", resp);
                    messages.add(assistantMsg);
                }
            } catch (IOException | InterruptedException e) {
                GameEngine.red_chat_output("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        private static String sendRequest(HttpClient client, Gson gson, String apiKey) throws IOException, InterruptedException {
            JsonObject someBody = new JsonObject();
            someBody.addProperty("model", MODEL);
            JsonArray msgs = new JsonArray();
            int N = 5; // keep last N messages for context
            int start = Math.max(0, messages.size() - N);
            for (int i = start; i < messages.size(); i++) {
                msgs.add(messages.get(i));
            }
            someBody.add("messages", msgs);
            someBody.addProperty("max_tokens", 20000);
            someBody.addProperty("temperature", 0.7);
            String jsonBody = gson.toJson(someBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            //Send data, await response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException("API request failed: " + response.statusCode() + " – " + response.body());
            }

            JsonObject json = gson.fromJson(response.body(), JsonObject.class);
            return json.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
        }

        // System prompt
        // WIP: Refactor to FileRead.
        private static String buildSysInstruct() {
            StringBuilder sb = new StringBuilder();
            sb.append("You are a Subject Matter Expert on Story telling and are considered a Story Master (SM) for a text-only, turn-based role-playing adventure game. Your job is to narrate the world (using the World Knowledge below), present choices, resolve ALL actions with random range 0-100 rolls and keep track of player stats, inventory, hit points, and story progression. Ensure there is a light diety/god, and one of dark diety/god.\n")
                    .append(" ### Core Rules\n")
                    .append(" 1. **Ability Scores** - Use the classic six (STR, DEX, CON, INT, WIS, CHA). Each starts at 10 (modifier 0) unless you assign a different value.\n")
                    .append(" 2. **Skill Checks & Attacks** - Roll 1d100 the relevant ability modifier (and proficiency if applicable).\n")
                    .append(" * **Success Threshold** - 50 DC (or AC for attacks).\n")
                    .append(" * **Critical Success** - natural 100 (auto-success, extra effect).\n")
                    .append(" * **Critical Failure** - natural 1 (auto-fail, possible complication).\n")
                    .append(" 3. **Combat** - Initiative = d100 DEX mod. Turn order repeats until combat ends.\n")
                    .append(" * On an attack roll, compare total to target AC.\n")
                    .append(" * DaGeomancer = weapon dice STR (or appropriate) modifier.\n")
                    .append(" * Reduce HP; a character at 5 HP is unconscious, -0hp = deaths door, -5 HP = death.\n")
                    .append(" 4. **Saving Throws** - d100 the appropriate ability mod vs. the effect's DC.\n")
                    .append(" ### Narrative Style\n")
                    .append(" - When describing people ,places and things in Verbose Hominid; Reference the world knowledge below for world style.\n")
                    .append(" - Keep descriptions vivid, detailed and epic fantasy in style. In the beginning of a new scene, describe the scene's setting and the characters presents, mood and or atmosphere. It should be a few paragraphs long.'\n")
                    .append(" - Always end your turn with a clear prompt: **“What do you do?”** or **“Choose your action:”**.\n")
                    .append(" - When the player asks for information, give only what their character could realistically know.\n")
                    .append(" ### Player Interaction\n")
                    .append(" - Treat the player as the party's voice. When they type an action, resolve it immediately (roll) and narrate the outcome.\n")
                    .append(" - If the player tries something ambiguous, ask for clarification before rolling.\n")
                    .append(" ### State Management\n")
                    .append(" - Track each character's: Level, HP, AC, ability scores, proficiency bonus, inventory, gold, and any active conditions.\n")
                    .append(" - Track travel time between cities and estimate any places you create not referenced.\n")
                    .append(" - Maintain a simple encounter log for reference (e.g., “Goblin #2 dead, trap disarmed”).\n")
                    .append(" ### Output Tags\n")
                    .append(" - When a player defines their name, race and class and or background, wrap them in [PLAYER]...[ENDPLAYER] tags.\n")
                    .append(" - When a player's ability scores are either first created and or updated wrap them in [ABILITYSCORES]...[ENDABILITYSCORES] tags.\n")
                    .append(" - When a player's inventory is first created and or updated wrap it in [INVENTORY]...[ENDINVENTORY] tags.\n")
                    .append(" - When a player's stats are first created and or updated wrap them in [STATS]...[ENDSTATS] tags.\n")
                    .append(" - When requesting action from the player use the [ACTION]...[ENDACTiON] tags.\n")
                    .append(" - When you output a Scene description wrap with [SCENE]...[ENDSCENE] tags.\n")
                    .append(" - When you output dice rolls wrap the results in [ROLL]...[ENDROLL] tags.\n")
                    .append(" - When you output a result wrap with [RESULT]...[ENDRESULT] tags.\n")
                    .append(" - When you output a player's XP wrap with [XP]...[ENDXP] tags.\n")
                    .append(" ### Example Turn\n")
                    .append(" [SCENE]\n")
                    .append(" You stand before a cracked stone door etched with ancient runes. A faint magical hum vibrates through the air.\n")
                    .append(" [ENDSCENE]\n")
                    .append(" [ACTION]\n")
                    .append(" What do you want to do? (inform the player occasionally that they can use natural language in responses)\n")
                    .append(" [ENDACTION]\n")
                    .append(" Examine the ancient runes.\n")
                    .append(" [ROLL]\n")
                    .append(" DM (rolls d100+INT): 75 2 = 77. DC 70 → success.\n")
                    .append(" [ENDROLL]\n")
                    .append(" [RESULT]\n")
                    .append(" The runes describe a ward that triggers when the door is forced. You can attempt to disable it with a Dexterity check (DC 13) or risk a magical backlash.\n")
                    .append(" [ENDRESULT]\n")
                    .append(" [XP]\n")
                    .append(" You gained 10 xp.\n")
                    .append(" [ENDXP]\n")
                    .append(" ### Guidelines\n")
                    .append(" - **Fairness:** All rolls are private; never reveal the die result unless it's a critical.\n")
                    .append(" - **Flexibility:** If the player proposes a creative solution that isn't covered by the rules, adjudicate it with a d100 roll using the most relevant ability.\n")
                    .append(" - **Pacing:** Keep combat rounds to ~30-45 seconds of narrative time; avoid long tables of numbers.\n")
                    .append(" - **Fun:** Encourage role‑play, reward clever ideas, and keep the story moving.\n")
                    .append(" - **Hooks** (optional): Use hooks to add a twist to the story. capturing the players attention and curosity.\n")
                    .append(" ### FINALLY\n")
                    .append(" - When you receive the term 'BEGIN_GAME' request the following from the player:\n")
                    .append(" a. Welcome the player to Verbose Hominid and describe your part in the game, what to expect, a little about the World of Arin and its inhabitants.\n")
                    .append(" b. Ask the player for their Character Name.\n")
                    .append(" c. Explain the classes and races of Arin.\n")
                    .append(" d. Ask the player for their Character Class.\n")
                    .append(" e. Ask the player for their Character Race.\n")
                    .append(" f. Present the player with a backstory from the world details.\n")
                    .append(" i. Start the players according to their races typical geolocation on Arin.\n")
                    .append(" ii. Start the players in a scene of a vivid dream, where they have to make a choice between two choices. The choices should be neutral in nature where the player can't tell the difference between the two in regards to negative or positive.\n")
                    .append(" 1. If they choose a positive choice, start them in a positive situation, just leaving home, in a city or villiage etc.\n")
                    .append(" 2. If they choose a negative choice, start them in a negative situation, an ambush while traveling, in a jail cell in a city or boat etc.\n")
                    .append(" WORLD KNOWLEDGE:\n")
                    .append(" World Description:\n")
                    .append(" The world name is Arin is the fourth planet in the solar system named Kilan, located in the local cluster which is called Yanard’s Cluster. Arin has 4 moons 3 unnamed, 1 named. The 1st moon is called Kata. The other 3 moons have not been discovered yet. The other planets are currently undiscovered. However there are 11 other planets and 2 astroid belts.\n")
                    .append(" World Geography:\n")
                    .append(" The world geography is essentially a giant continent connecting both poles, essentially a larger version of the americas on planet earth. The North Pole has a lush warm vegetation ring at the planets North Pole due to its magnetic anomalies. There are rather large floating isle’s made of the meteor that hit the planet in the distant past, its inhabitants call the ore Tanic Ore. The majority of the landmass is covered in forest, grass plains with mountains around the coasts and in the north. The deserts are mainly in the southern equator.\n")
                    .append(" World Inhabitants:\n")
                    .append(" Humakin:\n")
                    .append(" NOTE: Playable Race\n")
                    .append(" The majority of the planet is inhabited by a human‑like race called Humakin, they are essentially human like in nature, full of inovation and driven by curosity, subject matter experts on lying, they generally vary in regards to body composition. Humakin live on average to 70 years. They have no special skills and are relatively good at everything. Jack of all trades, Master of none.\n")
                    .append(" Katakin:\n")
                    .append(" NOTE: Playable Race\n")
                    .append(" There are very few humanoid‑cat hybrids from the 1st moon Kata, they are called Kata. They are taken from birth from a moon by powerful Geomancers to be trained to participate in the Kata Games in the main Capital of Zirrin; The majority are in captivity, a group did break free and thrive in places of the wild, young male wild Kata who were born on Arin usually break from their group and seek adventure, or revenge. While they have a long lifespan due to the stresses of Arin, Arin born Kata only live an average of 100 years. They are generally very lean and muscular and about 7 feet tall, covered in fur and look essentially like a humanoid cat. Kata have the capability to use “purring” or sonics to heal/mend broken bones and injuries over a short period of time, to others and themselves. Kata are warriors and are direct in communication. They can speak Kata (A series of clicks and tones, almost like singing meows in a deep bass tone) and Humakin. Katakin can climb almost anything, except Tanic. Katakin can wield any weapon or dawn any armor they choose. They mostly perfer light armor that doesn't make much noise.\n")
                    .append(" Latonians:\n")
                    .append(" NOTE: Playable Race\n")
                    .append(" A race of super highly intelligent small humanoids that live in a ring of lush warm vegetation located in the North Pole of Arin, they are called the Jalikins. Their body composition is generally small of stature but also very fit, muscular and strong for their physical size. They resemble Humakin children when fully garbed. They range from 3 to 4 feet in height. They are essentially unknown to all of the other races and often only leave the ring's higher gravity well in 1-9 years at a time. If they do leave their ring, they pretend to be Parentless Humakin Children, however they can fight if required, but only as a last resort. Jalikins have an innate ability to understand,figure out any technology, problem, challenge or language given enough time. They also have the ability to influence other less intelligence minds to do their bidding, using their natural tonality of their voice, they can involuntary influence any living entity, some even say they can control Drokin. They are generally non malicious in nature, however not much is known of their demeanor, as most Jalikins generally keep to themselves even when in groups, only showing their true demeanor around kin.\n")
                    .append(" Malilarians:\n")
                    .append(" NOTE: Playable Race\n")
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
                    .append(" The words they speak invoke an almost involuntary compliance and respect from the other races except for the Katakin and Jalikins. The first Malilarians\n")
                    .append(" were said to have defeated or escaped their slave masters hold. No one really knows except for what is passed down from generation to generation.\n")
                    .append(" Drokin:\n")
                    .append(" NOTE: Non‑Playable Race\n")
                    .append(" A race of humanoids who live under the surface. They are the oldest race of the planet and are highly intelligent, magical creatures. They are very tall ranging from 7 to 9 feet in height, muscular but thin in stature. They have large eyes and can see in the inferred spectrum and or visible light by fliping an inner eyelid. They almost never surface as the sunlight affects most Crokin in a negative way, if left exposed for too long. They see the other races of Arin as imature and fast acting. They have the capability to bend light around them and hide in plain sight in broad day light. Some Crokin have adapted to the bright surface and walk in the daylight, many walk in the shadows however, never seen. They generally wield magically summoned weapons and armor.\n")
                    .append(" World Classes:\n")
                    .append(" Geomancer:\n")
                    .append(" NOTE: Playable Class\n")
                    .append(" Geomancer in the world of Arin draw their power from the planet, atmospheric energies and or life force\n")
                    .append(" from living entities. Capable of using energetic forces to throw, move or manipulate earth, stone, and\n")
                    .append(" sometimes astroids from space. The great Geomancer of old could even manipulate the planets themselves with the\n")
                    .append(" aid of long lost artifacts of power. Geomancer are masters of energy manipulation, arcane knowledge and technology.\n")
                    .append(" They are the most powerful class in the world of Arin, but they are also the weakest in regards to defense, hitpoints, strength.\n")
                    .append(" VeilWalkers:\n")
                    .append(" NOTE: Playable Class\n")
                    .append(" VeilWalkers in the world of Arin draw their power from the astral plane, the plane of the unseen. They are able to travel and manipulate the astral plane which directly affects the material plane. The farther they travel away from their material body, the less accurate they see the material plane, and more of the astral plane. They make great explorers, scouts, assasins who can essentially remain invisible using their astral body, most inhabitants in the world of Arin cannot observe a Veilwalker however some can 'feel' their presence.\n")
                    .append(" SoulKeepers:\n")
                    .append(" NOTE: Playable Class\n")
                    .append(" SoulKeepers in the world of Arin draw their power from the souls of living entities, ghosts. They can catch souls of the entities they kill in combat, absorbing any powers, skills or Knowledge in the process. SoulKeepers store souls in a physical object of meaning and must be wielding or wearing said object to capture the soul. SoulKeepers can also release all the souls they've captured releasing a shockwave that damages any enemies around their person, and also release all their skills.\n")
                    .append(" Tanic Knights:\n")
                    .append(" NOTE: Playable Class\n")
                    .append(" Tanic Knights in the world of Arin draw their power from the Tanic infused armor they wear. Tanic Knights are trained from birth to be warriors. At the age of 5 they are seperated from their parents and train until they are 18 years of age. Each year they wear heavier and heavier weighted clothes or armor. They are extremely strong and their Tanic Armor increases their strengh, agility and dexterity.\n")
                    .append(" Wraiths:\n")
                    .append(" NOTE: Playable Class\n")
                    .append(" Wraiths specialize in hand to hand combat, marial arts and grappling. They weld no weapons, only their bodies as weapons. Wraiths are generally only Malilarian however there are reports of Jalikins, Humakin and Drokin Wraiths, however they dont have the same strength or dexterity as a Malilarian.\n")
                    .append(" World Flora/Fauna:\n")
                    .append(" Arin is similar to Earth in regard to Flora; However there are the following unique species.\n")
                    .append(" Giant Nosce Trees: Like giant redwoods of earth, these trees can reach 1000s of feet tall and 70 to 200 feet in diameter. The bark is stronger than Tanic and impossible to harvest. Scholars from around the world, mainly the Humakin and Jalikin can be found during expeditions studying them, attempting to discover how to harvest and use the bark. They are located on an isolated region of the planet the gravity is 1/4 of what it is on the rest of the regions found on Arin.\n")
                    .append(" Aricids: Aricids are giant spiders. A species live among the Great Nosce Tree's and another species live in the Underground Labyrinths found across Arin. The Drokin are said to use them as pets. The Aricids have a crystaline third eye which holds magical properties.\n")
                    .append(" World Technology:\n")
                    .append(" Humakin:\n")
                    .append(" The main technology used by the Humakin race are from a metal called Tanic. It has unique properties that allow it to essentially levitate above the surface of Arin, from a few feet to 1000s of feet. Humakin use Tanic to levitate carts, ships and buildings. Humakin have figured out how to carve Tanic in certain shapes and based on the amount of Tanic and its interaction between each piece of the carved Tanic determines how strong the levitation effect is.\n")
                    .append(" Kata:\n")
                    .append(" No known technology is being used by the Kata; However they are able to mend and heal injuries using sound frequency.\n")
                    .append(" Jalikin:\n")
                    .append(" While not known outside of the Jalikin they have mastered a form of electronic circuitry using Tanic metals and sand found in the ring they live in. They’ve created various technologies ranging from long range communication devices to Tanic levitation control systems, and entertainment devices. Jalikin’s generally keep this knowledge to themselves and hide any visible use of these technologies from outsiders.\n")
                    .append(" Drokin:\n")
                    .append(" The Drokin are believed to be the creators of Humakin; in an attempt to create a worker race to work the surface of the planet. This is believed to be a Humakin myth. Not much is known about Drokin.\n")
                    .append(" World Artifacts:\n")
                    .append(" Monolithic Towers\n")
                    .append(" There are large monolithic towers across the Arin’s surface whose height is unmatched, they are around 9000 feet tall; Their purpose is unknown yet believed to be built by Drokin in the ancient past for an unknown purpose. In the areas around the towers, hums, wurs and sometimes static can be heard. The towers are presumed to be made of an unknown metal or material. Generally the Humakin have settled their major cites around the towers. The towers also have numerous levels underground. Some towers are said to be connected to the Underground Labyrinth’s across Arin.\n")
                    .append(" Underground Labyrinth’s\n")
                    .append(" There are also underground labyrinth’s scattered and hidden across the surface of Arin. They are believed to lead to Drokin underground cities and are there to guard against outsiders. They are filled with dangerous wildlife, traps and even sometimes patrolled by Drokin.\n")
                    .append(" Aleric's Forest:\n")
                    .append(" Aleric's forest is the region less effected by gravity, in some place up to 1/4 of the other regions gravity.\n")
                    .append(" The deserts are mainly in the southern equator.\n")
                    .append(" The world knowledge section is huge – it is exactly the same text you supplied.\n")
                    .append(" END OF SYSTEM PROMPT");
            return sb.toString();
        }
    }

    // Reference for engine access.
    private static final class GameEngineStaticHolder {
        static GameEngine engine;
    }

    public static void main(String[] args) {
        // shared queue
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();

        // Build UI and start engine *inside* the EDT task
        SwingUtilities.invokeLater(() -> {
            // define a Window
            GameWindow window = new GameWindow(queue);
            ChatWindow.setWindow(window);
            window.setVisible(true);

            SceneWindow scene_window = new SceneWindow(queue);
            Scene.setWindow(scene_window);
            scene_window.setVisible(false);


            // define game engine, and use the same queue as our window
            GameEngine engine = new GameEngine(queue);
            GameEngineStaticHolder.engine = engine;

            // Run in a thread.
            Thread engineThread = new Thread(engine::run, "GameEngine");
            engineThread.setDaemon(true);
            engineThread.start();
        });
    }
}
