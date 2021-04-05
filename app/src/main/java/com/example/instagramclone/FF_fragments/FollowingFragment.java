package com.example.instagramclone.FF_fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagramclone.Adapter.UserAdapter;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowingFragment extends Fragment {

    private List<User> followingList;

    private UserAdapter userAdapter;

    private List<String> idList;

    private String profileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_following, container, false);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = (getContext()).getSharedPreferences("TEMP_FOLLOWING", Context.MODE_PRIVATE).getString("ID_FOLLOWING","none");
        if(data.equals("none")) {
            profileId = firebaseUser.getUid();
        } else {
            profileId = data;
            getContext().getSharedPreferences("TEMP_FOLLOWING", Context.MODE_PRIVATE).edit().clear().apply();
        }

        RecyclerView recyclerViewFollowing = v.findViewById(R.id.recyclerViewFollowing_id);
        recyclerViewFollowing.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFollowing.setHasFixedSize(true);
        followingList = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), followingList, "followingFragment");
        recyclerViewFollowing.setAdapter(userAdapter);

        idList = new ArrayList<>();

        getIds();
        return v;
    }

    private void getIds() {
        FirebaseDatabase.getInstance().getReference().child("follow").child(profileId)
                .child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();


                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    idList.add(dataSnapshot.getKey());
                }


                getFollowing();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowing() {
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    for(String id: idList) {
                        if(user.getId().equals(id)) {
                            followingList.add(user);
                        }
                    }
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}