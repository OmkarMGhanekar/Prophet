package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.TagLostException;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {
    private Button register_user;
    private EditText editTextFullName, editTextEmail, editTextPass;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();
        register_user = (Button)findViewById(R.id.registerUser);
        register_user.setOnClickListener(this);


        editTextFullName = findViewById(R.id.fullName);
        editTextEmail = findViewById(R.id.email);
        editTextPass = findViewById(R.id.password);

        progressBar = findViewById(R.id.progressbar);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.registerUser:
                registerUser();
                break;
                

        }
    }

    private void registerUser() {
        final String fullName= editTextFullName.getText().toString().trim();
        final String email=editTextEmail.getText().toString().trim();
        final String pass= editTextPass.getText().toString().trim();

        if (fullName.isEmpty())
        {
            editTextFullName.setError("Full Name is Required");
            editTextFullName.requestFocus();
            return;
        }
        if (email.isEmpty())
        {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("please provide valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (pass.isEmpty())
        {
            editTextPass.setError("password is required");
            editTextPass.requestFocus();
            return;
        }
        if (pass.length()<6)
        {
            editTextPass.setError("min length should be 6 character");
            editTextPass.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            User user= new User(fullName,email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(getApplicationContext(),"User has been registered succesfully!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        Intent abc = new Intent(RegisterUser.this, MainActivity.class);
                                        startActivity(abc);

                                    }
                                    else
                                    {
                                        Toast.makeText(RegisterUser.this, "Failed to register!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(RegisterUser.this, "Failed! Already registered, Try to Login", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            Intent abc = new Intent(RegisterUser.this, MainActivity.class);
                            startActivity(abc);

                        }
                    }
                });

    }
}