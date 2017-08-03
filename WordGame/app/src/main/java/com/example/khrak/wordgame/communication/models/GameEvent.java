package com.example.khrak.wordgame.communication.models;

/**
 * Created by melia on 8/2/2017.
 */

public class GameEvent<T> {

    public String eventKey;
    public String eventAuthor;
    protected T eventData;

    public GameEvent(EventResponse eventResponse){
        eventAuthor = eventResponse.eventAuthor;
        eventKey = eventResponse.eventKey;
    }

    public T getEventData() {
        return eventData;
    }

}
