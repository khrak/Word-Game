package com.example.khrak.wordgame.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.khrak.wordgame.R;
import com.example.khrak.wordgame.communication.CommunicationManager;
import com.example.khrak.wordgame.communication.IGameEventsListener;
import com.example.khrak.wordgame.communication.models.GameEvent;
import com.example.khrak.wordgame.communication.models.GameEventFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RoomActivity extends AppCompatActivity implements IGameEventsListener {
    private int roomId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.room_activity);

        getSupportActionBar().hide();

        roomId = Integer.valueOf(getIntent().getStringExtra("roomid"));
        String username = getIntent().getStringExtra("username");
        Button leaveBtn = (Button) findViewById(R.id.leave_room_btn);


        final Activity thisActivity = this;
        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thisActivity.finish();
                //leaveRoom();
            }
        });

        drawRoom(roomId, username);
        attachGameEventListener();
    }


    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public void onDestroy() {
        try {
            leaveRoom();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    private void drawRoom(int roomId, String username) {

        String url ="http://amimelia-001-site1.itempurl.com/api/gamelobby/GetRoom?userName=" +
                username + "&roomId=" + roomId;
        System.out.println(url);
        final com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String roomName = "";
                            String ownerName = "";

                            if (jsonObject.has("OwnerUserName")) {
                                ownerName = jsonObject.getString("OwnerUserName");

                                TextView ownerView = (TextView) findViewById(R.id.owner_image_view);

                                ownerView.setText(ownerName);
                            }

                            if (jsonObject.has("RoomName")) {
                                roomName = jsonObject.getString("RoomName");

                                TextView roomNameView = (TextView) findViewById(R.id.room_title);

                                roomNameView.setText(roomName);
                            }

                            ImageView imageView1 = (ImageView) findViewById(R.id.profile_image1);
                            ImageView imageView2 = (ImageView) findViewById(R.id.profile_image2);
                            ImageView imageView3 = (ImageView) findViewById(R.id.profile_image3);
                            ImageView imageView4 = (ImageView) findViewById(R.id.profile_image4);
                            ImageView imageView5 = (ImageView) findViewById(R.id.profile_image5);

                            TextView nameView1 = (TextView) findViewById(R.id.profile_name1);
                            TextView nameView2 = (TextView) findViewById(R.id.profile_name2);
                            TextView nameView3 = (TextView) findViewById(R.id.profile_name3);
                            TextView nameView4 = (TextView) findViewById(R.id.profile_name4);
                            TextView nameView5 = (TextView) findViewById(R.id.profile_name5);

                            if (jsonObject.has("MemberUsers")) {
                                JSONArray array = jsonObject.getJSONArray("MemberUsers");

                                updateViews(ownerName, imageView1, nameView1, array.getJSONObject(0));

                                if (array.length() > 1) {
                                    updateViews(ownerName, imageView2, nameView2, array.getJSONObject(1));
                                }

                                if (array.length() > 2) {
                                    updateViews(ownerName, imageView3, nameView3, array.getJSONObject(2));
                                }

                                if (array.length() > 3) {
                                    updateViews(ownerName, imageView4, nameView4, array.getJSONObject(3));
                                }

                                if (array.length() > 4) {
                                    updateViews(ownerName, imageView5, nameView5, array.getJSONObject(4));
                                }
                            }

                        } catch (JSONException e) {
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

    private void updateViews(String ownerName, ImageView imageView1, TextView nameView1, JSONObject jsonObject) {

        try {
            String username = jsonObject.getString("UserName");
            int iconid = jsonObject.getInt("IconId");

            nameView1.setText(username);

            int sourceid = 0;

            if (iconid == 1) {
                sourceid = R.drawable.avatar_icon1;
            }

            if (iconid == 2) {
                sourceid = R.drawable.avatar_icon1;
            }

            if (iconid == 3) {
                sourceid = R.drawable.avatar_icon2;
            }

            if (iconid == 4) {
                sourceid = R.drawable.avatar_icon3;
            }

            if (iconid == 5) {
                sourceid = R.drawable.avatar_icon4;
            }

            if (iconid == 5) {
                sourceid = R.drawable.avatar_icon5;
            }

            if (iconid == 6) {
                sourceid = R.drawable.avatar_icon6;
            }

            if (iconid == 7) {
                sourceid = R.drawable.avatar_icon7;
            }

            if (iconid == 8) {
                sourceid = R.drawable.avatar_icon8;
            }

            if (iconid == 9) {
                sourceid = R.drawable.avatar_icon9;
            }

            imageView1.setImageResource(sourceid);

            if (ownerName.equals(username)) {
                ImageView ownerImage = (ImageView) findViewById(R.id.owner_image);

                ownerImage.setImageResource(sourceid);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void attachGameEventListener(){
        CommunicationManager.getInstance().addGameEventListener(this);
    }

    private void dispachGameEventsListener(){
        CommunicationManager.getInstance().removeGameEventListener(this);
    }

    public void leaveRoom() {

        dispachGameEventsListener();
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

    @Override
    public void processGameEvent(GameEvent event) {
        if (event.IsSameEvent(GameEventFactory.EVENT_KET_UPDATE_ROOM)){
            drawRoom(roomId, CommunicationManager.getInstance().getUserName());
        }
    }
}
