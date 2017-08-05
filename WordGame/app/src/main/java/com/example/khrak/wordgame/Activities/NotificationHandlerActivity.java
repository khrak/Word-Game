package com.example.khrak.wordgame.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class NotificationHandlerActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String username = getIntent().getStringExtra("username");
        String roomid   = getIntent().getStringExtra("roomid");

        joinRoom(roomid, username);
    }

    private void joinRoom(final String roomid, final String username) {
        final com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);

        System.out.println(username);

        System.out.println("Aaaaaaaaa");

        try {
            String keyPath = "http://amimelia-001-site1.itempurl.com/api/gamelobby/JoinRoom?userName="
                    + username + "&roomId=" + roomid;

            System.out.println(keyPath);

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, keyPath,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(final String response) {

                            Intent intent = new Intent(NotificationHandlerActivity.this,
                                                        RoomActivity.class);

                            intent.putExtra("roomid", roomid);
                            intent.putExtra("username", username);

                            NotificationHandlerActivity.this.finish();

                            startActivity(intent);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("That didn't work!");
                }
            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
