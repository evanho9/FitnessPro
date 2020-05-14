package evanh.fitnesspro.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import evanh.fitnesspro.R;
import evanh.fitnesspro.activity.LoginActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context);

    }

    void showNotification(Context context) {
        String CHANNEL_ID = "FitnessPro";
        CharSequence name = context.getResources().getString(R.string.app_name);
        NotificationCompat.Builder mBuilder;
        Intent notificationIntent = new Intent(context, LoginActivity.class);
        Bundle bundle = new Bundle();
        notificationIntent.putExtras(bundle);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= 26) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(mChannel);
            mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLights(Color.RED, 300, 300)
                    .setChannelId(CHANNEL_ID)
                    .setContentTitle("Workout");
        } else {
            mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentTitle("Workout");
        }

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setContentText("Time for daily exercise");
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(1, mBuilder.build());
    }
}
