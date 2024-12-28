package com.example.mobileapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class EventNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String eventName = intent.getStringExtra("eventName");
        String eventDescription = intent.getStringExtra("eventDescription");
        int eventId = intent.getIntExtra("eventId", 0);

        // Lấy âm thanh thông báo mặc định
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "events_channel")
                .setSmallIcon(R.drawable.ic_notification) // Thêm icon thông báo
                .setContentTitle(eventName)
                .setContentText(eventDescription)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                // Thêm âm thanh thông báo
                .setSound(defaultSoundUri);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(eventId, builder.build());
        }
    }
}