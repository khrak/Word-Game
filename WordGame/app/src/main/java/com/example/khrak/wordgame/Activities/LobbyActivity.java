package com.example.khrak.wordgame.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.khrak.wordgame.Adapters.LobbyAdapter;
import com.example.khrak.wordgame.Model.Room;
import com.example.khrak.wordgame.R;
import com.example.khrak.wordgame.communication.CommunicationManager;
import com.example.khrak.wordgame.communication.models.GameEvent;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class LobbyActivity extends ICommunicatorActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    private int LobbyUpdateDelay = 2000; //milliseconds
    private Handler LobbyUpdateHandler = new Handler();
    private RadioButton privateButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("On create called");

        System.out.println("lobby started");

        mGoogleApiClient = WelcomeActivity.mGoogleApiClient;

        setContentView(R.layout.lobby_activity);

        getSupportActionBar().hide();

	final String username = CommunicationManager.getInstance().getUserName();
        
System.out.println("Test Username = " + username);

        drawRooms(CommunicationManager.getInstance().getUserName());

        final FloatingActionButton createRoom = (FloatingActionButton) findViewById(R.id.createroom_button);

        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(LobbyActivity.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.setContentView(R.layout.createroom_dialog);

                final EditText editText = (EditText) dialog.findViewById(R.id.editText);
                Button btnSave = (Button) dialog.findViewById(R.id.save);
                Button btnCancel = (Button) dialog.findViewById(R.id.cancel);

                final RadioButton privateButton = (RadioButton) dialog.findViewById(R.id.radio_private);

                dialog.show();

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String roomName = editText.getText().toString();

                        String privacy = "public";

                        if (privateButton.isChecked()) {
                            privacy = "private";
                        }

                        addRoom(roomName, privacy);

                        dialog.dismiss();
                    }
                });
            }
        });

        final FloatingActionButton logoutButton = (FloatingActionButton) findViewById(R.id.logout_button);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (signedWithGoogle()) {
                        googleSignOut();
                    }

                    if (SignUpActivity.signedWithFacebook()) {
                        SignUpActivity.facebookSignOut();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(LobbyActivity.this, WelcomeActivity.class);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        drawRooms(CommunicationManager.getInstance().getUserName());
        System.out.println("On resume called");
        LobbyUpdateHandler.postDelayed(new Runnable(){
            public void run(){
                drawRooms(CommunicationManager.getInstance().getUserName());
                LobbyUpdateHandler.postDelayed(this, LobbyUpdateDelay);
            }
        }, LobbyUpdateDelay);

        super.onResume();
    }

    @Override
    public void processGameEvent(GameEvent event) {
        return;
    }

    @Override
    public void connectionToServerEstabilished() {
        drawRooms(CommunicationManager.getInstance().getUserName());
    }

    @Override
    public void onPause() {
        LobbyUpdateHandler.removeCallbacksAndMessages(null);
        super.onPause();
    }

    private void addRoom(String roomName, String privacy) {
        String url ="http://amimelia-001-site1.itempurl.com/api/Gamelobby/CreateRoom?userName="
                + CommunicationManager.getInstance().getUserName() + "&roomName="
                + roomName + "&roomAccessibility=" + privacy;

        final com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String id = "" + jsonObject.getInt("roomId");

                            Intent intent = new Intent(LobbyActivity.this, RoomActivity.class);

                            intent.putExtra("roomid", id);
                            intent.putExtra("username", getIntent().getStringExtra("username"));

                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                System.out.println("That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private boolean signedWithGoogle() {
        return Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient).isDone();
    }

    private void googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void drawRooms(final String username) {

        final com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);

        String keyPath = "http://amimelia-001-site1.itempurl.com/api/Gamelobby/GetAvaliableRooms?userName=" + username;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, keyPath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        try {
                            JSONArray jsonarray = new JSONArray(response);

                            ArrayList<Room> rooms = new ArrayList<>();

                            for (int i = 0; i < jsonarray.length(); i++) {
                                JSONObject json = jsonarray.getJSONObject(i);

                                Room room = new Room(json.getString("RoomName"),
                                                        "" + json.getInt("RoomId"),
                                                            json.getString("Accessibility"),
                                                                json.getInt("MembersCount"));

                                rooms.add(room);
                            }

                            ListView listView = (ListView) findViewById(R.id.rooms_list);

                            final LobbyAdapter adapter = new LobbyAdapter(rooms, LobbyActivity.this);

                            listView.setAdapter(adapter);

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    Room room = (Room) adapter.getItem(position);

                                    joinRoom(room, CommunicationManager.getInstance().getUserName());
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void joinRoom(final Room room, final String username) {
        final com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);

        System.out.println(username);

        try {
            String keyPath = "http://amimelia-001-site1.itempurl.com/api/gamelobby/JoinRoom?userName="
                    + username + "&roomId=" + room.getId();

            System.out.println(keyPath);

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, keyPath,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(final String response) {

                            Intent intent = new Intent(LobbyActivity.this, RoomActivity.class);

                            intent.putExtra("roomid", room.getId());
                            intent.putExtra("username", username);

                            LobbyActivity.this.finish();

                            startActivityForResult(intent, 1);

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
