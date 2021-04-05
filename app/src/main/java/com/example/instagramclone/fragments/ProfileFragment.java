package com.example.instagramclone.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.instagramclone.Adapter.PhotoAdapter;
import com.example.instagramclone.EditProfileActivity;
import com.example.instagramclone.FollowersFollowingActivity;
import com.example.instagramclone.Models.Post;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.PostActivity;
import com.example.instagramclone.R;
import com.example.instagramclone.RegLogActivity;
import com.example.instagramclone.SavedActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private PhotoAdapter photoAdapter;
    private List<Post> photoList;

    private TextView username;

    private CircleImageView userProfileImage;
    private TextView totalPost;
    private TextView totalFollowers;
    private TextView totalFollowing;

    private TextView fullName;
    private TextView bio;

    private Button followingStatus;

    private FirebaseUser firebaseUser;
    String profileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = (Objects.requireNonNull(getContext())).getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId", "none");
        if(data.equals("none")) {
            Log.d("from comment", "yes3");
            profileId = firebaseUser.getUid();
        } else {
            Log.d("from comment", "yes2");
            profileId = data;
            getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().clear().apply();
        }



        username = v.findViewById(R.id.PFUsername_id);
        fullName = v.findViewById(R.id.PFUserFullName_id);
        bio = v.findViewById(R.id.PFBio_id);
        userProfileImage = v.findViewById(R.id.PFUserProfileImage_id);
        totalPost = v.findViewById(R.id.PFNumberOfPosts_id);
        totalFollowers = v.findViewById(R.id.PFNumberOfFollowers_id);
        totalFollowing = v.findViewById(R.id.PFNumberOfFollowing_id);
        ImageView addPost = v.findViewById(R.id.PFAddPost_id);
        ImageView logout = v.findViewById(R.id.PFLogout_id);
        Button editProfile = v.findViewById(R.id.PFEditProfile_id);
        Button saved = v.findViewById(R.id.PFSaved_id);
        followingStatus = v.findViewById(R.id.PFFollowUnFollowButton);

        LinearLayout llPost = v.findViewById(R.id.profileFragmentPosts_id);
        LinearLayout llFollowers = v.findViewById(R.id.profileFragmentFollowers_id);
        LinearLayout llFollowing = v.findViewById(R.id.profileFragmentFollowing_id);

        RecyclerView recyclerViewPhotos = v.findViewById(R.id.recyclerViewPhotos_id);
        recyclerViewPhotos.setHasFixedSize(true);
        recyclerViewPhotos.setLayoutManager(new GridLayoutManager(getContext(),3));
        photoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), photoList, "profile");
        recyclerViewPhotos.setAdapter(photoAdapter);

         countPosts();
         userDetails();
         countFollowersFollowing();
         photos();

         if(!firebaseUser.getUid().equals(profileId)) {
             editProfile.setVisibility(View.GONE);
             saved.setVisibility(View.GONE);
             followingStatus.setVisibility(View.VISIBLE);
             checkFollowingOrNot();

             followingStatus.setOnClickListener(v18 -> {
                 String status = followingStatus.getText().toString();
                 if(status.equals("Follow")) {
                     FirebaseDatabase.getInstance().getReference().child("follow").
                             child(firebaseUser.getUid()).child("following").
                             child(profileId).setValue(true);

                     FirebaseDatabase.getInstance().getReference().child("follow").
                             child(profileId).child("follower").
                             child(firebaseUser.getUid()).setValue(true);
                 } else {
                     FirebaseDatabase.getInstance().getReference().child("follow").
                             child(firebaseUser.getUid()).child("following").
                             child(profileId).removeValue();

                     FirebaseDatabase.getInstance().getReference().child("follow").
                             child(profileId).child("follower").
                             child(firebaseUser.getUid()).removeValue();
                 }
             });
         }

         addPost.setOnClickListener(v17 -> startActivity(new Intent(getContext(), PostActivity.class)));

         logout.setOnClickListener(v16 -> {

             AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
             alertDialog.setTitle("Do you want to logout");

             alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No", (dialog, which) -> dialog.dismiss());

             alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", (dialog, which) -> {
                 FirebaseAuth.getInstance().signOut();

                 startActivity(new Intent(getContext(), RegLogActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                 dialog.dismiss();
             });

             alertDialog.show();
         });

         editProfile.setOnClickListener(v15 -> startActivity(new Intent(getContext(), EditProfileActivity.class)));

         saved.setOnClickListener(v14 -> startActivity(new Intent(getContext(), SavedActivity.class)));

         llPost.setOnClickListener(v13 -> {
         ///   not done yet ********************************************************************
         });

         llFollowers.setOnClickListener(v12 -> {
             Intent intent = new Intent(getContext(), FollowersFollowingActivity.class);
             intent.putExtra("title", username.getText().toString());
             intent.putExtra("id", profileId);
             intent.putExtra("from", "Follower");
             startActivity(intent);
         });

         llFollowing.setOnClickListener(v1 -> {
             Intent intent = new Intent(getContext(), FollowersFollowingActivity.class);
             intent.putExtra("title", username.getText().toString());
             intent.putExtra("id", profileId);
             intent.putExtra("from", "Following");
             startActivity(intent);
         });

         return v;
    }

    private void photos() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                photoList.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);

                    if(post.getPublisher().equals(profileId)) {
                        photoList.add(post);
                    }
                }

                Collections.reverse(photoList);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollowingOrNot() {
        FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(profileId).exists()) {
                    String status = "Following";
                    followingStatus.setText(status);
                } else {
                    String status = "Follow";
                    followingStatus.setText(status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countPosts() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int count = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);

                    assert post != null;
                    if (post.getPublisher().equals(profileId)) {
                        count++;
                    }
                }

                totalPost.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countFollowersFollowing() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("follow").child(profileId);

        databaseReference.child("follower").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalFollowers.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("following").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalFollowing.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userDetails() {

        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                assert user != null;
                username.setText(user.getUsername());
                fullName.setText(user.getName());
                bio.setText(user.getBio());

                if(user.getImage().equals("default")) {
                    userProfileImage.setImageResource(R.mipmap.ic_default_image);
                }
                else{
                    Picasso.get().load(user.getImage()).into(userProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}