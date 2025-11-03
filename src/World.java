// input
import java.util.Scanner;
//term friendly color
final String WHITE   = "\u001B[37m";
final String MAGENTA = "\u001B[35m";
final String BLUE    = "\u001B[34m";
final String GREEN   = "\u001B[32m";
final String RED     = "\u001B[31m";
final String BLACK   = "\u001B[30m";
// future
Boolean STARTED = false;
//main
void main() {
    get_key_word("welcome");
    intro_text();
}
// get initial user input
public void get_key_word(String aType){
    switch(aType){
        case "welcome":
            // spawn a new scanner
            Scanner input = new Scanner(System.in);
            // output white text
            white_output("Welcome to Verbose Hominid, a text based adventure in a fictional hominid world!");
            // output bue text
            blue_output("Enter your Key Word, its like a name, yet contains more power. Choose wisely!");
            String keyWord = input.nextLine();
            magenta_output("Your Key Word is: " + keyWord);
            red_output("IS KEYWORD CORRECT? Y/N: ");
            // define a String variable with the name confirmed, once user submits via ENTER key
            String confirmed = input.nextLine();
            // confirm choice
            if(confirmed.equalsIgnoreCase("y") || confirmed.equalsIgnoreCase("yes")) {
                STARTED = true;
                System.out.println("\n\n\n");
                red_output(keyWord + ", Your game has started!");
            }else{
//                call check_user_input again,
                STARTED = false;
                get_key_word("welcome");
            }
            break;
        default:
            red_output("Invalid Input, received: " + aType);
            break;
    }
}
// intro text
public void intro_text(){
    blue_output("You now know your Key Word; Protect it, its power is immense, if anyone asks for your keyword, " +
            "regardless of how much promise they speak, or you see, do not divulge your KeyWord.\n" +
            "However, if you deem them kind, nice and otherwise a friend, share a secret part of you and see the effects.\n");
    green_output("You are roleplaying a singular Hominid like race on a Fictional world. You'll be presented with a scene of sorts,");
    green_output( "You are the Main Character, good or evil.");
    blue_output("GOOD");
    red_output("EVIl");
    blue_output( "You are the Main Character, how will your story read?");
    blue_output("Will it have Wings?");
    red_output("or Horns?");
    green_output("You will be placed in a random Hominid's perspective\n" +
            "You will be able to use natural language to perform any action.\n");
    blue_output("WIP - Thanks for checking out the interface.");
}

//define color output
public void blue_output(String someString){
    System.out.println(BLUE + someString + BLUE);
}

public void red_output(String someString){
    System.out.println(RED + someString + RED);
}

public void green_output(String someString){
    System.out.println(GREEN + someString + GREEN);
}

public void white_output(String someString){
    System.out.println(WHITE + someString + WHITE);
}

public void magenta_output(String someString){
    System.out.println(MAGENTA + someString + MAGENTA);
}

