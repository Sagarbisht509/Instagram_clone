package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.appcompat.socialview.Hashtag;
import com.hendraanggrian.appcompat.widget.HashtagArrayAdapter;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {

    private Uri imageUri;
    private String imageUrl;

    private ImageView postImage;
    SocialAutoCompleteTextView caption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ImageView close = findViewById(R.id.imageViewClose_id);
        ImageView next = findViewById(R.id.imageViewNext);
        postImage = findViewById(R.id.imageViewPostImage);
        caption = findViewById(R.id.imageCaption);

        close.setOnClickListener(v -> {
            startActivity(new Intent(PostActivity.this, HomeActivity.class));
            finish();
        });

        next.setOnClickListener(v -> post());

        //return image
        CropImage.activity().start(PostActivity.this);
    }

    private void post() {

        final ProgressDialog progressDialog = new ProgressDialog(PostActivity.this);
        progressDialog.setMessage("Uploading post");
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference("Posts").
                    child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            StorageTask storageTask = storageReference.putFile(imageUri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    assert downloadUri != null;
                    imageUrl = downloadUri.toString();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
                    String postId = databaseReference.push().getKey();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("postId", postId);
                    map.put("imageUrl", imageUrl);
                    map.put("caption", caption.getText().toString());
                    map.put("publisher", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

                    assert postId != null;
                    databaseReference.child(postId).setValue(map);

                    DatabaseReference hashTagRef = FirebaseDatabase.getInstance().getReference().child("HashTags");
                    List<String> hashTags = caption.getHashtags();

                    if (!hashTags.isEmpty()) {
                        for (String tag : hashTags) {
                            map.clear();

                            map.put("tag", tag.toLowerCase());
                            map.put("postId", postId);
                            hashTagRef.child(tag.toLowerCase()).child(postId).setValue(map);
                        }
                    }

                    progressDialog.dismiss();
                    startActivity(new Intent(PostActivity.this, HomeActivity.class));
                    finish();
                }
            }).addOnFailureListener(e -> Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(PostActivity.this, "PLease select image for post", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {

        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            assert result != null;
            imageUri = result.getUri();

            postImage.setImageURI(imageUri);
        } else {
            Toast.makeText(PostActivity.this, "Something went wrong, try again ", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, HomeActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        final ArrayAdapter<Hashtag> hashtagArrayAdapter = new HashtagArrayAdapter<>(getApplicationContext());

        FirebaseDatabase.getInstance().getReference().child("HashTags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    hashtagArrayAdapter.add(new Hashtag(Objects.requireNonNull(dataSnapshot.getKey()), (int) dataSnapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        caption.setHashtagAdapter(hashtagArrayAdapter);
    }
}