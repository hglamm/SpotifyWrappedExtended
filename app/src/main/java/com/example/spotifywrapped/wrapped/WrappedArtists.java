package com.example.spotifywrapped.wrapped;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WrappedArtists extends AppCompatActivity {

    private String mAccessToken;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;
    private TextView artistTextView;
    private Call mCall;
    private String[] topArtistsFinal;
    private MediaPlayer mediaPlayer;
    private String[] previewURLS;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped_artists);

        Bundle bundle = getIntent().getExtras();
        mAccessToken = bundle.getString("token");
        previewURLS = bundle.getStringArray("previewURLs");

        artistTextView = (TextView) findViewById(R.id.wrapped_artists_text);

        if (mAccessToken == null) {
            final AuthorizationRequest request = APIInteraction.getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
            AuthorizationClient.openLoginActivity(WrappedArtists.this, 3, request);
        }

        showWrappedArtists();

        CountDownTimer timer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                return;
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(WrappedArtists.this, WrappedAcousticness.class);
                bundle.putString("token", mAccessToken);
                bundle.putStringArray("topArtists", topArtistsFinal);
                intent.putExtras(bundle);
                mediaPlayer.stop();
                startActivity(intent);
            }
        };
        timer.start();

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(previewURLS[1]);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // Start playback
                    mediaPlayer.start();
                }
            });
        } catch (Exception e) {
            Toast.makeText(WrappedArtists.this, "Failed to load media", Toast.LENGTH_SHORT).show();
        }

    }

    private void showWrappedArtists() {
        final Request request1 = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request1);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(WrappedArtists.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray topArtists = jsonObject.getJSONArray("items");

                    int topArtistsLength = Math.min(topArtists.length(), 5);
                    String names = "";

                    topArtistsFinal = new String[topArtistsLength];

                    for (int i = 0; i < topArtistsLength; i++) {
                        String name = ((JSONObject) (topArtists.get(i))).getString("name");
                        names = names.concat((i+1) + ". " + name + "\n\n");
                        topArtistsFinal[i] = name;
                    }

                    names = "TOP ARTISTS: \n\n\n" + names;

                    setTextAsync(names, artistTextView);

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(WrappedArtists.this, "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
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
