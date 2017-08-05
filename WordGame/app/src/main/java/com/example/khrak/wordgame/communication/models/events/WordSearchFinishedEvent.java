package com.example.khrak.wordgame.communication.models.events;

import com.example.khrak.wordgame.Game.GameModel;
import com.example.khrak.wordgame.communication.models.EventResponse;
import com.example.khrak.wordgame.communication.models.GameEvent;
import com.example.khrak.wordgame.communication.models.GameEventFactory;
import com.example.khrak.wordgame.communication.models.WordSearchingFinished;
import com.google.gson.Gson;

/**
 * Created by melia on 8/5/2017.
 */

public class WordSearchFinishedEvent extends GameEvent<WordSearchingFinished> {

    public WordSearchFinishedEvent(EventResponse eventResponse){
        super(eventResponse);
        eventData = new WordSearchingFinished();
        Gson gson = new Gson();
        eventData = gson.fromJson(eventResponse.eventJsonData, eventData.getClass());
    }

    public WordSearchFinishedEvent(){
        this.eventKey = GameEventFactory.OFFLINE_EVENT_MOVE_FINISHED;
    }
}
