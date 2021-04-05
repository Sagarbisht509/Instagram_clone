package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UserDetailsActivity extends AppCompatActivity {

    private TextInputLayout textInputLayoutFullName;
    private TextInputLayout textInputLayoutUserName;
    private TextInputLayout textInputLayoutRePassword;

    private  FirebaseAuth firebaseAuth;
    //FirebaseDatabase root;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        Intent intent = getIntent();
        String email = intent.getStringExtra("Email");
        String password = intent.getStringExtra("Password");

        textInputLayoutFullName = findViewById(R.id.fullName_id);
        textInputLayoutUserName = findViewById(R.id.Username_id);
        textInputLayoutRePassword = findViewById(R.id.rePassword_id);
        Button next = findViewById(R.id.buttonContinue);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        next.setOnClickListener(v -> {
            String fullName = textInputLayoutFullName.getEditText().getText().toString().trim();
            String userName = textInputLayoutUserName.getEditText().getText().toString().trim();
            String rePassword = textInputLayoutRePassword.getEditText().getText().toString().trim();

            if (validate(fullName, userName, rePassword, password) == 3) {

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("Name", fullName);
                    map.put("Username", userName);
                    map.put("Email", email);
                    map.put("Bio","");
                    map.put("Image","default");
                    map.put("Id", firebaseAuth.getCurrentUser().getUid());

                    databaseReference.child("Users").child(firebaseAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(UserDetailsActivity.this, "You have successfully registered", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(UserDetailsActivity.this,LoginActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(e -> Toast.makeText(UserDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private int validate(String fullName, String userName, String rePassword, String password) {
        int ans = 0;

        if (validateUsername(userName)) ans++;

        if (validateFullName(fullName)) ans++;

        if (!rePassword.equals(password)) {
            textInputLayoutRePassword.setError("Password does not match");
        } else {
            textInputLayoutRePassword.setError(null);
            ans++;
        }

        return ans;
    }

    private boolean validateFullName(String fullName) {
        if (fullName.isEmpty()) {
            textInputLayoutFullName.setError("Field cannot be empty");
            return false;
        } else {
            textInputLayoutFullName.setError(null);
            return true;
        }
    }

    private boolean validateUsername(String userName) {
        if (userName.isEmpty()) {
            textInputLayoutUserName.setError("Field cannot be empty");
            return false;
        } else {
            textInputLayoutUserName.setError(null);
            return true;
        }
    }
}