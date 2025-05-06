package com.example.fedatingapp.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.fedatingapp.R;
import com.example.fedatingapp.models.Notification;

import java.util.Random;

public class NotificationUtils {
    private static final String CHANNEL_ID = "notify_channel";
    private static final String CHANNEL_NAME = "App Notifications";

    public static void showPushNotification(Context context, Notification notification) {
        if (context == null || notification == null) return; // Thêm kiểm tra tránh crash

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Tạo Notification Channel cho Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH // HIGH để dễ thấy hơn
            );
            channel.setDescription("Thông báo từ ứng dụng");
            notificationManager.createNotificationChannel(channel);
        }

        // Tạo thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Icon phải là dạng trắng đen (vector) với Android 8+
                .setContentTitle("Thông báo " + notification.getType())
                .setContentText(notification.getContent())
                .setPriority(NotificationCompat.PRIORITY_HIGH) // HIGH để ưu tiên hiển thị
                .setAutoCancel(true);

        // Hiển thị thông báo với ID hợp lệ
        int notificationId = new Random().nextInt(); // Đảm bảo ID luôn hợp lệ
        notificationManager.notify(notificationId, builder.build());
    }
}
