package com.example.khrak.wordgame.communication;

import com.example.khrak.wordgame.Game.GameModel;
import com.example.khrak.wordgame.communication.models.GameEvent;

/**
 * Created by melia on 8/2/2017.
 */

public interface IGameEventsListener {
    void processGameEvent(GameEvent event);
}
