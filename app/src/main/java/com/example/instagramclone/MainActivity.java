package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    } else {
                        startActivity(new Intent(MainActivity.this,RegLogActivity.class));
                    }
                    finish();
                }
            },5000);
    }
}