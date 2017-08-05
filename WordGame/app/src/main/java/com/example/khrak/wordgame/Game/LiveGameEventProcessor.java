package com.example.khrak.wordgame.Game;

import com.example.khrak.wordgame.communication.models.GameEvent;

/**
 * Created by melia on 8/5/2017.
 */

public class LiveGameEventProcessor extends AbstractGameEventsProcessor {

    private int mGameId = 0;
    public LiveGameEventProcessor(int gameId, WordGame game){
        super(game);
        mGameId = gameId;
    }

    @Override
    public void refreshGameModel() {
        //TODO request from server and update it in gui via gameModelUpdated
    }

    @Override
    public void processGameEvent(GameEvent eventToProcess) {

    }
}
