package com.example.khrak.wordgame.communication.models;

import com.google.gson.Gson;

/**
 * Created by melia on 8/2/2017.
 */

public class TestGameEvent extends GameEvent<String> {

    public TestGameEvent(EventResponse eventResponse){
        super(eventResponse);
        //Gson gson = new Gson();
        //eventData = gson.fromJson(eventResponse.eventJsonData, eventData.getClass());
    }
}
