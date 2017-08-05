package com.example.khrak.wordgame.Game;

import com.example.khrak.wordgame.communication.models.GameEvent;

/**
 * Created by melia on 8/5/2017.
 */

public abstract class AbstractGameEventsProcessor {

    GameModel mGameModel;
    WordGame mGame;

    public abstract void refreshGameModel();
    public abstract void processGameEvent(GameEvent eventToProcess);

    public AbstractGameEventsProcessor(WordGame game){
        mGame = game;
    }

    public void gameModelUpdated(){
        mGame.drawGame(mGameModel);
    }

}
