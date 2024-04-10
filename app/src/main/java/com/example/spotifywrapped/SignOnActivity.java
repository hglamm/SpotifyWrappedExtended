package com.example.spotifywrapped;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class SignOnActivity extends AppCompatActivity {

    private static String email;
    private EditText emailEditText, passwordEditText;
    private String strEm;
    private String pass;
    private static FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener listAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        FragmentActivity frag = new FragmentActivity();
        auth = FirebaseAuth.getInstance();
        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        Button createAcc = (Button) findViewById(R.id.create_account_butt);
        emailEditText = (EditText) findViewById(R.id.email_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        listAuth = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth fireAuth) {
                FirebaseUser user = fireAuth.getCurrentUser();
                //if (user) {}
            }
        };




        signInButton.setOnClickListener((v) -> {
            strEm = emailEditText.getText().toString();
            pass = passwordEditText.getText().toString();
            if (strEm == null || strEm.length() == 0 || pass == null || pass.length() == 0) {
                Toast.makeText(SignOnActivity.this, "You didn't fill out all of the fields", Toast.LENGTH_SHORT).show();
            } else {
                //if (strEm == null || pass == null) {
                email = strEm;
                auth.signInWithEmailAndPassword(strEm, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (auth.getCurrentUser() != null) {

                            Intent intent = new Intent(SignOnActivity.this, MainActivity.class);
                            //intent.putExtra("auth", (CharSequence);
                            Toast.makeText(SignOnActivity.this, "Signing in!!!", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        } else {
                            Toast.makeText(SignOnActivity.this, "This user doesn't exist", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

        });

        createAcc.setOnClickListener((v) -> {
            Intent inti = new Intent(SignOnActivity.this, CreateAccountActivity.class);

            startActivity(inti);
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(listAuth);
    }
    public void onStop() {
        super.onStop();
        auth.removeAuthStateListener(listAuth);
    }
    public static FirebaseAuth getAuth() {
        return auth;
    }
    public static String getEmail(){
        return email;
    }
    }
