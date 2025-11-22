package org.verboseStory.engine;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the output that comes from the LLM and forwards
 * the relevant pieces to the UI.
 *
 * ToDo:
 * implement a parseInput() method.
 */
public final class RegexEngine {

    private RegexEngine() {}

    public static void parseOutput(String someString) {
        // Regex patterns (pre‑compiled, reusable)
        Pattern scene = Pattern.compile("\\[SCENE\\](.*)\\[ENDSCENE\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern abilityScores = Pattern.compile("\\[ABILITYSCORES\\](.*)\\[ENDABILITYSCORES\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern inventory = Pattern.compile("\\[INVENTORY\\](.*)\\[ENDINVENTORY\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern stats = Pattern.compile("\\[STATS\\](.*)\\[ENDSTATS\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern action = Pattern.compile("\\[ACTION\\](.*)\\[ENDACTION\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern roll = Pattern.compile("\\[ROLL\\](.*)\\[ENDROLL\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern result = Pattern.compile("\\[RESULT\\](.*)\\[ENDRESULT\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern experience = Pattern.compile("\\[XP\\](.*)\\[ENDXP\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern player = Pattern.compile("\\[PLAYER\\](.*)\\[ENDPLAYER\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
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
            GameEngine.printOutput(Color.CYAN, "REGEXENGINE", sceneMatch.group(1));
        }

        if (inventoryMatch.find()) {
            GameEngine.printOutput(Color.YELLOW, "REGEXENGINE", inventoryMatch.group(1));
        }
    }
}