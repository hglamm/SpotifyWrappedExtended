package com.example.spotifywrapped;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifywrapped.APIInteraction;
import com.example.spotifywrapped.R;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Recommendations extends AppCompatActivity {

    private String mAccessToken;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;

    private TextView recommendationText;
    private Call mCall;
    private String[] topSongsIDs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendations_layout);

        Bundle bundle = getIntent().getExtras();
        mAccessToken = bundle.getString("token");

        recommendationText = (TextView) findViewById(R.id.recommendation_text);
        Button recommendationBtn = findViewById(R.id.get_recommendations_btn);
        Button backBtn = findViewById(R.id.recommendation_back_btn);

        if (mAccessToken == null) {
            final AuthorizationRequest request = APIInteraction.getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
            AuthorizationClient.openLoginActivity(Recommendations.this, 3, request);
        }

        recommendationBtn.setOnClickListener((v) -> {
            showWrappedSongs();
        });

        backBtn.setOnClickListener((v) -> {
            Intent intent = new Intent(Recommendations.this, MainActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }

    private void showWrappedSongs() {
        final Request request1 = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?limit=5")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request1);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(Recommendations.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray topSongs = jsonObject.getJSONArray("items");

                    int topSongsLength = Math.min(topSongs.length(), 5);

                    topSongsIDs = new String[topSongsLength];

                    for (int i = 0; i < topSongsLength; i++) {
                        String id = ((JSONObject) (topSongs.get(i))).getString("id");
                        topSongsIDs[i] = id;
                    }
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(Recommendations.this, "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        while (topSongsIDs == null) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                continue;
            }
        }

        String concatIDs = "";
        for (int i = 0; i < topSongsIDs.length; i++) {
            if (i == 0) {
                concatIDs = topSongsIDs[0];
            } else {
                concatIDs = concatIDs.concat("," + topSongsIDs[i]);
            }
        }

        final Request request2 = new Request.Builder()
                .url("https://api.spotify.com/v1/recommendations?seed_tracks=" + concatIDs)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        mCall = mOkHttpClient.newCall(request2);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(Recommendations.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray recommendedSongs = jsonObject.getJSONArray("tracks");

                    int recommendedSongsLength = Math.min(recommendedSongs.length(), 10);

                    String[] recommendedSongsNames = new String[recommendedSongsLength];
                    String[] recommendedSongsArtists = new String[recommendedSongsLength];

                    for (int i = 0; i < recommendedSongsLength; i++) {
                        String name = ((JSONObject) (recommendedSongs.get(i))).getString("name");
                        recommendedSongsNames[i] = name;

                        String artist = ((JSONObject) (((JSONObject) (recommendedSongs.get(i))).getJSONArray("artists").get(0))).getString("name");
                        recommendedSongsArtists[i] = artist;
                    }

                    String displayText = "Recommended Songs Based on Your Top 5 Songs: \n\n";

                    for (int i = 0; i < recommendedSongsLength; i++) {
                        String pair = recommendedSongsNames[i] + " by " + recommendedSongsArtists[i];
                        displayText = displayText.concat((i+1) + ". " + pair + "\n");
                    }

                    setTextAsync(displayText, recommendationText);

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
//                     Toast.makeText(Recommendations.this, "Failed to parse data, watch Logcat for more details",
//                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
        }
    }

    public void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    public void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

}