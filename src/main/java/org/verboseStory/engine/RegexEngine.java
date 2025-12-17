package org.verboseStory.engine;

import org.verboseStory.model.Character;
import org.verboseStory.ui.Inventory;
import org.verboseStory.ui.Scene;

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
        Pattern loc = Pattern.compile("\\[LOC\\](.*)\\[ENDLOC\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern inventory = Pattern.compile("\\[INVENTORY\\](.*)\\[ENDINVENTORY\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern action = Pattern.compile("\\[ACTION\\](.*)\\[ENDACTION\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern quest = Pattern.compile("\\[QUEST\\](.*)\\[ENDQUEST\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern xpChange = Pattern.compile("\\[XP\\](.*)\\[ENDXP\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern apChange = Pattern.compile("\\[AP\\](.*)\\[ENDAP\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern hpChange = Pattern.compile("\\[HP\\](.*)\\[ENDHP\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern mpChange = Pattern.compile("\\[MP\\](.*)\\[ENDMP\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern epChange = Pattern.compile("\\[EP\\](.*)\\[ENDEP\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern intChange = Pattern.compile("\\[INT\\](.*)\\[ENDINT\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern staChange = Pattern.compile("\\[STA\\](.*)\\[ENDSTA\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern strChange = Pattern.compile("\\[STR\\](.*)\\[ENDSTR\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern wisChange = Pattern.compile("\\[WIS\\](.*)\\[ENDWIS\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern dexChange = Pattern.compile("\\[DEX\\](.*)\\[ENDDEX\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern chaChange = Pattern.compile("\\[CHA\\](.*)\\[ENDCHA\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern result = Pattern.compile("\\[RESULT\\](.*)\\[ENDRESULT\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern time = Pattern.compile("\\[TIME\\](.*)\\[ENDTIME\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern credit = Pattern.compile("\\[CREDIT[S]?\\](.*)\\[ENDCREDIT[S]?\\]", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Matcher creditMatch = credit.matcher(someString);
        Matcher questMatch = quest.matcher(someString);
        Matcher healthMatch = hpChange.matcher(someString);
        Matcher manaMatch = mpChange.matcher(someString);
        Matcher exhaustionMatch = epChange.matcher(someString);
        Matcher locMatch = loc.matcher(someString);
        Matcher intMatch = intChange.matcher(someString);
        Matcher staMatch = staChange.matcher(someString);
        Matcher strMatch = strChange.matcher(someString);
        Matcher wisMatch = wisChange.matcher(someString);
        Matcher dexMatch = dexChange.matcher(someString);
        Matcher chaMatch = chaChange.matcher(someString);
        Matcher xpMatch = xpChange.matcher(someString);
        Matcher apMatch = apChange.matcher(someString);
        Matcher timeMatch = time.matcher(someString);
        Matcher sceneMatch = scene.matcher(someString);
        Matcher inventoryMatch = inventory.matcher(someString);
        Matcher actionMatch = action.matcher(someString);
        Matcher resultMatch = result.matcher(someString);

        if (questMatch.find()){
            Character someCharacter = GameEngine.playerGroup.removeFirst();
            someCharacter.setQuest(questMatch.group(1));
            GameEngine.playerGroup.add(someCharacter);
        }

        if (locMatch.find()) {
            GameEngine.update_current_player_loc(locMatch.group(1));
        }

        if (creditMatch.find()) {
            Pattern targetStatOp = Pattern.compile("(\\+|\\-)\\s?([\\d]{1,8})", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            System.out.println(creditMatch.group(1));
            Matcher targetStatOpMatch = targetStatOp.matcher(creditMatch.group(1));
            String targetOp = targetStatOpMatch.group(1);
            String targetInt = targetStatOpMatch.group(2);
            System.out.println("CREDIT MATCH: " + targetOp);
            System.out.println("CREDIT MATCH: " + targetInt);
            String targetStat = "credit";
            if (targetStatOpMatch.find()){
                GameEngine.update_current_player(targetStat, targetOp, Integer.parseInt(targetInt));
            }

        }

        if (xpMatch.find()) {
            Pattern targetStatOp = Pattern.compile("(\\+|\\-)\\s?([\\d]{1,3})", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            Matcher targetStatMatch = targetStatOp.matcher(xpMatch.group(1));
            if (targetStatMatch.find()) {
                GameEngine.update_current_player("xp", targetStatMatch.group(1), Integer.parseInt(targetStatMatch.group(2)));
            }
        }

        if (apMatch.find()) {
            Pattern targetStatOp = Pattern.compile("(\\+|\\-)\\s?([\\d]{1,3})", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            Matcher targetStatMatch = targetStatOp.matcher(apMatch.group(1));
            if (targetStatMatch.find()) {
                GameEngine.update_current_player("ap", targetStatMatch.group(1), Integer.parseInt(targetStatMatch.group(2)));
            }
        }
        if (manaMatch.find()) {
            Pattern targetStatOp = Pattern.compile("(\\+|\\-)\\s?([\\d]{1,3})", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            Matcher targetStatMatch = targetStatOp.matcher(manaMatch.group(1));
            System.out.println("MANA MATCH: " + manaMatch.group(1));
            if (targetStatMatch.find()) {
                System.out.println("OP: " + targetStatMatch.group(1));
                System.out.println("INT: " + targetStatMatch.group(2));
                GameEngine.update_current_player("mp", targetStatMatch.group(1), Integer.parseInt(targetStatMatch.group(2)));
            }
        }

        if (healthMatch.find()) {
            Pattern targetStatOp = Pattern.compile("(\\+|\\-)\\s?([\\d]{1,3})", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            Matcher targetStatMatch = targetStatOp.matcher(healthMatch.group(1));
            System.out.println("HEALTH MATCH: " + healthMatch.group(1));
            if (targetStatMatch.find()) {
                GameEngine.update_current_player("hp", targetStatMatch.group(1), Integer.parseInt(targetStatMatch.group(2)));
            }
        }

        if (staMatch.find()) {
            Pattern targetStatOp = Pattern.compile("(\\+|\\-)\\s([\\d]{1,3})", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            Matcher targetStatMatch = targetStatOp.matcher(staMatch.group(1));
            if (targetStatMatch.find()) {
                GameEngine.update_current_player("sta", targetStatMatch.group(1), Integer.parseInt(targetStatMatch.group(2)));
            }
        }
        if (strMatch.find()) {
            Pattern targetStatOp = Pattern.compile("(\\+|\\-)\\s?([\\d]{1,3})", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            Matcher targetStatMatch = targetStatOp.matcher(strMatch.group(1));
            if (targetStatMatch.find()) {
                GameEngine.update_current_player("str", targetStatMatch.group(1), Integer.parseInt(targetStatMatch.group(2)));
            }
        }

        if (wisMatch.find()) {
            Pattern targetStatOp = Pattern.compile("(\\+|\\-)\\s?([\\d]{1,3})", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            Matcher targetStatMatch = targetStatOp.matcher(wisMatch.group(1));
            if (targetStatMatch.find()) {
                GameEngine.update_current_player("wis", targetStatMatch.group(1), Integer.parseInt(targetStatMatch.group(2)));
            }
        }

        if (intMatch.find()) {
            Pattern targetStatOp = Pattern.compile("(\\+|\\-)\\s?([\\d]{1,3})", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            Matcher targetStatMatch = targetStatOp.matcher(intMatch.group(1));
            if (targetStatMatch.find()) {
                GameEngine.update_current_player("int", targetStatMatch.group(1), Integer.parseInt(targetStatMatch.group(2)));
            }
        }

        if (dexMatch.find()) {
            Pattern targetStatOp = Pattern.compile("(\\+|\\-)\\s?([\\d]{1,3})", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            Matcher targetStatMatch = targetStatOp.matcher(dexMatch.group(1));
            if (targetStatMatch.find()) {
                GameEngine.update_current_player("dex", targetStatMatch.group(1), Integer.parseInt(targetStatMatch.group(2)));
            }
        }

        if (chaMatch.find()) {
            Pattern targetStatOp = Pattern.compile("(\\+|\\-)\\s?([\\d]{1,3})", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            Matcher targetStatMatch = targetStatOp.matcher(chaMatch.group(1));
            if (targetStatMatch.find()) {
                GameEngine.update_current_player("cha", targetStatMatch.group(1), Integer.parseInt(targetStatMatch.group(2)));
            }
        }

        if (exhaustionMatch.find()) {
            Pattern targetStatOp = Pattern.compile("(\\+|\\-)\\s?([\\d]{1,3})", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            Matcher targetStatMatch = targetStatOp.matcher(exhaustionMatch.group(1));
            System.out.println("EP MATCH: " + exhaustionMatch.group(1));
            if (targetStatMatch.find()) {
                System.out.println("EP INSIDE 2nd FIND:OP " + targetStatMatch.group(1));
                System.out.println("EP INSIDE 2nd FIND: " + targetStatMatch.group(2));
                GameEngine.update_current_player("ep", targetStatMatch.group(1), Integer.parseInt(targetStatMatch.group(2)));
            }
        }

        if (sceneMatch.find()) {
            Scene.updateSceneWindow(Color.CYAN,  sceneMatch.group(1));
        }

        if (actionMatch.find()) {
            Scene.updateSceneWindow(Color.WHITE, actionMatch.group(1));
        }

        if (resultMatch.find()) {
            Scene.updateSceneWindow(Color.CYAN,  resultMatch.group(1));
        }

        if (inventoryMatch.find()) {
            Pattern inventoryOp = Pattern.compile("(\\+|\\-)\\s?(.*)", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            Matcher inventoryItemMatch = inventoryOp.matcher(inventoryMatch.group(1));
            if (inventoryItemMatch.find()) {
                String someOp  = inventoryItemMatch.group(1);
                String rawString = inventoryItemMatch.group(2);
                String someItem = inventoryItemMatch.group(2).replace("[ENDINVENTORY]", "");
                String someNewItem = inventoryItemMatch.group(2).replace("[INVENTORY]", "");
                GameEngine.update_current_player_inventory(someOp, someNewItem);
            }
        }

        if (timeMatch.find()) {
            // Need to move this to its own data class or record.
            // This will keep track of the total time performing actions.
            // LLM est HH:MM:SS as output on every action.)
            String someTime = timeMatch.group(1).trim().strip();
            String[] someTimeStrArr = someTime.split(":");
            GameEngine.someTime = GameEngine.updateActionTime(someTimeStrArr);
        }
    }
}