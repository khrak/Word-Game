package com.example.khrak.wordgame.communication.models;

/**
 * Created by melia on 8/2/2017.
 */

public class GameEventFactory {
    public static final String EVENT_KET_TEST = "game.event.test";

    public static GameEvent getGameEvent(EventResponse responce){
        switch (responce.eventKey){
            case EVENT_KET_TEST:
                return new TestGameEvent(responce);
            default:
                return null;
        }
    }
}
