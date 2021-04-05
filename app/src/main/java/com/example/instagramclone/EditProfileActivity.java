package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagramclone.Models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private MaterialEditText name;
    private MaterialEditText username;
    private MaterialEditText bio;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    private Uri imageUri;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ImageView back = findViewById(R.id.editProfileBack_id);
        ImageView update = findViewById(R.id.editProfileUpdate_id);
        profileImage = findViewById(R.id.editProfileImage_id);
        TextView changeProfileImage = findViewById(R.id.editProfileChangeImage_id);
        name = findViewById(R.id.editProfileName_id);
        username = findViewById(R.id.editProfileUsername_id);
        bio = findViewById(R.id.editProfileBio_id);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        storageReference = FirebaseStorage.getInstance().getReference().child("Uploads");

        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                assert user != null;
                name.setText(user.getName());
                username.setText(user.getUsername());
                bio.setText(user.getBio());

                if(user.getImage().equals("default"))
                {
                    profileImage.setImageResource(R.mipmap.ic_default_image);
                }
                else
                {
                    Picasso.get().load(user.getImage()).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(v -> finish());

        update.setOnClickListener(v -> {
            UpdateProfile();
            finish();
        });

        changeProfileImage.setOnClickListener(v -> CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this));

        profileImage.setOnClickListener(v -> CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this));
    }

    private void UpdateProfile() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("Name", Objects.requireNonNull(name.getText()).toString());
        map.put("Username", Objects.requireNonNull(username.getText()).toString());
        map.put("Bio", Objects.requireNonNull(bio.getText()).toString());

        databaseReference.child(firebaseUser.getUid()).updateChildren(map);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            assert result != null;
            imageUri = result.getUri();

            uploadImage();

        } else {
            Toast.makeText(EditProfileActivity.this,"Something went wrong, try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if(imageUri != null) {
            StorageReference fileRef = storageReference.child(System.currentTimeMillis()+".jpeg");

            StorageTask storageTask = fileRef.putFile(imageUri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        assert downloadUri != null;
                        String url = downloadUri.toString();

                        databaseReference.child(firebaseUser.getUid()).child("Image").setValue(url);
                        progressDialog.dismiss();
                    } else
                    {
                        Toast.makeText(EditProfileActivity.this,"Upload failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(EditProfileActivity.this,"Please select image", Toast.LENGTH_SHORT).show();
        }
    }
}