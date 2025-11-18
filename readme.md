Course: CSC203
Author: Chad Verbus
Date: Nov 18, 2025

## Description:
VerboseHominid:Starbase 13 is a single player SciFi/Fantasy text based simulation/rpg. At its current state, it's essentially a prototype chat interface to see if it would work and be fun, surprisingly it turned out to be fun.

Branch Starbase13_start is the last playable build. 

Starbase13 Start:
* https://github.com/ev3rnub/CSC203_Week_0/tree/starbase13_start

## Requirements:
* OpenJDK 25+
* Maven
* MacOS (Note: I'm learning Java, I've had mixed results with getting VerboseHominid to run on other macs.)
* xAI API Key (set local environmental variable to export xAI_API_KEY=<YOUR_API_KEY>)
* build, compile and have fun. 

## Usage
Once you 
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
## 11/16/2025
* 1. Compiled a local version!
## 11/18/2025
* 1. Created Branch StarBase13_Char
* 2. Started work on an API Analytics class.
### ToDO:
* 1. Define a Character class.
* 2. Define a Item class.
* 3. Define a LocalPlayArea class.
### BUGs/Missing Features
* 1. Music doesn't automatically loop/restart.
* 2. WILL Update readme.md.
