package com.example.truekeycar.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import com.example.truekeycar.Constants.AllConstants;
import com.example.truekeycar.Log_in;
import com.example.truekeycar.MainActivity;
import com.example.truekeycar.Mandar;
import com.example.truekeycar.R;
import com.example.truekeycar.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class FirebaseMessageReceiver extends FirebaseMessagingService {

  //  private Util util = new Util();


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {


            Map<String, String> map = remoteMessage.getData();
            String title = map.get("title");
            String message = map.get("message");
            String hisID = map.get("hisID");
           /* String hisImage = map.get("hisImage");
            String chatID = map.get("chatID");*/

        assert hisID != null;
        if (!hisID.equals("Opcion"))
            createOreoNotification(title, message);
        else
            createOreoNotificationOpcion(title, message);


        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        updateToken(s);
        super.onNewToken(s);
    }

    private void updateToken(String token) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        SessionManager sessionManager =  new SessionManager(this,SessionManager.SESSION_USERSESSION);
        HashMap<String,String> phone = sessionManager.getUsersDetailFromSession();
        String numero =  phone.get(SessionManager.KEY_PHONENO);
        if (firebaseAuth.getCurrentUser() != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios").child(numero).child("token");
            Map<String, Object> map = new HashMap<>();
            map.put("token", token);
            databaseReference.updateChildren(map);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createOreoNotification(String title, String message) {

        NotificationChannel channel = new NotificationChannel(AllConstants.CHANNEL_ID, "Message", NotificationManager.IMPORTANCE_HIGH);
        channel.setShowBadge(true);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new Notification.Builder(this, AllConstants.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(ResourcesCompat.getColor(getResources(), R.color.green, null))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        manager.notify(100, notification);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createOreoNotificationOpcion(String title, String message) {

        NotificationChannel channel = new NotificationChannel(AllConstants.CHANNEL_ID, "Message", NotificationManager.IMPORTANCE_HIGH);
        channel.setShowBadge(true);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        Intent intent = new Intent(this, Log_in.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        Intent mandar = new Intent(this, Mandar.class);
        mandar.putExtra("Notificacion","NoAuth");
        mandar.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent Noauth = PendingIntent.getActivity(this, 0, mandar, PendingIntent.FLAG_IMMUTABLE);


        Notification.Action apagar_coche = new Notification.Action.Builder(Icon.createWithResource(this,R.drawable.logo),"Apagar coche",Noauth).build();

        Notification notification;
        notification = new Notification.Builder(this, AllConstants.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(ResourcesCompat.getColor(getResources(), R.color.green, null))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .addAction(apagar_coche)
                .setAutoCancel(true)
                .build();
        manager.notify(100, notification);

    }

}
