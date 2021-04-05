package com.example.instagramclone.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.Models.Comment;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private final Context context;
    private final List<Comment> commentList;

    private FirebaseUser firebaseUser;

    String postId;

    public CommentAdapter(Context context, List<Comment> commentList, String postId) {
        this.context = context;
        this.commentList = commentList;
        this.postId = postId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item,parent,false);

        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Comment comment = commentList.get(position);

        holder.commentInPost.setText(comment.getComment());

        FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                assert user != null;
                holder.usernameOfComment.setText(user.getUsername());
                if(user.getImage().equals("default")) {
                    holder.profileImage.setImageResource(R.mipmap.ic_default_image);
                } else {
                    Picasso.get().load(user.getImage()).into(holder.profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        holder.usernameOfComment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, HomeActivity.class);
//                intent.putExtra("publisherId", comment.getPublisher());
//                context.startActivity(intent);
//            }
//        });
//
//        holder.profileImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, HomeActivity.class);
//                intent.putExtra("publisherId",comment.getPublisher());
//                context.startActivity(intent);
//            }
//        });

        holder.itemView.setOnLongClickListener(v -> {
            if(comment.getPublisher().endsWith(firebaseUser.getUid())) {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Do you want to delete comment");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No", (dialog, which) -> dialog.dismiss());
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", (dialog, which) -> FirebaseDatabase.getInstance().getReference().child("Comments")
                        .child(postId).child(comment.getCommentId()).removeValue().addOnCompleteListener(task -> {
                            if(task.isSuccessful()) {
                                Toast.makeText(context,"Comment deleted successfully",Toast.LENGTH_SHORT).show();

                                dialog.dismiss();
                            }
                        }));
                alertDialog.show();
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView profileImage;
        public TextView usernameOfComment;
        public TextView commentInPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.commentUserProfileImage_id);
            usernameOfComment = itemView.findViewById(R.id.usernameOfComment_id);
            commentInPost = itemView.findViewById(R.id.commentInPost_id);
        }
    }
}
