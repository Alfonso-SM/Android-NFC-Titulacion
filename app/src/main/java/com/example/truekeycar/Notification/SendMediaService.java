package com.example.truekeycar.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


import com.example.truekeycar.R;

import java.util.ArrayList;

public class SendMediaService extends Service {

    private NotificationCompat.Builder builder;
    private NotificationManager manager;
    private String hisID, chatID;
    private int MAX_PROGRESS;
   // private Util util = new Util();
    private ArrayList<String> images;


    public SendMediaService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        hisID = intent.getStringExtra("hisID");
        chatID = intent.getStringExtra("chatID");
        images = intent.getStringArrayListExtra("media");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            createChannel();

        startForeground(100, getNotification().build());
        builder.setContentTitle("Sending Completed")
                .setProgress(0, 0, false);
        manager.notify(600, builder.build());
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    private NotificationCompat.Builder getNotification() {

        builder = new NotificationCompat.Builder(this, "android")
                .setContentText("Sending Media")
                .setProgress(MAX_PROGRESS, 0, false)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher);

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(600, builder.build());
        return builder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {

        NotificationChannel channel = new NotificationChannel("android", "Message", NotificationManager.IMPORTANCE_HIGH);
        channel.setShowBadge(true);
        channel.setLightColor(R.color.green);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        channel.setDescription("Sending Media");
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

    }

}