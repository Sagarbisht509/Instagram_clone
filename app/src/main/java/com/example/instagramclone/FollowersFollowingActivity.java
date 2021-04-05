package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.instagramclone.Adapter.PageAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;


public class FollowersFollowingActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;

   // private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers_following);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String id = intent.getStringExtra("id");
       // String whichTab = intent.getStringExtra("from");

        SharedPreferences sharedPreferencesFollower = getSharedPreferences("TEMP_FOLLOWER",MODE_PRIVATE);
        sharedPreferencesFollower.edit().putString("ID_FOLLOWER",id).apply();

        SharedPreferences sharedPreferencesFollowing = getSharedPreferences("TEMP_FOLLOWING",MODE_PRIVATE);
        sharedPreferencesFollowing.edit().putString("ID_FOLLOWING",id).apply();

        TabLayout tabLayout = findViewById(R.id.tabLayout_id);
        viewPager2 = findViewById(R.id.viewPage_id);

        Toolbar toolbar = findViewById(R.id.FFToolBar_id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager(), getLifecycle(), tabLayout.getTabCount());
        viewPager2.setAdapter(pageAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}