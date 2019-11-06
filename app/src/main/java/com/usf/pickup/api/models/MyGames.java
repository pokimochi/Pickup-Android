package com.usf.pickup.api.models;

public class MyGames {
    private Game[] organizedGames;
    private Game[] games;

    public MyGames() {
    }

    public Game[] getGames() {
        return games;
    }

    public Game[] getOrganizedGames() {
        return organizedGames;
    }
}
