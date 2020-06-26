package com.example.getitdonev2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private LottieAnimationView lottieAnimationView;
    public static final String CHANNEL_1_ID = "channelStartDate";
    public static final String CHANNEL_2_ID = "channelPause";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificatonChannel();
        //createNotificationsChannels();


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        //remove the button login item
        //bottomNavigationView.getMenu().removeItem(R.id.loginFragment);

         NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
         NavigationUI.setupWithNavController(bottomNavigationView, navController);
         NavigationUI.setupActionBarWithNavController(this, navController);


    }

    private void  createNotificatonChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Notifications for tasks";
            String description = "Pause time and work time";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel1", name, importance);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

//    private void createNotificationsChannels(){
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channelStart = new NotificationChannel(CHANNEL_1_ID, "Channel Start", NotificationManager.IMPORTANCE_DEFAULT);
//            channelStart.setDescription("Starting channel");
//
//            //Channel 2
//            NotificationChannel channelPause = new NotificationChannel(CHANNEL_2_ID, "Channel Pause", NotificationManager.IMPORTANCE_DEFAULT);
//            channelPause.setDescription("Pause channel");
//
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channelStart);
//            manager.createNotificationChannel(channelPause);
//        }
//    }



}
