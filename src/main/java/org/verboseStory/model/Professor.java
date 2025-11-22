package org.verboseStory.model;

public class Professor extends Character {
    // The Characters primary subject of expertise.
    public String subject;
    // Define some modes of teaching, in academic, personal, to crowds, and regarding their belief.
    public enum Professing {
        ACADEMIC,
        PUBLIC,
        CROWD,
        BELIEF,
        QUIET
    };
    private int effortPoints;
    public Professing currentProfessing;

    public Professor(String firstName, String lastName, String background, String race, String subject) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.background = background;
        this.subject = subject;
        this.someRace = race;
        this.setCharacterClass("PROFESSOR");
        this.setTitle("Professor");
        this.setProfessing(Professing.QUIET);
        this.setCreditWallet(100);
    }

    // Subject the professor is considered to be an expert
    //setter
    public void  setSubject(String subject) {
        this.subject = subject;
    }
    //getter
    public String getSubject() {
        return subject;
    }

    // set teaching
    public void setProfessing(Professing professing) {
        currentProfessing = professing;
    }
    // get teaching
    public Professing getProfessing() {
        return currentProfessing;
    }
}
