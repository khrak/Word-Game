package com.example.khrak.wordgame.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.khrak.wordgame.R;
import com.example.khrak.wordgame.communication.CommunicationManager;

import org.json.JSONException;
import org.json.JSONObject;

public class RoomActivity extends AppCompatActivity {
    private int roomId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.room_activity);

        getSupportActionBar().hide();

        roomId = Integer.valueOf(getIntent().getStringExtra("roomid"));
        String username = getIntent().getStringExtra("username");
        Button leaveBtn = (Button) findViewById(R.id.leave_room_btn);
        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveRoom();
            }
        });
    }

    public void leaveRoom(){

        String url ="http://amimelia-001-site1.itempurl.com/api/Gamelobby/LeaveRoom?userName="
                + CommunicationManager.getInstance().getUserName();

        final com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        try {
                            Intent intent = new Intent(RoomActivity.this, LobbyActivity.class);
                            intent.putExtra("username", getIntent().getStringExtra("username"));
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!" + error);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
