package com.example.instagramclone.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.HomeActivity;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.R;
import com.example.instagramclone.fragments.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final Context mContext;
    private final List<User> mUsers;
    private final String from;

    private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<User> mUsers, String from) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.from = from;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        User user = mUsers.get(position);

        holder.follow.setVisibility(View.VISIBLE);

        holder.userName.setText(user.getUsername());
        holder.fullName.setText(user.getName());

        Picasso.get().load(user.getImage()).placeholder(R.mipmap.ic_default_profile_image_round).into((holder.imageProfile));

        if(from.equals("followerFragment")) {
            holder.follow.setText("Remove");
        } else if(from.equals("followingFragment")) {
            holder.follow.setText("Following");
        } else {
            isFollow(user.getId(), holder.follow);
        }

        if(user.getId().equals(firebaseUser.getUid())){
            holder.follow.setVisibility(View.GONE);
        }

        holder.follow.setOnClickListener(v -> {
            if(holder.follow.getText().toString().equals("follow")) {
                FirebaseDatabase.getInstance().getReference().child("follow")
                        .child(firebaseUser.getUid())
                        .child("following")
                        .child(user.getId()).setValue(true);

                FirebaseDatabase.getInstance().getReference().child("follow")
                        .child(user.getId()).child("follower")
                        .child(firebaseUser.getUid()).setValue(true);

                addNotification(user.getId());

            }
            else if(holder.follow.getText().toString().equals("Remove")) {
                FirebaseDatabase.getInstance().getReference().child("follow")
                        .child(firebaseUser.getUid())
                        .child("follower")
                        .child(user.getId()).removeValue();

                FirebaseDatabase.getInstance().getReference().child("follow")
                        .child(user.getId()).child("following")
                        .child(firebaseUser.getUid()).removeValue();
            }
            else {
                FirebaseDatabase.getInstance().getReference().child("follow")
                        .child(firebaseUser.getUid())
                        .child("following")
                        .child(user.getId()).removeValue();

                FirebaseDatabase.getInstance().getReference().child("follow")
                        .child(user.getId()).child("follower")
                        .child(firebaseUser.getUid()).removeValue();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if(from.equals("searchFragment")){
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId",user.getId()).apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_id, new ProfileFragment()).commit();
            } else {
                Intent intent = new Intent(mContext, HomeActivity.class);
                intent.putExtra("publisherId", user.getId());
                mContext.startActivity(intent);
            }
        });
    }

    private void addNotification(String id) {
        HashMap<String,Object> map = new HashMap<>();

        map.put("userId", firebaseUser.getUid());
        map.put("notificationText", " started following you.");
        map.put("postId", "");
        map.put("fromPost", false);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(id).push().setValue(map);
    }

    private void isFollow(final String id, final Button follow) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(id).exists()) {
                    follow.setText("following");
                }
                else {
                    follow.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView imageProfile;
        public TextView userName;
        public TextView fullName;
        public Button follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.circularProfileImage);
            userName = itemView.findViewById(R.id.searchedUserName);
            fullName = itemView.findViewById(R.id.searchedUserFullName);
            follow = itemView.findViewById(R.id.followButton);
        }
    }
}
