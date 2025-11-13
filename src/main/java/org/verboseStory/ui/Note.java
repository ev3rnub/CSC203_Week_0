package org.verboseStory.ui;

/**
 * A tiny connector that saves String data from the NoteWindow.
 */
public final class Note {
    private static NoteWindow window;
    // stores reference for our game window
//    setter
    public static void setWindow(NoteWindow w) { window = w; }
//    getter
    public static NoteWindow getWindow() { return window; }
}