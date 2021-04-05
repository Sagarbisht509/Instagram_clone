package com.example.instagramclone.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.instagramclone.Adapter.PostAdapter;
import com.example.instagramclone.Models.Post;
import com.example.instagramclone.PostActivity;
import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

  //  private RecyclerView recyclerViewStory;

    private PostAdapter postAdapter;
    private List<Post> postList;
    private List<String> followingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        ImageView newPost = v.findViewById(R.id.addPost_id);
        newPost.setOnClickListener(v1 -> startActivity(new Intent(getContext(),PostActivity.class)));

        RecyclerView recyclerViewPost = v.findViewById(R.id.recyclerViewPost_id);
        recyclerViewPost.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);  /// latest post on the top
        linearLayoutManager.setReverseLayout(true);
        recyclerViewPost.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerViewPost.setAdapter(postAdapter);

        followingList = new ArrayList<>();

        check();

        return v;
    }

    private void check() {
        FirebaseDatabase.getInstance().getReference().child("follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    followingList.add(dataSnapshot.getKey());
                }

                followingList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                readPost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readPost() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);

                    for(String id: followingList) {
                        if(post.getPublisher().equals(id)) {
                            postList.add(post);
                        }
                    }
                }

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}