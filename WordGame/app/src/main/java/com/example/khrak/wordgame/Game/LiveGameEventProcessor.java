package com.example.khrak.wordgame.Game;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.khrak.wordgame.Activities.RoomActivity;
import com.example.khrak.wordgame.AppMain;
import com.example.khrak.wordgame.communication.CommunicationManager;
import com.example.khrak.wordgame.communication.models.GameEvent;
import com.example.khrak.wordgame.communication.models.GameEventFactory;
import com.google.gson.Gson;

/**
 * Created by melia on 8/5/2017.
 */

public class LiveGameEventProcessor extends AbstractGameEventsProcessor {

    private int mGameId = 0;
    public LiveGameEventProcessor(int gameId, WordGame game){
        super(game);
        mGameId = gameId;
        refreshGameModel();
    }

    @Override
    public void refreshGameModel() {
        //TODO request from server and update it in gui via gameModelUpdated
        final String url ="http://amimelia-001-site1.itempurl.com/api/game/getgame?userName="
                + CommunicationManager.getInstance().getUserName() +"&gameId=" + mGameId;
        final com.android.volley.RequestQueue queue = Volley.newRequestQueue(AppMain.getContext());

        System.out.println(url);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        System.out.println("Create Data Found result = " + response);
                        Gson gson = new Gson();
                        GameModel eventData = gson.fromJson(response, GameModel.class);
                        LiveGameEventProcessor.this.mGameModel = eventData;
                        refreshGameModel();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public void processGameEvent(GameEvent eventToProcess) {
        if (eventToProcess.IsSameEvent(GameEventFactory.EVENT_LIVE_GAME_EVENT)){
            GameModel model = (GameModel) eventToProcess.getEventData();
            mGameModel = model;
            gameModelUpdated();
        }
    }
}
