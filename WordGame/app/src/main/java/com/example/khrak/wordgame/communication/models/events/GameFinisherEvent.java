package com.example.khrak.wordgame.communication.models.events;

import com.example.khrak.wordgame.Game.GameModel;
import com.example.khrak.wordgame.communication.models.EventResponse;
import com.example.khrak.wordgame.communication.models.GameEvent;
import com.google.gson.Gson;

/**
 * Created by melia on 8/6/2017.
 */

public class GameFinisherEvent  extends GameEvent<String>  {
    public GameFinisherEvent(EventResponse eventResponse){
        super(eventResponse);
    }
}
