package org.verboseStory.model;
// Chad V.
//[ Class  | ---------Professor | ---------------------------------------Assignment Title ]
//[ CSC203 | ------------Prof H | ----------------Object Oriented Programming Inheritance ]
//[ Student Child Class of Character | -------------------------------------- Requirement ]
import org.verboseStory.engine.GameEngine;
import org.verboseStory.engine.GameEngineStaticHolder;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

/**
 * This is my version of a "person" class.
 *  The Character class is the base of all other "Character" classes, Professor, Student, Geomancer,
 *  wraith, Tanic Knight, etc.
 *  Note: some variables are defined as public for class assignment
 *
 *  Character someCharacter = new Character();
 *  someCharacter.firstName = "Dora"; || *.setFirstName("Dora); || String aName = *.getFirstName()
 *  someCharacter.lastName = "Verbose";
 *  someCharacter.someTitle = "NPC";
 *  someCharacter.someCharacterClass = "NPC";
 */

//Character class.
public class Character {
    //String firstname, lastname, title and location,
    public String firstName;
    public String lastName;
    private String someTitle;
    public String someRace;
    public String someCharacterClass;
    public String background;
    private String location;
    private String currentQuest;
    private int hitPoints;
    private int manaPoints;
    private int actionPoints;
    private int exhaustionPoints;
    private int strength;
    private int stamina;
    private int dexterity;
    private int intelligence;
    private int wisdom;
    private int charisma;
    private int creditWallet;


    private enum Demeanor {
        CALM,
        FRIENDLY,
        RESERVED,
        CONFIDENT,
        EASYGOING,
        CHEERFUL,
        STOIC,
        CURIOUS,
        ENERGETIC,
        INTROVERTED,
        OUTGOING,
        COMPASSIONATE,
        IMPULSIVE,
        AGGRESSIVE,
        PESSIMISTIC,
        MOODY,
        ANXIOUS,
        INDIFFERENT,
        SARCASTIC,
        RUDE,
        RAGE,
    };

    private List someInventory;

    // Character State
    private enum Posture {PRONE, CRAWL, CROUCH, KNEELING, SITTING, STANDING};
    private enum Locomotion {SNEAK, WALKING, MARCHING, JOGGING, RUNNING, SPRINTING};
    //default character states
    public Demeanor currentDemeanor = Demeanor.CALM;
    public Posture currentPosture = Posture.STANDING;
    public Locomotion currentLocomotion = Locomotion.WALKING;


    /**
     *Getters/Setters to access private var
     * setFirstName("Aleric")
     * String charFirstName = getFirstName();
     * output: "Aleric"
     * */
    public Character(String firstName, String lastName, String someBackground, String someRace, String someCharacterClass) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.background = someBackground;
        this.someRace = someRace;
        this.someCharacterClass = someCharacterClass;
        this.setTitle(someCharacterClass.toUpperCase());
        this.setCreditWallet(150);
        this.setActionPoints(150);
        this.setExhaustionPoints(5);
        this.someInventory = new List();
        this.appendInventory("SH13 Visitor Pass");
    }
    //firstname
    //setter
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }
    //getter
    public String getFirstName(){
        return firstName;
    }
    //lastName
    //setter
    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    //getter
    public String getLastName(){
        return lastName;
    }
    //location
    //setter
    public void setLocation(String location){
        this.location = location;
    }

    //getter
    public String getLocation(){
        return location;
    }

    //setter
    public void setRace(String race){
        this.someRace = race;
    }
    //race
    //getter
    public String getRace(){
        return someRace;
    }

    //setter
    public void setCharacterClass(String className){
        this.someCharacterClass = className;
    }
    // character class
    //getter
    public String getCharacterClass(){
        return someCharacterClass;
    }
    // character background
    //setter
    public void setBackground(String background){
        this.background = background;
    }

    //getter
    public String getBackground() {
        return background;
    }
    // General Demeanor of the professor
    //setter
    public void setDemeanor(Demeanor demeanor) {
        this.currentDemeanor = demeanor;
    }
    //getter
    public String getDemeanor() {
        return currentDemeanor.toString();
    }

    // character title
    /**Titles will be used by the AI to role-play a character if need be.
     * .. String someTitle = someCharacter.getTitle();
     * .. someCharacter.setTitle("JEDI")
     * */
    //getter
    public String getTitle(){
        return someTitle;
    }
    //setter
    public void setTitle(String title){
        this.someTitle = title;
    }
    // character hp
    /**Hit Points will be used by the AI to determine the health of a character.
     * .. int hp = someCharacter.getHitPoints();
     * .. someCharacter.setHitPoints(10)
     * */
    //get hp
    public int getHitPoints(){
        return hitPoints;
    }
    //define hp
    public void setHitPoints(int hitPoints){
        this.hitPoints = hitPoints;
    }
    // character MP
    /**Mana Points will be used by the AI to restrict spell usage.
     * .. int mp = someCharacter.getManaPoints();
     * .. someCharacter.setManaPoints(100)
     * */
    public void setManaPoints(int manaPoints){
        this.manaPoints = manaPoints;
    }

    public int getManaPoints(){
        return manaPoints;
    }

    //exhaustionPoints
    /**Exhaustion Points will be used by the AI to restrict Character movement, .
     * .. int mp = someCharacter.getManaPoints();
     * .. someCharacter.setManaPoints(100)
     * */
    public void setExhaustionPoints(int exhaustionPoints){
        this.exhaustionPoints = exhaustionPoints;
    }

    public int getExhaustionPoints(){
        return exhaustionPoints;
    }


    /**
     * ActionPoints: action points are essentially the number of turns/actions a character can
     * take before needing to rest.
     * */
//    actionPoints;
    public void setActionPoints(int actionPoints){
        this.actionPoints = actionPoints;
    }

    public int getActionPoints(){
        return actionPoints;
    }
    /**
     * Character Stats determine what the character can and can't do.
     * */
//    strength;
    public void setStrength(int strength){
        this.strength = strength;
    }

    public int getStrength(){
        return strength;
    }
//    stamina;
    public void setStamina(int stamina){
        this.stamina = stamina;
    }

    public int getStamina(){
        return stamina;
    }
//    dexterity;
    public void setDexterity(int dexterity){
        this.dexterity = dexterity;
    }

    public int getDexterity(){
        return dexterity;
    }
//    intelligence;
    public void setIntelligence(int intelligence){
        this.intelligence = intelligence;
    }

    public int getIntelligence(){
        return intelligence;
    }
//    wisdom;
    public void setWisdom(int wisdom){
        this.wisdom = wisdom;
    }

    public int getWisdom(){
        return wisdom;
    }
//    charisma;
    public void setCharisma(int charisma){
        this.charisma = charisma;
    }

    public int getCharisma(){
        return charisma;
    }

    //posture
    /**
     * Posture and Locomotion: The characters posture and type of movement.
     *
     * Posture somePosture = getCurrentPosture()
     * somePosture.setPosture(Posture.SITTING)
     *
     * */
    public void setPosture( Posture newPosture){
        this.currentPosture = newPosture;
    }

    public Posture getCurrentPosture(){
        return currentPosture;
    }

    public void setLocomotion(Locomotion newLocomotion){
        currentLocomotion = newLocomotion;
    }

    public Locomotion getCurrentLocomotion(){
        return currentLocomotion;
    }
    //Inventory
    /**
     * An Inventory for the character. String for now, but will be a record in the future.
     * #FUTUREME: Define an Item Record class.
     *
     * Append an item to the inventory
     * someInventory.append("Necklace of the Vertonal")
     *
     * Get the inventory:
     * List someInventory = getInventory()
     *
     * Get an Item.
     * String someItem = someInventory[0];
     * */

    public void appendInventory(String someItem){
        someInventory.add(someItem);
    }

    public List getInventory(){
        return someInventory;
    }

    /**
     * Character "Credit Wallet", so the character can store earned credits.
     * */

    public void setCreditWallet(int creditWallet){
        this.creditWallet = creditWallet;
    }

    public int getCreditWallet(){
        return creditWallet;
    }

    /**
     * Character Actions; These are default character "actions" that a player has access to directly
     * talk, sleep, sit, stand, walk, run, jog, e
     * */

    public void talk(){
        StringBuilder msg = new StringBuilder();
        msg.append(someTitle + " " + firstName + " " + lastName + " Hello");
        GameEngine.printOutput(Color.GREEN, "CHARACTER", msg.toString());
    }

    public void rest(int hours){
        StringBuilder msg = new StringBuilder();
        msg.append(someTitle + " " + firstName + " " + lastName + " began sleeping")
                .append("and is planning on sleeping for " + hours + " hours.");
        GameEngine.printOutput(Color.GREEN, "Character", msg.toString());

    }

    public void sit(){
        setPosture(
                Posture.SITTING
        );
    }

    public void stand(){
        setPosture(
                Posture.STANDING
        );
    }

    public void startJogging(){
        if (currentPosture != Posture.STANDING) {
            setPosture(
                    Posture.STANDING
            );
        }
        setLocomotion(
                Locomotion.JOGGING
        );
    }

    public void startRunning(){
        setLocomotion(
                Locomotion.RUNNING
        );
    }

    // eating
    /**
     * All Characters need to eat :).
     * Placeholder for now, just prints a string that is passed to it.
     * */
    public void eat(String someFood){
        StringBuilder msg = new StringBuilder();
        msg.append(someTitle + " " + firstName + " " + lastName + " is Eating " + someFood);
        GameEngine.printOutput(Color.GREEN, "CHARACTER", msg.toString());
        //add food calories to digestion
    }

    // Not sure I need this, as I"ll already have a reference; Though I thought I could use it
    // in the future to automatically pass updates through the queue, for the character sheet.
    // update the engines input queue with a character message
    public void updateEngine(String someUpdateMsg){
        BlockingQueue<String> anInputQueue = GameEngineStaticHolder.engine.inputQueue;
        if(!someUpdateMsg.isEmpty()){
            anInputQueue.offer(someUpdateMsg);
        }
    }
}
