package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.Adapter.CommentAdapter;
import com.example.instagramclone.Models.Comment;
import com.example.instagramclone.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    private String postId;
    private String publisherId;

    FirebaseUser firebaseUser;

    private CircleImageView profileImage;
    private EditText addComment;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar = findViewById(R.id.toolBar_id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");  // post id
        publisherId = intent.getStringExtra("authorId"); // post author id

        RecyclerView recyclerView = findViewById(R.id.commentsRecyclerView_id);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList, postId);
        recyclerView.setAdapter(commentAdapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        profileImage = findViewById(R.id.userCommentImage_id);
        addComment = findViewById(R.id.userComment_id);
        TextView postComment = findViewById(R.id.postUserComment_id);

        setImageToProfileImage();

        postComment.setOnClickListener(v -> {
            if(TextUtils.isEmpty(addComment.getText().toString())) {
                Toast.makeText(CommentActivity.this,"No comment added",Toast.LENGTH_SHORT).show();
            } else {
              addNewComment();
            }
        });

        getComment();

    }

    private void getComment() {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    commentList.add(comment);
                }

                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addNewComment() {
        HashMap<String, Object> map = new HashMap<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);

        String commentId = databaseReference.push().getKey();
        map.put("commentId", commentId);
        map.put("comment",addComment.getText().toString());
        map.put("publisher",firebaseUser.getUid());

        addComment.setText("");

        databaseReference.child(commentId).setValue(map).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Toast.makeText(CommentActivity.this,"Comment added",Toast.LENGTH_SHORT).show();

                if(!firebaseUser.getUid().equals(publisherId))
                    addNotification(postId, publisherId);

            } else {
                Toast.makeText(CommentActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addNotification(String postId, String publisherId) {
        HashMap<String,Object> map = new HashMap<>();

        map.put("userId", firebaseUser.getUid());
        map.put("notificationText", " comment in your post.");
        map.put("postId", postId);
        map.put("fromPost", true);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(publisherId).push().setValue(map);
    }

    private void setImageToProfileImage() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                assert user != null;
                if(user.getImage().equals("default")) {
                    profileImage.setImageResource(R.mipmap.ic_default_image);
                } else {
                    Picasso.get().load(user.getImage()).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}