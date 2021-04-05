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

public class FollowerFragment extends Fragment {

    private List<User> followersList;

    private UserAdapter userAdapter;

    private List<String> idList;

    private String profileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_follower, container, false);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = (getContext()).getSharedPreferences("TEMP_FOLLOWER", Context.MODE_PRIVATE).getString("ID_FOLLOWER","none");
        if(data.equals("none")) {
            profileId = firebaseUser.getUid();
        } else {
            profileId = data;
            getContext().getSharedPreferences("TEMP_FOLLOWER", Context.MODE_PRIVATE).edit().clear().apply();
        }

        RecyclerView recyclerViewFollowers = v.findViewById(R.id.recyclerViewFollowers_id);
        recyclerViewFollowers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFollowers.setHasFixedSize(true);
        followersList = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), followersList, "followerFragment");
        recyclerViewFollowers.setAdapter(userAdapter);

        idList = new ArrayList<>();

        getIds();

        return v;
    }

    private void getIds() {
        FirebaseDatabase.getInstance().getReference().child("follow").child(profileId)
                .child("follower").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    idList.add(dataSnapshot.getKey());
                }

                getFollowers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followersList.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    for(String id: idList) {
                        if(user.getId().equals(id)) {
                            followersList.add(user);
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