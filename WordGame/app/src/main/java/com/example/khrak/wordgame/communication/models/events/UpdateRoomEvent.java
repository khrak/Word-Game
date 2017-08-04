package com.example.khrak.wordgame.communication.models.events;

import com.example.khrak.wordgame.communication.models.EventResponse;
import com.example.khrak.wordgame.communication.models.GameEvent;
import com.google.gson.Gson;

/**
 * Created by melia on 8/3/2017.
 */

public class UpdateRoomEvent extends GameEvent<String> {

    public UpdateRoomEvent(EventResponse eventResponse){
        super(eventResponse);
        //Gson gson = new Gson();
        //eventData = gson.fromJson(eventResponse.eventJsonData, eventData.getClass());
    }


}
