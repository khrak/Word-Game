package com.example.khrak.wordgame.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.khrak.wordgame.R;
import com.example.khrak.wordgame.communication.CommunicationManager;
import com.example.khrak.wordgame.communication.models.GameEvent;
import com.example.khrak.wordgame.communication.models.GameEventFactory;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RoomActivity extends ICommunicatorActivity {
    private int roomId;
    private String username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.room_activity);

        getSupportActionBar().hide();

        roomId = Integer.valueOf(getIntent().getStringExtra("roomid"));
        username = getIntent().getStringExtra("username");

        Button leaveBtn = (Button) findViewById(R.id.leave_room_btn);

        final Activity thisActivity = this;
        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               goToLobby();
            }
        });
        //attachGameEventListener();
    }

    private void goToLobby() {

        Intent intent = new Intent(RoomActivity.this, LobbyActivity.class);

        this.finish();

        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        goToLobby();
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

    @Override
    public void onResume(){
        super.onResume();
        try {
            drawRoom(roomId, CommunicationManager.getInstance().getUserName());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawRoom(int roomId, final String username) {

        runOnUiThread(new Runnable() {
            public void run() {
                initializeRoom();
            }
        });

        String url ="http://amimelia-001-site1.itempurl.com/api/gamelobby/GetRoom?userName=" +
                username + "&roomId=" + roomId;
        System.out.println(url);
        final com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        System.out.println(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String roomName = "";
                            String ownerName = "";

                            if (jsonObject.has("OwnerUserName")) {
                                ownerName = jsonObject.getString("OwnerUserName");

                                TextView ownerView = (TextView) findViewById(R.id.owner_image_view);

                                ownerView.setText(ownerName);
                            }

                            final FloatingActionsMenu inviteButton = (FloatingActionsMenu) findViewById(R.id.invitefriends_button);

                            if (!username.equals(ownerName)) {

                                inviteButton.setVisibility(View.GONE);

                            } else {
                                Button waitingButton = (Button) findViewById(R.id.waiting_button);

                                waitingButton.setText("START GAME");

                                waitingButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startGameClicked();
                                    }
                                });

                                inviteButton.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
                                    @Override
                                    public void onMenuExpanded() {
                                        try {
                                            showInvitationDialog();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onMenuCollapsed() {
                                        System.out.println("Collapsed");
                                    }
                                });
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

    private void startGameClicked(){
        final String url ="http://amimelia-001-site1.itempurl.com/api/game/CreateGame?userName="
                + CommunicationManager.getInstance().getUserName() +"&roomId=" + roomId;
        final com.android.volley.RequestQueue queue = Volley.newRequestQueue(RoomActivity.this);

        System.out.println(url);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        System.out.println("Create Game result = " + response);
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

    public void tansparent(int roomid) {
        
       Intent intent = new Intent(RoomActivity.this, LiveGameActivity.class);
       intent.putExtra("roomid", roomid);

       startActivity(intent);
    }

    private void showInvitationDialog() {
        final Dialog dialog = new Dialog(RoomActivity.this);

        final FloatingActionsMenu inviteButton = (FloatingActionsMenu) findViewById(R.id.invitefriends_button);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.invitation_dialog);

        Button invitationButton = (Button) dialog.findViewById(R.id.send_invitation);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_invitation);

        invitationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editText = (EditText) dialog.findViewById(R.id.username_view);

                String invitee = editText.getText().toString();

                editText.setText("");

                System.out.println("username " + username);
                System.out.println("userto " + invitee);
                System.out.println("roomid " + roomId);

                final String url ="http://amimelia-001-site1.itempurl.com/api/gamelobby/SendGameInvitiation?userName="
                            + username + "&userTo=" + invitee + "&roomId=" + roomId;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        inviteFriend(url);
                    }
                });
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                inviteButton.collapse();
            }
        });

        dialog.show();
    }

    private void inviteFriend(String url) {
        final com.android.volley.RequestQueue queue = Volley.newRequestQueue(RoomActivity.this);

        System.out.println(url);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {

                        System.out.println(response);

                        if (response.equals("\"OK\"")) {
                            Toast toast = Toast.makeText(RoomActivity.this, "Invitation sent",
                                    Toast.LENGTH_SHORT);

                            toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 0);

                            toast.show();
                        } else {
                            Toast toast = Toast.makeText(RoomActivity.this, "User doesn't exist",
                                    Toast.LENGTH_SHORT);

                            toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 0);

                            toast.show();
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

    private void initializeRoom() {

        TextView ownerView = (TextView) findViewById(R.id.owner_image_view);

        ownerView.setText("Pending");

        Button waitingButton = (Button) findViewById(R.id.waiting_button);

        waitingButton.setText("WAITING TO START");

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

        imageView1.setImageResource(R.drawable.avatar_defaultmark);
        imageView2.setImageResource(R.drawable.avatar_defaultmark);
        imageView3.setImageResource(R.drawable.avatar_defaultmark);
        imageView4.setImageResource(R.drawable.avatar_defaultmark);
        imageView5.setImageResource(R.drawable.avatar_defaultmark);

        nameView1.setText("Pending");
        nameView2.setText("Pending");
        nameView3.setText("Pending");
        nameView4.setText("Pending");
        nameView5.setText("Pending");

        FloatingActionsMenu circleImageView = (FloatingActionsMenu) findViewById(R.id.invitefriends_button);

        circleImageView.setVisibility(View.VISIBLE);
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

        //dispachGameEventsListener();
        String url ="http://amimelia-001-site1.itempurl.com/api/Gamelobby/LeaveRoom?userName="
                + CommunicationManager.getInstance().getUserName();

        final com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        System.out.println(response);
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
        WriteLogMessage("Event in Activity : " + event.eventKey);
        if (event.IsSameEvent(GameEventFactory.EVENT_KET_UPDATE_ROOM)){
            drawRoom(roomId, CommunicationManager.getInstance().getUserName());
            return;
        }

        if (event.IsSameEvent(GameEventFactory.LIVE_EVENT_CREATED)){
            Log.w("Final", "Opening Game Activity");
            tansparent(Integer.parseInt((String)event.getEventData()));
            return;
        }
    }

    @Override
    public void connectionToServerEstabilished() {
        drawRoom(roomId, CommunicationManager.getInstance().getUserName());
    }
}
