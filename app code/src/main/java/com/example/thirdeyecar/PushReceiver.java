package com.example.thirdeyecar;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;

public class PushReceiver extends BroadcastReceiver {
    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_STREAM_URL = "stream_url";
    private static final String KEY_PARK_MODE = "park_mode";
    private static final String CHANNEL_ID = "default";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isParkMode = prefs.getBoolean(KEY_PARK_MODE, true);

        int id = intent.getIntExtra("id", 0);
        String message = intent.getStringExtra("message");
        String type = intent.getStringExtra("type");
        String url = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");

        SharedPreferences.Editor editor = prefs.edit();

        if ("type2".equals(type) && url != null) {
            editor.putString(KEY_STREAM_URL, url);
            editor.apply();
            Log.d("PushReceiver", "Stored URL from Type 2: " + url);
        }

        Intent ignoreIntent = new Intent(context, PushReceiver.class);
        ignoreIntent.setAction("IGNORE_ACTION");
        ignoreIntent.putExtra("notification_id", id);
        PendingIntent ignorePendingIntent = PendingIntent.getBroadcast(context, 0, ignoreIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent streamIntent = new Intent(context, WebViewActivity.class); 
        streamIntent.putExtra("url", url);
        PendingIntent streamPendingIntent;

        if (isParkMode) {
            streamIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            streamPendingIntent = PendingIntent.getActivity(context, 1, streamIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 3, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder driveModeBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setAutoCancel(true)
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setContentTitle("Drive Mode Alert")
                    .setContentText("Please switch to Park mode to stream video.")
                    .setLights(Color.RED, 1000, 1000)
                    .setVibrate(new long[]{0, 400, 250, 400})
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(mainPendingIntent);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(id + 1, driveModeBuilder.build());

            Intent mainFallbackIntent = new Intent(context, MainActivity.class);
            mainFallbackIntent.putExtra("url", url);
            mainFallbackIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            streamPendingIntent = PendingIntent.getActivity(context, 2, mainFallbackIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message != null ? message : "Test notification")
                .setLights(Color.RED, 1000, 1000)
                .setVibrate(new long[]{0, 400, 250, 400})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(streamPendingIntent); 

        if ("type1".equals(type)) {
            builder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Ignore", ignorePendingIntent)
                    .addAction(android.R.drawable.ic_menu_view, "Stream", streamPendingIntent);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Notification Channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notification Channel Description");
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }
}
