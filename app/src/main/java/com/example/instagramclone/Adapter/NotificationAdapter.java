package com.example.instagramclone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.Models.Notification;
import com.example.instagramclone.Models.Post;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.R;
import com.example.instagramclone.fragments.PostDetailFragment;
import com.example.instagramclone.fragments.ProfileFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final Context context;
    private final List<Notification> notificationList;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);

        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Notification notification = notificationList.get(position);

        getNotificationUser(holder.userProfileImage, holder.username, notification.getUserId());

        if(notification.getFromPost()) {
            holder.postImage.setVisibility(View.VISIBLE);

            getPostImage(holder.postImage, notification.getPostId());
        } else {
            holder.postImage.setVisibility(View.GONE);
        }

        holder.text.setText(notification.getNotificationText());

        holder.itemView.setOnClickListener(v -> {
            if(notification.getFromPost()) {
                  context.getSharedPreferences("POST",Context.MODE_PRIVATE)
                          .edit().putString("postId",notification.getPostId()).apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer_id, new PostDetailFragment()).commit();
            } else {
                context.getSharedPreferences("PROFILE",Context.MODE_PRIVATE)
                        .edit().putString("profileId",notification.getUserId()).apply();


                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer_id, new ProfileFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView userProfileImage;
        private final TextView username;
        private final TextView text;
        private final ImageView postImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userProfileImage = itemView.findViewById(R.id.notificationUserImage_id);
            username = itemView.findViewById(R.id.notificationUsername_id);
            text = itemView.findViewById(R.id.notificationText_id);
            postImage = itemView.findViewById(R.id.notificationPost_id);
        }
    }


    private void getPostImage(ImageView postImage, String postId) {
        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);

                Picasso.get().load(post.getImageUrl()).into(postImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getNotificationUser(CircleImageView imageView, TextView textView, String userId) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                assert user != null;
                textView.setText(user.getUsername());

                if(user.getImage().equals("default")) {
                    imageView.setImageResource(R.mipmap.ic_default_image);
                } else {
                    Picasso.get().load(user.getImage()).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
