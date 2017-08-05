package com.example.khrak.wordgame.Game;

import com.example.khrak.wordgame.communication.models.GameEvent;

/**
 * Created by melia on 8/5/2017.
 */

public class WordGame {

    IWordGameListener gameEventsListener;
    AbstractGameEventsProcessor gameProcessor;

    public WordGame(int gameId, IWordGameListener listener, boolean isOfflineGame){
        this.gameEventsListener = listener;
        if (isOfflineGame){
            gameProcessor = new OfflineGameEventProcessor(gameId, this);
        }else{
            gameProcessor = new LiveGameEventProcessor(gameId, this);
        }
    }

    public void sendGameEvent(GameEvent eventToSend){
        gameProcessor.processGameEvent(eventToSend);
    }

    public void ConnectionReAvailable(){
        gameProcessor.refreshGameModel();
    }

    public void ResumeActivity(){
        gameProcessor.refreshGameModel();
    }

    public void drawGame(GameModel model){
        gameEventsListener.drawGame(model);
    }
}
