package com.example.khrak.wordgame.communication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.khrak.wordgame.Activities.LobbyActivity;
import com.example.khrak.wordgame.Activities.RoomActivity;
import com.example.khrak.wordgame.AppMain;
import com.example.khrak.wordgame.R;
import com.example.khrak.wordgame.TestActivity;
import com.example.khrak.wordgame.communication.models.GameEvent;
import com.example.khrak.wordgame.communication.models.GameEventFactory;
import com.example.khrak.wordgame.communication.models.InviteGameEventData;

/**
 * Created by melia on 8/3/2017.
 */

public class GameInvitesListener implements IGameEventsListener {

    @Override
    public void processGameEvent(GameEvent event) {
        if (!event.IsSameEvent(GameEventFactory.EVENT_KET_INVITATION_RECEVED))
            return;

        InviteGameEventData eventData = (InviteGameEventData) event.getEventData();

        Context context = AppMain.getContext();

        Log.w("InviteRequestReceived", "roomid = " + eventData.roomId + " user = " + eventData.inviteAuthor.UserName);

        System.out.println("Joining room");
        joinRoom(CommunicationManager.getInstance().getUserName(), eventData.roomId, context);
    }

    private void showNotification(Context context, int roomId, String userName) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");

        MediaPlayer mp = MediaPlayer.create(context, R.raw.sound);
        mp.start();

        Intent resultIntent = new Intent(context, RoomActivity.class);

        System.out.println("Show notification for " + userName + " " + roomId);

        resultIntent.putExtra("username", userName);
        resultIntent.putExtra("roomid", "" + roomId);

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        mBuilder.setAutoCancel(true);

        int mNotificationId = 001;
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    private void joinRoom(final String username, final int roomid, final Context context) {
        final com.android.volley.RequestQueue queue = Volley.newRequestQueue(context);

        String keyPath = "http://amimelia-001-site1.itempurl.com/api/gamelobby/JoinRoom?userName="
                + username + "&roomId=" + roomid;

        System.out.println("Key path iss thiis");
        System.out.println(keyPath);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, keyPath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {

                        System.out.println("Response");

                        showNotification(context, roomid, username);

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
}
