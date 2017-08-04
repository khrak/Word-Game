package com.example.khrak.wordgame.communication.models.events;

import com.example.khrak.wordgame.communication.models.EventResponse;
import com.example.khrak.wordgame.communication.models.GameEvent;
import com.example.khrak.wordgame.communication.models.InviteGameEventData;
import com.google.gson.Gson;

/**
 * Created by melia on 8/3/2017.
 */

public class InvitationReceivedEvent extends GameEvent<InviteGameEventData> {

    public InvitationReceivedEvent(EventResponse eventResponse){
        super(eventResponse);
        eventData = new InviteGameEventData();
        Gson gson = new Gson();
        eventData = gson.fromJson(eventResponse.eventJsonData, eventData.getClass());
    }
}
