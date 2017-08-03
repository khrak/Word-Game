package com.example.khrak.wordgame.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.khrak.wordgame.Adapters.LobbyAdapter;
import com.example.khrak.wordgame.Model.Room;
import com.example.khrak.wordgame.R;
import com.example.khrak.wordgame.communication.CommunicationManager;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class LobbyActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_activity);

        getSupportActionBar().hide();

        final String username = getIntent().getStringExtra("username");

        drawRooms(username);

        final FloatingActionButton createRoom = (FloatingActionButton) findViewById(R.id.createroom_button);

        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(LobbyActivity.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.setContentView(R.layout.createroom_dialog);

                final EditText editText = (EditText) dialog.findViewById(R.id.editText);
                Button btnSave          = (Button) dialog.findViewById(R.id.save);
                Button btnCancel        = (Button) dialog.findViewById(R.id.cancel);
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

                        RadioButton privateButton = (RadioButton) findViewById(R.id.radio_private);
                        RadioButton publicButton = (RadioButton) findViewById(R.id.radio_public);

                        boolean privateRoom = false;

                        if (privateButton.isChecked()) {
                            privateRoom = true;
                        }

                        addRoom(roomName, privateRoom);

                        dialog.dismiss();
                    }
                });
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        final FloatingActionButton logoutButton = (FloatingActionButton) findViewById(R.id.logout_button);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (signedWithGoogle()) {
                        googleSignOut();
                    }

//                    if (SignUpActivity.signedWithFacebook()) {
//                        SignUpActivity.facebookSignOut();
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(LobbyActivity.this, WelcomeActivity.class);

                startActivity(intent);
            }
        });
    }

    private void addRoom(String roomName, boolean privateRoom) {
        
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

                                    joinRoom(room, username);
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

    private void joinRoom(Room room, String username) {
        final com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);

        String keyPath = "http://amimelia-001-site1.itempurl.com/api/gamelobby/JoinRoom?userName="
                + username + "&roomId=" + room.getId();

        System.out.println(keyPath);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, keyPath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {

                        System.out.println("Joined");

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        Intent intent = new Intent(this, RoomActivity.class);

        startActivity(intent);
    }
}
