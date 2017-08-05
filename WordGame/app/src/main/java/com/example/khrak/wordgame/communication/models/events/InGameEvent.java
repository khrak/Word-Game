package com.example.khrak.wordgame.communication.models.events;

import com.example.khrak.wordgame.Game.GameModel;
import com.example.khrak.wordgame.communication.models.EventResponse;
import com.example.khrak.wordgame.communication.models.GameEvent;
import com.google.gson.Gson;

/**
 * Created by melia on 8/5/2017.
 */

public class InGameEvent extends GameEvent<GameModel> {

    public InGameEvent(EventResponse eventResponse){
        super(eventResponse);
        eventData = new GameModel();
        Gson gson = new Gson();
        eventData = gson.fromJson(eventResponse.eventJsonData, eventData.getClass());
    }

    public InGameEvent(String eventKey){
        this.eventKey = eventKey;
        this.eventData = new GameModel();
        this.eventAuthor = "You";
    }
}
