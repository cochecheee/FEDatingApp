package com.example.fedatingapp.utils;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.fedatingapp.R;
import com.example.fedatingapp.models.Notification;

public class NotificationUtils {
    private static final String CHANNEL_ID = "notify_channel";
    private static final String CHANNEL_NAME = "App Notifications";

    public static void showPushNotification(Context context, Notification notification) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Tạo Notification Channel cho Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Thông báo từ ứng dụng");
            notificationManager.createNotificationChannel(channel);
        }

        // Tạo thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Thay bằng icon của bạn
                .setContentTitle("Thông báo " + notification.getType())
                .setContentText(notification.getContent())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // Hiển thị thông báo với ID duy nhất
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
