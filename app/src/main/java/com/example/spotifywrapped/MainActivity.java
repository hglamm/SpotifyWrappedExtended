package com.example.spotifywrapped;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static FirebaseAuth fireAu = SignOnActivity.getAuth();
    private FirebaseDatabase datBa;
    private DatabaseReference dRef;
    private DataSnapshot dSnap;
    private FirebaseUser user;

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    public String mAccessToken, mAccessCode;
    private Call mCall;
    private String accEmail;//.getStringExtra("val");
    private Button loginBtn;

    private TextView welcomeTextView, codeTextView, profileTextView;
    private String topSongs;
    private String topArtists;
    private double avgEnergy;
    private double avgDanceability;
    private double avgInstrumentalness;
    private double avgLoudness;
    private double avgMode;
    private String id;
    private static String wrapName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle1 = getIntent().getExtras();
        Fragment fraggy = new WrappedNameFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, fraggy)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        //findViewById(R.id.fragmentContainerView).setVisibility(View.VISIBLE);

        //Button conf = (Button) findViewById(R.id.confyButt);

        if (bundle1 != null) {
            mAccessToken = bundle1.getString("token");
            datBa = FirebaseDatabase.getInstance();
            dRef = datBa.getReference();
            user = fireAu.getCurrentUser();
            id = user.getUid();
             Task<DataSnapshot> t = dRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    dSnap = task.getResult();
                    if (wrapName != null) {
                        addDataToBase(wrapName);
                        wrapName = null;
                    }

                }
            });

            dRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dSnap = snapshot;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            String[] topSongsArr = bundle1.getStringArray("topSongs");
            topSongs = "";
            String[] topArtistsArr = bundle1.getStringArray("topArtists");
            topArtists = "";
            for (int i = 0; i < topArtistsArr.length; i++) {
                topSongs = String.format("%s%d. %s%n", topSongs, (i + 1), topSongsArr[i]);
            }
            for(int i = 0; i < topArtistsArr.length; i++) {
                topArtists = String.format("%s%d. %s%n", topArtists, (i + 1), topArtistsArr[i]);
            }

            avgEnergy = secondDeci(bundle1.getDouble("avgEnergy"));
            avgDanceability = secondDeci(bundle1.getDouble("avgDanceability"));
            avgInstrumentalness = secondDeci(bundle1.getDouble("avgInstrumentalness"));
            avgLoudness = secondDeci(bundle1.getDouble("avgLoudness"));
            avgMode = secondDeci(bundle1.getDouble("avgMode"));




            // TODO - save wrapped info in bundle to database here
        }


        accEmail = getIntent().getStringExtra("val");
        // Initialize the views
        welcomeTextView = (TextView) findViewById(R.id.welcome_text_view);
        codeTextView = (TextView) findViewById(R.id.code_text_view);
        profileTextView = (TextView) findViewById(R.id.response_text_view);


        // Initialize the buttons
        Button tokenBtn = (Button) findViewById(R.id.token_btn);
        Button codeBtn = (Button) findViewById(R.id.code_btn);
        Button profileBtn = (Button) findViewById(R.id.profile_btn);
        loginBtn = (Button) findViewById(R.id.login_btn);
        Button settingBtn = (Button) findViewById(R.id.accSettings);
        Button wrappedBtn = (Button) findViewById(R.id.wrapped_btn);
        Button pastWrapped = (Button) findViewById(R.id.PastSummaries);

        if (mAccessToken != null) {
            loginBtn.setVisibility(View.INVISIBLE);
            welcomeTextView.setVisibility(View.VISIBLE);
        }

        // Set the click listeners for the buttons

        wrappedBtn.setOnClickListener((v) -> {
                if (mAccessToken == null) {
                    Toast.makeText(MainActivity.this, "You must log into Spotify first.", Toast.LENGTH_SHORT).show();
                    return;
                }
                findViewById(R.id.fragmentContainerView).setVisibility(View.VISIBLE);
                Button conf = (Button) findViewById(R.id.confyButt);
                conf.setOnClickListener((vv) -> {
                        wrapName = ((EditText)(findViewById(R.id.confWrappedTxt))).getText().toString();
                        if (wrapName == null || wrapName.length() == 0) {
                            Toast.makeText(MainActivity.this, "You must enter a name.", Toast.LENGTH_SHORT).show();

                        } else {
                            findViewById(R.id.fragmentContainerView).setVisibility(View.GONE);
                            Intent intent = new Intent(MainActivity.this, WrappedSongs.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("token", mAccessToken);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                });


        });

        loginBtn.setOnClickListener((v) -> {
            getToken();
        });

        tokenBtn.setOnClickListener((v) -> {
            getToken();
        });

        codeBtn.setOnClickListener((v) -> {
            getCode();
        });

        profileBtn.setOnClickListener((v) -> {
            onGetUserProfileClicked();
        });

        settingBtn.setOnClickListener((v) -> {
            Intent thing = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(thing);
        });

        pastWrapped.setOnClickListener((v) -> {
            Intent thing = new Intent(MainActivity.this, ViewPastSummaries.class);
            startActivity(thing);
        });


        /*pastSumBtn.setOnClickListener((v) -> {
                    Intent i = new Intent(MainActivity.this, PastSummariesActivity.class);
                    if (mAccessToken != null) {
                        i.putExtra("token", mAccessToken);
                        startActivity(i);
                    } else {
                        Toast.makeText(MainActivity.this, "You should have a token", Toast.LENGTH_SHORT).show();
                    }
        });*/


    }


    /**
     * Get token from Spotify
     * This method will open the Spotify login activity and get the token
     * What is token?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getToken() {
        final AuthorizationRequest request = APIInteraction.getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(MainActivity.this, AUTH_TOKEN_REQUEST_CODE, request);

    }

    /**
     * Get code from Spotify
     * This method will open the Spotify login activity and get the code
     * What is code?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getCode() {
        final AuthorizationRequest request = APIInteraction.getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(MainActivity.this, AUTH_CODE_REQUEST_CODE, request);
    }

    /**
     * Get user profile
     * This method will get the user profile using the token
     */
    public void onGetUserProfileClicked() {
        if (mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    setTextAsync(jsonObject.toString(3), profileTextView);

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

    public void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        System.out.println("Request Code: " + requestCode);
        System.out.println("Result Code: " + resultCode);
        System.out.println("data: " + data);
        System.out.println("Response: " + response);

        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
            loginBtn.setVisibility(View.INVISIBLE);
            welcomeTextView.setVisibility(View.VISIBLE);

        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
        }
    }
    public void addDataToBase(String wrapName) {
        boolean cont = true;
        if (wrapName == null || wrapName.length() == 0) {
            Toast.makeText(MainActivity.this, "Your wrap name can't be null", Toast.LENGTH_SHORT).show();
        } else {
            for(DataSnapshot ds: dSnap.getChildren()) {
                String thingy = ds.getKey();
                if (thingy.equals(id)) {
                    DataSnapshot snappy = ds.child("pastWrap");
                    DatabaseReference daty = snappy.getRef();
                    Map<String, Object> thing = new HashMap<String, Object>();
                    thing.put(wrapName, null);
                    daty.updateChildren(thing);

                }
            }
            DataSnapshot d1 = dSnap.child(id);
            DataSnapshot d2 = d1.child("pastWrap");
            DataSnapshot d3 = d2.child(wrapName);
                    DatabaseReference daty = d3.getRef();
                    Map<String, Object> thing = new HashMap<>();
                    thing.put("topSongs", topSongs);
                    thing.put("topArtists", topArtists);
                    thing.put("avgEnergy", avgEnergy);
                    thing.put("avgDanceability", avgDanceability);
                    thing.put("avgInstrumentalness", avgInstrumentalness);
                    thing.put("avgLoudness", avgLoudness);
                    thing.put("avgMode", avgMode);
                    daty.updateChildren(thing).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this, "Your new summary has been added", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    public double secondDeci(double d) {
        return ((double)((int)(d * 100))) / 100;
    }

}
