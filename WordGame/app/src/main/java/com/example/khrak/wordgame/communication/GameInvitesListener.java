package com.example.khrak.wordgame.communication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.khrak.wordgame.Activities.NotificationHandlerActivity;
import com.example.khrak.wordgame.AppMain;
import com.example.khrak.wordgame.R;
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
        showNotification(context, eventData.roomId, CommunicationManager.getInstance().getUserName());
    }

    private void showNotification(Context context, int roomId, String userName) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle("Invitation received")
                        .setContentText(userName + " sent invitation");

        MediaPlayer mp = MediaPlayer.create(context, R.raw.sound);
        mp.start();

        Intent resultIntent = new Intent(context, NotificationHandlerActivity.class);

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
}
