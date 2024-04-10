package com.example.spotifywrapped;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser u;
    FirebaseDatabase dBa;
    DatabaseReference ref;
    DataSnapshot dSnap;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        auth = SignOnActivity.getAuth();
        Button logOut = (Button) findViewById(R.id.logOutButt);
        Button dele = (Button) findViewById(R.id.delAcc);

        Bundle bundle = new Bundle();
        Fragment fraggy = new CertaintyFragment();


        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView2, fraggy).
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
        ;
        ((FragmentContainerView)(findViewById(R.id.fragmentContainerView2))).setVisibility(View.GONE);
        u = auth.getCurrentUser();
        String id = u.getUid();
        dBa = FirebaseDatabase.getInstance();
        ref = dBa.getReference();

        ref.addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dSnap = snapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }));




        logOut.setOnClickListener((v) -> {
            ((TextView)(findViewById(R.id.texty2))).setText((CharSequence) "Are You Sure You Want To Log Out of Your Account");
            ((FragmentContainerView)(findViewById(R.id.fragmentContainerView2))).setVisibility(View.VISIBLE);
            Button but = (Button) (findViewById(R.id.yesButt));
            Button noBut = (Button) (findViewById(R.id.noButt));
            but.setOnClickListener((va) -> {
                ((FragmentContainerView)(findViewById(R.id.fragmentContainerView2))).setVisibility(View.GONE);
                auth.signOut();
                Intent inti = new Intent(SettingsActivity.this, SignOnActivity.class);
                startActivity(inti);
            });
            noBut.setOnClickListener((va) -> {
                ((FragmentContainerView)(findViewById(R.id.fragmentContainerView2))).setVisibility(View.GONE);
            });

        });

        dele.setOnClickListener((v) -> {
            ((TextView)(findViewById(R.id.texty2))).setText((CharSequence) "Are You Sure You Want To Delete Your Account");
            ((FragmentContainerView)(findViewById(R.id.fragmentContainerView2))).setVisibility(View.VISIBLE);
            Button but = (Button) (findViewById(R.id.yesButt));
            Button noBut = (Button) (findViewById(R.id.noButt));
            but.setOnClickListener((va) -> {
                ((FragmentContainerView)(findViewById(R.id.fragmentContainerView2))).setVisibility(View.GONE);


                deleteSnapShotInfo(id);
                u.delete();
                Intent inti = new Intent(SettingsActivity.this, SignOnActivity.class);
                startActivity(inti);
            });
            noBut.setOnClickListener((va) -> {
                ((FragmentContainerView)(findViewById(R.id.fragmentContainerView2))).setVisibility(View.GONE);
            });

        });



    }
    public void deleteSnapShotInfo(String id) {
        for (DataSnapshot ds: dSnap.getChildren()) {
            if (ds.getKey().equals("allEmails")) {
                for (DataSnapshot d: ds.getChildren()) {
                    if (d.getValue().equals((SignOnActivity.getEmail()))) {
                        d.getRef().removeValue();
                    }
                }
            } else if (ds.getKey().equals(id)) {
                ds.getRef().removeValue();
            }
        }
    }
}
