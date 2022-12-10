package com.example.rss3.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rss3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * LoginActivity is the login page for the application. It uses Firebase authentication.
 *
 *
 * CodeWithMazn for Login and Registration on firebase:
 * https://www.youtube.com/watch?v=Z-RE1QuUWPg , https://www.youtube.com/watch?v=KB2BIm_m1Os&t=218s
 */
public class LoginActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    Button register;
    Button login;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        register = findViewById(R.id.button2);
        login = findViewById(R.id.button3);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     *
     * @param v gets the id of the button clicked to either log in or register an user.
     */
    @SuppressLint("NonConstantResourceId")
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * R.id.button2 = Register user
             */
            case R.id.button2:
                String[] user = null;
                if ((user = checkCredentials()) == null) {
                    progressBar.setVisibility(View.GONE);
                    return;
                } else {
                    /**
                     * Registering user with Firebase, on complete listener sends a toast announcing the outcome
                     */
                    mAuth.createUserWithEmailAndPassword(user[0], user[1])
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        Log.d("MESAAGE", "User has been registered successfully");
                                        Toast.makeText(LoginActivity.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
                break;
            /**
             * R.id.button3 = Register user.
             */
            case R.id.button3:
                checkCredentials();
                userLogin();
                break;
        }
    }

    /**
     * Login method, on success an intent sends the user to the SearchRss activity
     */
    private void userLogin() {
        String email = this.email.getText().toString().trim();
        String password = this.password.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, SearchRss.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    progressBar.setVisibility(View.GONE);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "login failed! Wrong credentials", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Check credentials method is an input authentication verifier that notifies users if there's an invalid input
     * @return String array with value at index 0: email, index 1 password
     */
    private String[] checkCredentials() {
        String email = this.email.getText().toString().trim();
        String password = this.password.getText().toString().trim();

        if (email.isEmpty()) {
            this.email.setError("Email is required");
            this.email.requestFocus();
            return null;
        }
        if (password.isEmpty()) {
            this.password.setError("Password is required");
            this.password.requestFocus();
            return null;
        }
        if (password.length() < 6) {
            this.password.setError("Min password length is 6 characters");
            this.password.requestFocus();
            return null;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.email.setError("Please provide valid email");
            this.email.requestFocus();
            return null;
        }
        progressBar.setVisibility(View.VISIBLE);
        return new String[]{email, password};
    }
}
