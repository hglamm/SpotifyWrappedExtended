package com.example.spotifywrapped;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseDatabase datBa;
    private DatabaseReference dRef;
    private DataSnapshot dSnap;
    String emSave = "";
    String passSave = "";
    String nameSave = "";
    private boolean dupli = false;
    private FirebaseAuth.AuthStateListener listAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_acc);


        auth = FirebaseAuth.getInstance();
        datBa = FirebaseDatabase.getInstance();
        dRef = datBa.getReference();
        listAuth = (FirebaseAuth.AuthStateListener) (firebaseAuth) -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                String id = user.getUid();
                Map<String, Object> thing = new HashMap<String, Object>();
                thing.put(id, null);
                dRef.updateChildren(thing);
                        DatabaseReference d = dRef.child(id);
                thing = new HashMap<String, Object>();
                thing.put("email: ", emSave);
                thing.put("name: ", nameSave);
                d.updateChildren(thing);
                DatabaseReference emails = dRef.child("allEmails");
                thing = new HashMap<String, Object>();
                thing.put(id, emSave);
                emails.updateChildren(thing);
            } else {
                Log.e("CreateAccountActivity", "The User was null");
            }

        };
        Button finishCrea = (Button) findViewById(R.id.finish);
        Button back = (Button) findViewById(R.id.back_main);
        dRef.addValueEventListener(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                                           dSnap = snapshot;
                                           //dupli = notDuplicate(snapshot);
                                       }

                                       @Override
                                       public void onCancelled(@NonNull DatabaseError error) {
                                       }
        });

        finishCrea.setOnClickListener((v) -> {

                    boolean s = saveStuff();
                    dupli = notDuplicate(dSnap);
                    if (s && !dupli) {
                        auth.createUserWithEmailAndPassword(emSave, passSave)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                //FirebaseUser usy = auth.getCurrentUser();
                                                auth.signOut();
                                                Toast.makeText(CreateAccountActivity.this, "Account Successfully created", Toast.LENGTH_SHORT).show();
                                                Intent inti = new Intent(CreateAccountActivity.this, SignOnActivity.class);
                                                startActivity(inti);
                                            }
                                        });


                    } else if (dupli) {
                        Toast.makeText(CreateAccountActivity.this, "This email is in use", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CreateAccountActivity.this, "Failed to create Account", Toast.LENGTH_SHORT).show();
                    }

                });
        back.setOnClickListener((v) -> {
            Intent inti = new Intent(CreateAccountActivity.this, SignOnActivity.class);
            startActivity(inti);
        });

    }

    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(listAuth);
    }
    public void onStop() {
        super.onStop();
        auth.removeAuthStateListener(listAuth);
    }
    public boolean saveStuff() {
        boolean bool = true;
        if (((EditText)findViewById(R.id.NameCont)).getText() != null) {
            nameSave = ((EditText)findViewById(R.id.NameCont)).getText().toString();
        } else {
            Toast.makeText(CreateAccountActivity.this, "You didn't enter a name.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (((EditText)findViewById(R.id.EmailCont)).getText() != null) {
            emSave = ((EditText)findViewById(R.id.EmailCont)).getText().toString();
        } else {
            Toast.makeText(CreateAccountActivity.this, "You didn't enter an email.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (((EditText)findViewById(R.id.PassCont)).getText() != null) {
            passSave = ((EditText)findViewById(R.id.PassCont)).getText().toString();
        } else {
            Toast.makeText(CreateAccountActivity.this, "You didn't enter a password.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!passSave.equals(((EditText)findViewById(R.id.PassConf)).getText().toString())) {
            Toast.makeText(CreateAccountActivity.this, "Your passwords don't match.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }



    }
    public boolean notDuplicate(DataSnapshot datSnap) {
        for (DataSnapshot ds: datSnap.getChildren()) {
            if (ds.getKey().equals("allEmails")) {
                for (DataSnapshot d: ds.getChildren()) {
                    if (d.getValue().equals(emSave)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    @Override
    protected void onDestroy() {
        //cancelCall();
        super.onDestroy();
    }

}
