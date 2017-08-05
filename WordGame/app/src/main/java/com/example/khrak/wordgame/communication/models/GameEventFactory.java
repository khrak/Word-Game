package com.example.khrak.wordgame.communication.models;

import com.example.khrak.wordgame.communication.models.events.*;

/**
 * Created by melia on 8/2/2017.
 */

public class GameEventFactory {
    public static final String EVENT_KET_TEST = "game.event.test";
    public static final String EVENT_KET_UPDATE_ROOM = "game.event.room.changed";
    public static final String EVENT_KET_INVITATION_RECEVED = "game.event.invitation.receved";
    public static final String EVENT_KET_PING = "game.event.ping";
    public static final String EVENT_GAME_START = "game.event.start";

    public static final String OFFLINE_EVENT_MOVE_FINISHED = "offline.game.move.finished.event";
    public static final String OFFLINE_EVENT_BETTING_STARTED = "offline.game.betting.started";
    public static final String OFFLINE_EVENT_BETTING_FINISHED = "offline.game.move.betting.finished";
    public static final String EVENT_GAME_ROUND_FINISH = "game.event.word.finish";

    public static GameEvent getGameEvent(EventResponse responce){
        switch (responce.eventKey){
            case EVENT_KET_TEST:
                return new TestGameEvent(responce);
            case EVENT_KET_UPDATE_ROOM:
                return new UpdateRoomEvent(responce);
            case EVENT_KET_INVITATION_RECEVED:
                return new InvitationReceivedEvent(responce);
            default:
                return null;
        }
    }
}
