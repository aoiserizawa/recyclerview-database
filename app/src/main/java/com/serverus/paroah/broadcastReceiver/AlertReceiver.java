package com.serverus.paroah.broadcastReceiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.serverus.paroah.R;
import com.serverus.paroah.activities.MainActivity;
import com.serverus.paroah.activities.ReminderPreviewActivity;

/**
 * Created by alvinvaldez on 8/21/15.
 */
public class AlertReceiver extends BroadcastReceiver {

    private int id;
    // Called when a broadcast is made targeting this class
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        String title = bundle.getString("title");
        String time = bundle.getString("time");
        id = bundle.getInt("id");

        createNotification(context, title, time, "Pharoah Reminder");
    }

    public void createNotification(Context context, String msg, String msgText,  String msgAlert){
        Intent reminderActivity =  new Intent(context, ReminderPreviewActivity.class);
        reminderActivity.putExtra("id", id);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // All the parents of SecondActivity will be added to task stack.
        stackBuilder.addParentStack(MainActivity.class);
        // Add a SecondActivity intent to the task stack.
        stackBuilder.addNextIntentWithParentStack(reminderActivity);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);

//        PendingIntent notificationIntent = PendingIntent.getActivity(context, id,
//                reminderActivity, PendingIntent.FLAG_UPDATE_CURRENT );

        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(msg)
                .setTicker(msgAlert)
                .setContentText(msgText)
                .setContentIntent(pendingIntent);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(id, mBuilder.build());
    }
}
