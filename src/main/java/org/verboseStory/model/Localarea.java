package org.verboseStory.model;

/**
 * A class to model the local area around the player. You can think of this as the 'room" a player is in,
 * containing all of the lootables(Loot, food, water, insects, flora, fauna, earth), interactables(Hominids,
 * Loot, Puzzles, Quests, Questlines, doors) in the room.
 * WIP
 * */

public class Localarea {
    private enum AreaType {OUTSIDE, INSIDE};
    private enum FloorType {DIRT, STONE, GLASS, WOOD, ROCK, SAND, METAL};
    private enum WallType {DIRT, STONE, GLASS, WOOD, ROCK, SAND, METAL};
    private enum AtmosType {VACUUM, BREATHABLE, UNBREATHABLE}

    AreaType defaultArea;
    FloorType defaultFloor;
    WallType defaultWall;
    AtmosType defaultAtmosType;

//

     public Localarea(AreaType someArea, FloorType someFloor, WallType someWall, AtmosType someAtmos){
        //        Defaults
       this.defaultArea = someArea;
       this.defaultFloor = someFloor;
       this.defaultWall = someWall;
       this.defaultAtmosType = someAtmos;
    }
}
