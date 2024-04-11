package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WrappedAcousticness extends AppCompatActivity {
    private String mAccessToken;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;

    private TextView analysisTextView;
    private Call mCall;
    private String ids;

    private double avgAcousticness = 0.0;
    private double avgDanceability = 0.0;
    private double avgEnergy = 0.0;
    double avgInstrumentalness = 0.0;
    double avgLoudness = 0.0;
    private double avgMode = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped_acousticness);

        Bundle bundle = getIntent().getExtras();
        mAccessToken = bundle.getString("token");

        analysisTextView = (TextView) findViewById(R.id.wrapped_analysis_text);

        if (mAccessToken == null) {
            final AuthorizationRequest request = APIInteraction.getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
            AuthorizationClient.openLoginActivity(WrappedAcousticness.this, 3, request);
        }

        showWrappedAnalysis();


        CountDownTimer timer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                return;
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(WrappedAcousticness.this, WrappedEnergy.class);
                bundle.putString("token", mAccessToken);
                bundle.putDouble("avgEnergy", avgEnergy);
                bundle.putDouble("avgDanceability", avgDanceability);
                bundle.putDouble("avgInstrumentalness", avgInstrumentalness);
                bundle.putDouble("avgLoudness", avgLoudness);
                bundle.putDouble("avgMode", avgMode);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };
        timer.start();
    }

    private void showWrappedAnalysis() {
        final Request request1 = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?limit=50")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request1);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(WrappedAcousticness.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray top50Songs = jsonObject.getJSONArray("items");
                    String ids1 = "";

                    for (int i = 0; i < top50Songs.length(); i++) {
                        String id = ((JSONObject) (top50Songs.get(i))).getString("id");
                        if (i == 0) {
                            ids1 = id;
                        } else {
                            ids1 = ids1.concat("," + id);
                        }
                    }
                    ids = ids1;
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(WrappedAcousticness.this, "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        while (ids == null) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                continue;
            }
        }
        final Request request2 = new Request.Builder()
                .url("https://api.spotify.com/v1/audio-features?ids=" + ids)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        mCall = mOkHttpClient.newCall(request2);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(WrappedAcousticness.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray topSongsAudioFeatures = jsonObject.getJSONArray("audio_features");

                    for (int i = 0; i < topSongsAudioFeatures.length(); i++) {
                        avgAcousticness += Double.parseDouble(((JSONObject) (topSongsAudioFeatures.get(i))).getString("acousticness"));
                        avgDanceability += Double.parseDouble(((JSONObject) (topSongsAudioFeatures.get(i))).getString("danceability"));
                        avgEnergy += Double.parseDouble(((JSONObject) (topSongsAudioFeatures.get(i))).getString("energy"));
                        avgInstrumentalness += Double.parseDouble(((JSONObject) (topSongsAudioFeatures.get(i))).getString("instrumentalness"));
                        avgLoudness += Double.parseDouble(((JSONObject) (topSongsAudioFeatures.get(i))).getString("loudness"));
                        avgMode += Double.parseDouble(((JSONObject) (topSongsAudioFeatures.get(i))).getString("mode"));
                    }

                    DecimalFormat f = new DecimalFormat("##.00");
                    avgAcousticness /= topSongsAudioFeatures.length();
                    avgDanceability /= topSongsAudioFeatures.length();
                    avgEnergy /= topSongsAudioFeatures.length();
                    avgInstrumentalness /= topSongsAudioFeatures.length();
                    avgLoudness /= topSongsAudioFeatures.length();
                    avgMode /= topSongsAudioFeatures.length();

                    String displayText = "Chill Vibes Meter: \n\n" + (Math.round(avgAcousticness*100) + " / 100");

                    setTextAsync(displayText, analysisTextView);

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(WrappedAcousticness.this, "Failed to parse data, watch Logcat for more details",
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
