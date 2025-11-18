package org.verboseStory.model;
import org.verboseStory.engine.GameEngine;


public record Character (
        String firstName,
        String lastName,
        String gender,
        String race,
        String charClass,
        String someBackground,
        String location,
        String extLocation, /** external location, A city, a town-village, the central prison*/
        String typeLocation, /** surface, underwater, air, space*/

        int movementPoints,
        int hunger,
        int health,
        int height,
        int weight,
        private int strength,
        private int stamina,
        private int agility,
        private int intelligence,
        private int charisma,
        private int dexterity,
        private int wisdom,
        private int blood,
        private int chaosPoints,
        private int lightPoints,
        private int darkPoints,
        private int bodyTemp,
        private int envTemp,
        private String localCharEnvWeather,
        private int headHP,
        private int torsoHP,
        private int leftArmHP,
        private int rightArmHP,
        private int leftLegHP,
        private int rightLegHP,
        private int leftHandHP,
        private int rightHandHP,
        private int leftEyeHP,
        private int rightEyeHP

// constructor
    private Character(Buider someBuilder) {
        this.firstName = someBuilder.add.firstName;
        this.lastName = someBuilder.lastName;
        this.gender = someBuilder.gender;
        this.race = someBuilder.race;
        this.charClass = someBuilder.someClass;
        this.someBackground = someBuilder.someBackground;
        this.location = someBuilder.location;
        this.extLocation = someBuilder.extLocation;
        this.typeLocation = someBuilder.typeLocation;
        this.movementPoints = 0;
        this.hunger = 0;
        this.health = 0;
        this.weight = 0;
        this.strength = 0;
        this.stamina = 0;
        this.agility = 0;
        this.intelligence = 0;
        this.charisma = 0;
        this.dexterity = 0;
        this.wisdom = 0;
        this.blood = 0;
        this.headHP = 10;
        this.torsoHP = 10;
        this.leftArmHP = 10;
        this.leftHandHP = 10;
        this.leftEyeHP = 10;
        this.leftLegHP = 10;
        this.rightArmHP = 10;
        this.rightHandHP = 10;
        this.rightEyeHP = 10;
        this.rightLegHP = 10;
        this.health = 100;
        this.bodyTemp = 98;
        this.envTemp = 0;
        this.localCharEnvWeather = "{Clear, Some clouds, 65f, 90%h}";
        this.chaosPoints = 0;
        this.lightPoints = 0;
        this.darkPoints = 0;
}



}
