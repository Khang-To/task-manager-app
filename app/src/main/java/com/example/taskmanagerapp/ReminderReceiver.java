package com.example.taskmanagerapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderReceiver extends BroadcastReceiver {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @Override
    public void onReceive(Context context, Intent intent) {
        int taskId = intent.getIntExtra("taskId", -1);
        String tenCV = intent.getStringExtra("tenCV");
        String ngayDenHan = intent.getStringExtra("ngayDenHan");

        // Tạo notification channel nếu chạy trên Android Oreo (API 26) trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Task Reminder Channel";
            String description = "Channel for task reminder notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("TASK_CHANNEL_ID", name, importance);
            channel.setDescription(description);

            // Đăng ký kênh thông báo
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Tạo thông báo
        String contentText ="";
        if (ngayDenHan != null && !ngayDenHan.isEmpty()) {
            contentText = "Tên công việc: "+tenCV+". Ngày đến hạn: " + ngayDenHan+"\nMau hoàn thành xong bạn nhé!";  // Thêm ngày đến hạn vào thông báo
        }
        else
            contentText = "Tên công việc: "+tenCV+"\nMau hoàn thành xong bạn nhé!";  // Thêm ngày đến hạn vào thông báo


        // Tạo thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "TASK_CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_notification) // icon bạn có
                .setContentTitle("Nhắc nhở công việc!")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Tạo Notification Manager để gửi thông báo
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // ReminderReceiver.java
        notificationManager.notify(taskId, builder.build());
    }

}

