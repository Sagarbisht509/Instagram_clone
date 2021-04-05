package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextView textViewSignUp;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textInputLayoutEmail = findViewById(R.id.LoginEmail_id);
        textInputLayoutPassword = findViewById(R.id.LoginPassword_id);
        Button login = findViewById(R.id.buttonLogin);
        textViewSignUp = findViewById(R.id.textViewSignUp);

        firebaseAuth = FirebaseAuth.getInstance();

        signUp();

        textViewSignUp.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        login.setOnClickListener(v -> {

            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Please wait");
            progressDialog.show();

            String email = Objects.requireNonNull(textInputLayoutEmail.getEditText()).getText().toString().trim();
            String password = Objects.requireNonNull(textInputLayoutPassword.getEditText()).getText().toString().trim();

            if (validate(email, password)) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()) {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "login successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "please verify email", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
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
        if(password.isEmpty()) {
            textInputLayoutPassword.setError("Field cannot be empty");
            return false;
        }
        else if(password.length() < 6) {
            textInputLayoutPassword.setError("Password length should be at-least 6");
            return false;
        } else {
            textInputLayoutPassword.setError(null);
            return true;
        }
    }

    private boolean validate(String email, String password) {

        int ans = 0;

        if (validateEmail(email)) ans++;

        if (validatePassword(password)) ans++;

        if (ans == 2) return true;
        else return false;
    }

    private void signUp() {
        SpannableString spannableString = new SpannableString("Don't have account? Sign up.");

        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(styleSpan, 20, 27, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        textViewSignUp.setText(spannableString);
    }
}