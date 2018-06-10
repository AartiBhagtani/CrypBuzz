package com.example.navin.cryptbuzz;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import static com.example.navin.cryptbuzz.Constants.channel_id;

public class MyNotificationManager {
    private Context mctx;
    private static MyNotificationManager mInstance;
    private MyNotificationManager(Context context){
        mctx=context;

    }
    public static synchronized MyNotificationManager getInstance(Context context){
        if(mInstance==null){
            mInstance=new MyNotificationManager(context);

        }
        return mInstance;
    }
    public void displayNotification(String title , String Body){
        NotificationCompat.Builder mbuilder=new NotificationCompat.Builder(mctx,Constants.channel_id)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(title)
                .setContentText(Body);

        Intent i=new Intent(mctx,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(mctx,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
        mbuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager= (NotificationManager) mctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager!=null){
            notificationManager.notify(1,mbuilder.build());
        }
    }

}
