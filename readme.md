Course: CSC203
Author: Chad Verbus
Date: Nov 23, 2025

## Description:
VerboseHominid:SunHome 13 is a single player SciFi/Fantasy text based simulation/rpg. At its current state, it's essentially a prototype chat interface to see if it would work and be fun, surprisingly it turned out to be fun.

Branch Student_Start is the last playable build. 

Starbase13 Start:
* https://github.com/ev3rnub/CSC203_Week_0/tree/Student_Start

## Requirements:
* OpenJDK 25+
* Maven
* MacOS (Note: I'm learning Java, I've had mixed results with getting VerboseHominid to run on other macs.)
* xAI API Key (set local environmental variable to export xAI_API_KEY=<YOUR_API_KEY>)
* build, compile and have fun. 

## Usage
⚠️ Working Document – This README is a living document that will be updated throughout the semester as new topics, assignments, and resources become available.  

## 11/07/2025
* 1. Added more functionality. added regex engine class to parse LLM output to parse the output to other game windows. Added xAI api class to interface with Grok API.
* 2. Created Dev0 Branch.
* 3. Added a inventory window and button.
* 4. Refactored main .java file and seperated code by function per java file.
* 5. Modified System prompt for xAI LLM.

## 11/08/2025
* 1. Created Branch for this weeks assignment starbase13, specifically start, renamed for RP purposes to SunHome13. Enjoy :)
* 2. Modified system prompt for starbase13 start. 
* 3. Modified Color theme.
* 4. Added a local LLM class for a local ollama instance.
* 5. Updated readme.md

## 11/09/2025
* 1. Fixed system instructions, reformated comments and added more comments, removed magenta and added cyan. Removed old code, no longer being used.
* 2. Updated readme.md

## 11/12/2025
* 1. Added a Note button, and window.
* 2. Added a basic note system.
* 3. Experimented with sound. Added temp background music and a button to toggle music.

## 11/13/2025
### Fixed/Added
* 1. Added SaveGame functionality to GameEngine.
* 2. Converted music .wav file to 8bit to reduce filesize for github restriction.
## 11/15/2025
### Paused
* 1. Save and load functionality.
## 11/17/2025
* 1. Compiled a local version!
## 11/18/2025
* 1. Created Branch StarBase13_Char
* 2. Started work on a Character class to further define the player character and NPCs.
## 11/22/2025
* 1. Finished a character class in place of a person class. 
* 2. Finished a professor class. 
* 3. Finished a student class. 
* 4. Refactored some code, updated comments across the codebase, started using jdoc comments.
* 5. Updated Readme
* 6. Started work on LocalPlayArea class.
* 7. Created Branch Student_Start
## 11/23/2025
* 1. Updated StoryMaster System Instruction
* 2. Fixed music looping issue
* 3. Implemented classes using super to define base class properties.
* 4. Removed some debugging statements.
* 5. Updated comments 
* 6. Updated readme
## 12/09/2025
* 1. Defined a simple weather class. WIP
* 2. Defined a localarea class, to define hard parameters for the local area surrounding the player.
* 3. Created new branch, "detained"; In this branch, I will focus on using local area and classes only, making the world a bit smaller.
### InWork:
* 0. Regex Engine.
### ToDO:
* 0. Define the remaining character classes.
* 1. Define some class, to monitor, manage the local area
* 2. Define some class, to monitor the characters state and other NPCs in local area.
* 3. Define some class to manage direction/quests.
* 4. Define a API Analytics tool class to track API keys, usage.
