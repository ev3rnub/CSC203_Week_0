package org.verboseStory.engine;

/**
 * Holds a single static reference to the running {@link GameEngine}.
 * This is a tiny “service locator” that the API thread can read.
 */
public final class GameEngineStaticHolder {
    private GameEngineStaticHolder() {}
    public static GameEngine engine;
}