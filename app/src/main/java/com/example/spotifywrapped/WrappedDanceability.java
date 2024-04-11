package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.text.DecimalFormat;

import okhttp3.Call;
import okhttp3.OkHttpClient;

public class WrappedDanceability extends AppCompatActivity {
    private String mAccessToken;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private TextView danceTextView;
    private Call mCall;
    private double avgAcousticness = 0.0;
    private double avgDanceability = 0.0;
    private double avgEnergy = 0.0;
    double avgInstrumentalness = 0.0;
    private double avgLiveness = 0.0;
    double avgLoudness = 0.0;
    private double avgMode = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped_danceability);

        Bundle bundle = getIntent().getExtras();
        mAccessToken = bundle.getString("token");
        avgDanceability = bundle.getDouble("avgDanceability");


        danceTextView = (TextView) findViewById(R.id.wrapped_danceability_text);

        if (mAccessToken == null) {
            final AuthorizationRequest request = APIInteraction.getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
            AuthorizationClient.openLoginActivity(WrappedDanceability.this, 3, request);
        }

        showWrappedDance();
        CountDownTimer timer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                return;
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(WrappedDanceability.this, WrappedInstrumentalness.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };
        timer.start();
    }

    private void showWrappedDance() {
        DecimalFormat f = new DecimalFormat("##.00");
        setTextAsync("DANCE FLOOR DELIGHTS \n\n Average Danceability: " + f.format(avgDanceability), danceTextView);
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
