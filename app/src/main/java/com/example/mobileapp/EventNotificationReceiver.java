package com.example.mobileapp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventNotificationReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_GROUP_ID = 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Lấy ngày hiện tại
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String todayDate = sdf.format(new Date());

        // Lấy danh sách sự kiện trong ngày
        EventDatabaseHandler eventDb = new EventDatabaseHandler(context);
        List<Event> todayEvents = eventDb.getEventsByDate(todayDate);

        // Lấy âm thanh thông báo mặc định
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Tạo NotificationCompat.Builder cho nhóm thông báo
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Today's Events");

        // Tạo Intent để mở MainActivity và chuyển đến CalendarFragment
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mainIntent.putExtra("NAVIGATE_TO", "CALENDAR_FRAGMENT");

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Tạo thông báo chung
        NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(context, "events_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Events Today")
                .setContentText(todayEvents.size() + " events scheduled")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setGroup(String.valueOf(NOTIFICATION_GROUP_ID))
                .setGroupSummary(true);

        // Tạo thông báo cho từng sự kiện
        for (Event event : todayEvents) {
            // Tạo Intent riêng cho từng event
            Intent eventIntent = new Intent(context, MainActivity.class);
            eventIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            eventIntent.putExtra("NAVIGATE_TO", "CALENDAR_FRAGMENT");
            eventIntent.putExtra("EVENT_ID", event.getEventId());

            PendingIntent eventPendingIntent = PendingIntent.getActivity(
                    context,
                    event.getEventId(),
                    eventIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "events_channel")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(event.getName())
                    .setContentText(event.getDescription())
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(eventPendingIntent)
                    .setGroup(String.valueOf(NOTIFICATION_GROUP_ID));

            // Thêm vào InboxStyle
            inboxStyle.addLine(event.getName());

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                // Hiển thị từng thông báo riêng
                notificationManager.notify(event.getEventId(), builder.build());
            }
        }

        // Thiết lập InboxStyle cho thông báo tổng
        summaryBuilder.setStyle(inboxStyle);

        // Hiển thị thông báo tổng
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(NOTIFICATION_GROUP_ID, summaryBuilder.build());
        }
    }
}