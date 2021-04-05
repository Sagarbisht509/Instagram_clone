package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.instagramclone.fragments.HomeFragment;
import com.example.instagramclone.fragments.NotificationsFragment;
import com.example.instagramclone.fragments.ProfileFragment;
import com.example.instagramclone.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private Fragment selectedFragment = null;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottomNavigationView_id);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.home_id:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.search_id:
                        selectedFragment = new SearchFragment();
                        break;
                    case R.id.post_id:
                        selectedFragment = null;
                        startActivity(new Intent(HomeActivity.this, PostActivity.class));
                        break;
                    case R.id.favorite_id:
                        selectedFragment = new NotificationsFragment();
                        break;
                    case R.id.profile_id:
                        selectedFragment = new ProfileFragment();
                        break;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_id, selectedFragment).commit();
                }

                return true;
            }
        });

        Bundle intent = getIntent().getExtras();
        if(intent!=null) {
            String profileId = intent.getString("publisherId" );

            getSharedPreferences("PROFILE", MODE_PRIVATE).edit().putString("profileId", profileId).apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_id, new ProfileFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.profile_id);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_id, new HomeFragment()).commit();
        }
    }

    @Override
    public void onBackPressed() {

        if(bottomNavigationView.getSelectedItemId() == R.id.home_id) {
            super.onBackPressed();
            finish();
        } else {
            bottomNavigationView.setSelectedItemId(R.id.home_id);
        }

    }

}