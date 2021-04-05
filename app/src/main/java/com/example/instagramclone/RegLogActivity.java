package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class RegLogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_log);

        Button register = findViewById(R.id.buttonCreateNewAccount);
        Button login = findViewById(R.id.buttonLogin);

        register.setOnClickListener(v -> {
            RegLogActivity.this.startActivity(new Intent(RegLogActivity.this, RegisterActivity.class));
            finish();
        });

        login.setOnClickListener(v -> {
            startActivity(new Intent(RegLogActivity.this, LoginActivity.class));
            finish();
        });
    }
}