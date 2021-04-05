package com.example.instagramclone.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.CommentActivity;
import com.example.instagramclone.Models.Post;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.R;
import com.example.instagramclone.fragments.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final Context context;
    private final List<Post> postList;

    private final FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Post post = postList.get(position);
        Picasso.get().load(post.getImageUrl()).into(holder.postImage);
        holder.caption.setText(post.getCaption());

        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                assert user != null;
                if (user.getImage().equals("default")) {
                    holder.postUserImage.setImageResource(R.mipmap.ic_default_image);
                } else {
                    Picasso.get().load(user.getImage()).into(holder.postUserImage);
                }
                holder.postUsername.setText(user.getUsername());
                holder.postAuthor.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        isLiked(post.getPostId(), holder.like);
        countNumberOfLikes(post.getPostId(), holder.numberOfLikes);
        countNumberOfComments(post.getPostId(), holder.numberOfComments);
        isPostSaved(post.getPostId(), holder.save);

        holder.like.setOnClickListener(v -> {
            if (holder.like.getTag().equals("like")) {
                FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);

                if(!post.getPublisher().equals(firebaseUser.getUid()))
                    addNotification(post.getPostId(), post.getPublisher());

            } else {
                FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
            }
        });

        holder.comment.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("postId", post.getPostId());
            intent.putExtra("authorId", post.getPublisher());

            context.startActivity(intent);
        });

        holder.numberOfComments.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("postId", post.getPostId());
            intent.putExtra("authorId", post.getPublisher());

            context.startActivity(intent);
        });

        holder.save.setOnClickListener(v -> {
            if(holder.save.getTag().equals("save")) {
                FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostId()).setValue(true);
            } else {
                FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostId()).removeValue();
            }
        });

        holder.postUserImage.setOnClickListener(v -> {
            context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId",post.getPublisher()).apply();

            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_id, new ProfileFragment()).commit();
        });

        holder.postUsername.setOnClickListener(v -> {
            context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId",post.getPublisher()).apply();

            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_id, new ProfileFragment()).commit();
        });

        holder.postAuthor.setOnClickListener(v -> {
            context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId",post.getPublisher()).apply();

            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_id, new ProfileFragment()).commit();
        });

//        holder.postImage.setOnClickListener(v -> {
//            context.getSharedPreferences("POST", Context.MODE_PRIVATE).edit().putString("postId",post.getPostId()).apply();
//
//            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_id,new PostDetailFragment()).commit();
//        });
    }

    private void addNotification(String postId, String publisher) {
        HashMap<String,Object> map = new HashMap<>();

        map.put("userId", firebaseUser.getUid());
        map.put("notificationText", " liked your photo.");
        map.put("postId",postId);
        map.put("fromPost", true);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(publisher).push().setValue(map);
    }

    private void isPostSaved(String postId, ImageView save) {
        FirebaseDatabase.getInstance().getReference().child("Saves").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postId).exists()) {
                    save.setImageResource(R.drawable.ic_saved);
                    save.setTag("saved");
                } else {
                    save.setImageResource(R.drawable.ic_save);
                    save.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView postUserImage;
        public ImageView postImage;
        public ImageView more;
        public ImageView like;
        public ImageView comment;
        public ImageView save;

        public TextView postUsername;
        public TextView numberOfLikes;
        public TextView numberOfComments;
        public TextView postAuthor;
        SocialTextView caption;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postUserImage = itemView.findViewById(R.id.postUserImage_id);
            postUsername = itemView.findViewById(R.id.postUsername_id);
            more = itemView.findViewById(R.id.more_id);
            postImage = itemView.findViewById(R.id.postImage_id);
            like = itemView.findViewById(R.id.like_id);
            comment = itemView.findViewById(R.id.comment_id);
            save = itemView.findViewById(R.id.save_id);
            numberOfLikes = itemView.findViewById(R.id.postLikes_id);
            caption = itemView.findViewById(R.id.cation_id);
            postAuthor = itemView.findViewById(R.id.postAuthor_id);
            numberOfComments = itemView.findViewById(R.id.postComments_id);
        }
    }

    private void isLiked(String postId, ImageView image) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    image.setImageResource(R.drawable.ic_liked);
                    image.setTag("liked");
                } else {
                    image.setImageResource(R.drawable.ic_like);
                    image.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void countNumberOfLikes(String postId, TextView totalLikes) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalLikes.setText(snapshot.getChildrenCount() + " Likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countNumberOfComments(String postId, TextView totalComments) {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalComments.setText("View all " + snapshot.getChildrenCount() + " comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
