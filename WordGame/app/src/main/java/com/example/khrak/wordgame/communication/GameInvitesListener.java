package com.example.khrak.wordgame.communication;

import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.khrak.wordgame.AppMain;
import com.example.khrak.wordgame.communication.models.GameEvent;
import com.example.khrak.wordgame.communication.models.GameEventFactory;
import com.example.khrak.wordgame.communication.models.InviteGameEventData;

/**
 * Created by melia on 8/3/2017.
 */

public class GameInvitesListener implements IGameEventsListener {

    @Override
    public void processGameEvent(GameEvent event) {
        if (!event.IsSameEvent(GameEventFactory.EVENT_KET_INVITATION_RECEVED))
            return;

        InviteGameEventData eventData = (InviteGameEventData) event.getEventData();
        Log.w("InviteRequestReceived", "roomid = " + eventData.roomId + " user = " + eventData.inviteAuthor.UserName);
    }
}
