package com.example.khrak.wordgame.communication.models.events;

import com.example.khrak.wordgame.Game.GameModel;
import com.example.khrak.wordgame.communication.models.EventResponse;
import com.example.khrak.wordgame.communication.models.GameEvent;
import com.example.khrak.wordgame.communication.models.WordSearchingFinished;
import com.google.gson.Gson;

/**
 * Created by melia on 8/6/2017.
 */

public class LiveGameEvent extends GameEvent<GameModel> {

    public LiveGameEvent(EventResponse eventResponse){
        super(eventResponse);
        eventData = new GameModel();
        Gson gson = new Gson();
        eventData = gson.fromJson(eventResponse.eventJsonData, eventData.getClass());
    }

}
