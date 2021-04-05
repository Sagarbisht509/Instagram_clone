package com.example.instagramclone.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagramclone.Adapter.TagAdapter;
import com.example.instagramclone.Adapter.UserAdapter;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private List<User> searchUsersList;
    private UserAdapter userAdapter;

    private SocialAutoCompleteTextView searchBar;

    private List<String> hashTagList;
    private List<String> hashTagCount;
    private TagAdapter tagAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewSearchedUser);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchUsersList = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(),searchUsersList,"searchFragment");
        recyclerView.setAdapter(userAdapter);

        RecyclerView recyclerViewTags = view.findViewById(R.id.recyclerViewSearchedHashTags);
        recyclerViewTags.setHasFixedSize(true);
        recyclerViewTags.setLayoutManager(new LinearLayoutManager(getContext()));
        hashTagList = new ArrayList<>();
        hashTagCount = new ArrayList<>();
        tagAdapter = new TagAdapter(getContext(), hashTagList, hashTagCount);
        recyclerViewTags.setAdapter(tagAdapter);

        searchBar = view.findViewById(R.id.searchBar_id);

        readTags();
        readUsers();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        return view;
    }

    private void readTags() {
        FirebaseDatabase.getInstance().getReference().child("HashTags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hashTagList.clear();
                hashTagCount.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    hashTagList.add(dataSnapshot.getKey());
                    hashTagCount.add(dataSnapshot.getChildrenCount()+"");
                }

                tagAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(TextUtils.isEmpty(searchBar.getText().toString())){
                    searchUsersList.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        User user = dataSnapshot.getValue(User.class);
                        searchUsersList.add(user);
                    }

                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchUser(String s) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("Username").startAt(s).endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                searchUsersList.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    searchUsersList.add(user);
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filter(String text) {
        List<String> searchedTags = new ArrayList<>();
        List<String> searchedTagsCount = new ArrayList<>();

        for(String s: hashTagList) {
            if(s.toLowerCase().contains(text.toLowerCase())) {
                searchedTags.add(s);
                searchedTagsCount.add(hashTagCount.get(hashTagList.indexOf(s)));
            }
        }

        tagAdapter.filter(searchedTags,searchedTagsCount);
    }

}