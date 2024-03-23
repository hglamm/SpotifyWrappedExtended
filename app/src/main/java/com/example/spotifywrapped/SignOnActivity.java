package com.example.spotifywrapped;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignOnActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    public static String email;
    public static String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        Button signInButton = (Button) findViewById(R.id.sign_in_button);

        emailEditText = (EditText) findViewById(R.id.email_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);

        signInButton.setOnClickListener((v) -> {
            saveUserInformation(emailEditText.getText().toString(), passwordEditText.getText().toString());
            Intent intent = new Intent(SignOnActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }



        public void saveUserInformation(String email, String password) {
            // save to database here if necessary
            this.email = email;
            this.password = password;
        }
    }
