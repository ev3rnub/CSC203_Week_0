package org.verboseStory.model;
//[Class  | Professor   | Assignment Title---------------------------------------]
//[CSC203 | Prof Hinton | Assignment 4.2: Object Oriented Programming Inheritance]
//[Student Child Class of Character|--------------------------------- Requirement]
import org.verboseStory.engine.GameEngine;
import java.awt.*;
/**
 * This is my version of a "Student" class.
 *  The Student class that can attend the newly established College on the SunHome13 space station.
 *  Note: some variables are defined as public for class assignment
 */
public class Student extends Character {
        private int focusPoints = 0;
        private enum StudentMode {STUDY, PARTY, NORMAL};
        private StudentMode currentStudentMode = StudentMode.NORMAL;

        public Student(String firstName, String lastName, String someBackground, String race) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.background = someBackground;
            this.someRace = race;
            this.someCharacterClass = "STUDENT";
            this.setTitle("Student");
            this.setCreditWallet(100);
            this.setFocusPoints(5);
            this.setActionPoints(100);
        }

        public void learn(){
            if (this.focusPoints >= 1){
                setStudentMode(StudentMode.STUDY);
                int someFocusPoints = focusPoints--;
                setFocusPoints(someFocusPoints);
            }else{
                setStudentMode(StudentMode.NORMAL);
                GameEngine.printOutput(Color.RED, "Student", "Not enough free focus points, rest and try again later");
            }

        }

        public StudentMode getCurrentStudentMode() {
            return currentStudentMode;
        }

        public void setStudentMode( StudentMode currentStudentMode) {
            this.currentStudentMode = currentStudentMode;
        }

        public void setFocusPoints(int focusPoints) {
            this.focusPoints = focusPoints;
        }

        public int getFocusPoints() {
            return focusPoints;
        }

}
