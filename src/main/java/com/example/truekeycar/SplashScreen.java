package com.example.truekeycar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;


public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_SCREEN = 1000;
    Animation bottomAnim;
    TextView logo_text;
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        //hooks
        logo_text = findViewById(R.id.text_Splash);
        logo_text.setAnimation(bottomAnim);
        token = FirebaseMessaging.getInstance().getToken().toString();
        new Handler().postDelayed(() ->{
                Intent intent;
                intent = new Intent(SplashScreen.this, Log_in.class).putExtra("Token",token);
                startActivity(intent);
                finish();
        }, SPLASH_SCREEN);
    }
}