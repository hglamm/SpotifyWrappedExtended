package com.example.spotifywrapped;

import android.database.DataSetObserver;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.spotifywrapped.databinding.ActivityViewPastSummariesBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewPastSummaries extends AppCompatActivity {
    private FirebaseAuth auth = SignOnActivity.getAuth();
    //private ActivityViewPastSummariesBinding binding;
    private FirebaseDatabase datBa;
    private DatabaseReference dRef;
    private FirebaseUser user;
    private DataSnapshot dSnap;
    private boolean adapterMade;
    private Spinner spin;
    private ArrayAdapter<String> spinA;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_past_summaries);

        spin = (Spinner) (findViewById(R.id.sumSpinner));
        adapterMade = false;
        datBa = FirebaseDatabase.getInstance();
        dRef = datBa.getReference();
        user = auth.getCurrentUser();
        userId = user.getUid();
        Button confirm = (Button) findViewById(R.id.sumConf);

        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dSnap = snapshot;
                if (!adapterMade) {
                    makeAdapter();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        confirm.setOnClickListener((v) -> {
            String thing = getWrappedStats();
            ((TextView) findViewById(R.id.sumBox)).setText(thing);
        });




    }
    public String getWrappedStats() {
        String val = (String) spin.getSelectedItem();
        String ret = "";
        DataSnapshot d1 = dSnap.child(userId).child("pastWrap").child(val);
        for (DataSnapshot ds : d1.getChildren()) {
            ret = String.format("%s%n%s%n%s", ret, ds.getKey(), ds.getValue());
        }
        return ret;
    }
    public void makeAdapter() {
        DataSnapshot d1 = dSnap.child(userId);
        DataSnapshot d2 = d1.child("pastWrap");
        ArrayList<String> arr = new ArrayList<String>();
        int i = 0;
        for (DataSnapshot dS: d2.getChildren()) {
            arr.add(dS.getKey());
            i++;
        }
        spinA = new ArrayAdapter<String>(ViewPastSummaries.this, android.R.layout.simple_spinner_item, arr);
        spinA.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);

        spin.setAdapter(spinA);
        adapterMade = true;

    }

}