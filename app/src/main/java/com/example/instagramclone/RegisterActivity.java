package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        textInputLayoutEmail = findViewById(R.id.Email_id);
        textInputLayoutPassword = findViewById(R.id.Password_id);
        Button next = findViewById(R.id.buttonNextRegister);

        firebaseAuth = FirebaseAuth.getInstance();

        next.setOnClickListener(v -> {
            String email = textInputLayoutEmail.getEditText().getText().toString().trim();
            String password = textInputLayoutPassword.getEditText().getText().toString().trim();
            if (validate(email, password) == 2) {

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        firebaseAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(aVoid -> {
                            Toast.makeText(RegisterActivity.this, "Verification email has been sent to your email please verify before login.", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(RegisterActivity.this, UserDetailsActivity.class);
                            String userEmail = textInputLayoutEmail.getEditText().getText().toString().trim();
                            String pass = textInputLayoutPassword.getEditText().getText().toString().trim();
                            intent.putExtra("Email", userEmail);
                            intent.putExtra("Password", pass);
                            startActivity(intent);
                            finish();
                        }).addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(RegisterActivity.this, "Failed while registering email", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private boolean validateEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email.isEmpty()) {
            textInputLayoutEmail.setError("Field cannot be empty");
            return false;
        } else {
            if (!email.matches(emailPattern)) {
                textInputLayoutEmail.setError("Invalid email address");
                return false;
            } else {
                textInputLayoutEmail.setError(null);
                textInputLayoutEmail.setErrorEnabled(false);
                return true;
            }
        }
    }

    private boolean validatePassword(String password) {
        if (password.length() < 6) {
            textInputLayoutPassword.setError("Password length mush be at-least 6");
            return false;
        } else {
            textInputLayoutPassword.setError(null);
            return true;
        }
    }

    private int validate(String email, String password) {
        int ans = 0;
        if (validateEmail(email)) ans++;
        if (validatePassword(password)) ans++;

        return ans;
    }
}