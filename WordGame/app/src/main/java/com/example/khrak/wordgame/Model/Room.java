package com.example.khrak.wordgame.Model;

/**
 * Created by khrak on 8/2/17.
 */

public class Room {
    private String name;
    private String privacy;
    private int numPlayers;

    public Room(String name, String privacy, int numPlayers) {
        this.name = name;
        this.privacy = privacy;
        this.numPlayers = numPlayers;
    }

    public String getName() {
        return name;
    }

    public String getPrivacy() {
        return privacy;
    }

    public int getNumPlayers() {
        return numPlayers;
    }
}
