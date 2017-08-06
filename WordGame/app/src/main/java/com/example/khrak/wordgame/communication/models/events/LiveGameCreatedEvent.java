package com.example.khrak.wordgame.communication.models.events;

import android.util.Log;

import com.example.khrak.wordgame.Game.GameModel;
import com.example.khrak.wordgame.communication.models.EventResponse;
import com.example.khrak.wordgame.communication.models.GameEvent;
import com.google.gson.Gson;

/**
 * Created by melia on 8/6/2017.
 */

public class LiveGameCreatedEvent extends GameEvent<String> {

    public LiveGameCreatedEvent(EventResponse eventResponse){
        super(eventResponse);
        eventData = "0";
        Gson gson = new Gson();
        eventData = gson.fromJson(eventResponse.eventJsonData, eventData.getClass());
        /* Log.w("Final", "here");
        Log.w("Final", "there"); */
    }

}
