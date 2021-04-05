package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.instagramclone.Adapter.PhotoAdapter;
import com.example.instagramclone.Models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SavedActivity extends AppCompatActivity {

    private List<Post> savedPostList;
    private PhotoAdapter savedPostAdapter;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar = findViewById(R.id.SavedToolBar_id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Saved");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerViewSavedPost = findViewById(R.id.recyclerViewSavesPost_id);
        recyclerViewSavedPost.setHasFixedSize(true);
        recyclerViewSavedPost.setLayoutManager(new GridLayoutManager(SavedActivity.this,3));
        savedPostList  = new ArrayList<>();
        savedPostAdapter = new PhotoAdapter(SavedActivity.this, savedPostList, "saved");
        recyclerViewSavedPost.setAdapter(savedPostAdapter);

        getSavedPost();
    }

    private void getSavedPost() {

        List<String> savedPostIds = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    savedPostIds.add(dataSnapshot.getKey());
                }

                FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        savedPostList.clear();

                        for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Post post  = dataSnapshot.getValue(Post.class);

                            for(String id : savedPostIds) {
                                assert post != null;
                                if(post.getPostId().equals(id)) {
                                    savedPostList.add(post);
                                }
                            }
                        }

                        savedPostAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}