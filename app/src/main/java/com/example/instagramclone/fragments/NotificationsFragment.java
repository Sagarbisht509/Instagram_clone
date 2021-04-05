package com.example.instagramclone.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagramclone.Adapter.NotificationAdapter;
import com.example.instagramclone.Models.Notification;
import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private List<Notification> notificationList;
    private NotificationAdapter notificationAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_notifications, container, false);

        RecyclerView recyclerViewNotification = v.findViewById(R.id.recyclerViewNotification_id);
        recyclerViewNotification.setHasFixedSize(true);
        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getContext(), notificationList);
        recyclerViewNotification.setAdapter(notificationAdapter);

        readNotifications();

        return v;
    }

    private void readNotifications() {

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    notificationList.add(dataSnapshot.getValue(Notification.class));
                }

                Collections.reverse(notificationList);

                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}