package com.example.navin.cryptbuzz;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mchannel=new NotificationChannel(Constants.channel_id,Constants.channel_name,NotificationManager.IMPORTANCE_HIGH);
        mchannel.setDescription(Constants.channel_des);
        mchannel.enableLights(true);
        mchannel.setLightColor(Color.RED);
        mchannel.enableVibration(true);
        mchannel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,100});
        notificationManager.createNotificationChannel(mchannel);
        }

    }

    public void price(View v) {
        if (v.getId() == R.id.price) {
            Intent i = new Intent(MainActivity.this, Price.class);
            startActivity(i);
        }
    }
    public void news(View v) {
        if (v.getId() == R.id.news) {
            Intent i = new Intent(MainActivity.this, News.class);
            startActivity(i);
        }
    }
    public void chatbot(View v) {
        if (v.getId() == R.id.chatbot) {
            Intent i = new Intent(MainActivity.this, Chatbot.class);
            startActivity(i);
        }
    }
    public void forum(View v) {
        if (v.getId() == R.id.forum) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://t.me/crypbuzz_ucc"));
            startActivity(intent);
        }
    }

    public void know(View v) {
        if (v.getId() == R.id.know) {
            Intent i = new Intent(MainActivity.this, Know.class);
            startActivity(i);
        }
    }

    public void aboutus(View v) {
        if (v.getId() == R.id.aboutus) {
            Intent i = new Intent(MainActivity.this, Aboutus.class);
            startActivity(i);
        }
    }

    public void alert(View v) {
        if (v.getId() == R.id.alert) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://coinmarketalert.com/"));
            startActivity(intent);
        }
    }

}