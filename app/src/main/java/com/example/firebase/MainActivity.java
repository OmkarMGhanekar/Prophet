package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail, editTextPass;
    private Button signIn;
    private TextView register, forgot;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    private Intent Userpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        register = findViewById(R.id.register_text);
        register.setOnClickListener(this);

        signIn = findViewById(R.id.signIn);
        signIn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.email);
        editTextPass = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressbar);
        forgot = findViewById(R.id.forgotPassword);
        forgot.setOnClickListener(this);
        Userpage = new Intent(MainActivity.this, ProfileActivity.class);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_text:
                startActivity(new Intent(this, RegisterUser.class));
                break;
            case R.id.signIn:
                userLogin();
                break;
            case R.id.forgotPassword:
                startActivity(new Intent(this, ForgotPassword.class));
        }
    }

    private void userLogin() {
        final String email = editTextEmail.getText().toString().trim();
        final String pass = editTextPass.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }
        if (pass.isEmpty()) {
            editTextPass.setError("Password is required");
            editTextPass.requestFocus();
            return;
        }
        if (pass.length() < 6) {
            editTextPass.setError("min password length is 6 character");
            editTextPass.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user.isEmailVerified()) {
//                        redirect to user profile
                        progressBar.setVisibility(View.INVISIBLE);
                        signIn.setVisibility(View.VISIBLE);
                        updateUI();

                    } else {
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Check you email to verify your account!", Toast.LENGTH_LONG).show();

                    }

                } else {
                    Toast.makeText(MainActivity.this, "Failed to login! please check your credentials", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void updateUI() {
        startActivity(Userpage);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            updateUI();
        }
    }
}