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

public class WrappedMode extends AppCompatActivity {
    private String mAccessToken;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private TextView modeTextView;
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
        setContentView(R.layout.wrapped_mode);

        Bundle bundle = getIntent().getExtras();
        mAccessToken = bundle.getString("token");
        avgMode = bundle.getDouble("avgMode");


        modeTextView = (TextView) findViewById(R.id.wrapped_mode_text);

        if (mAccessToken == null) {
            final AuthorizationRequest request = APIInteraction.getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
            AuthorizationClient.openLoginActivity(WrappedMode.this, 3, request);
        }

        showWrappedMode();
        CountDownTimer timer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                return;
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(WrappedMode.this, WrappedSummary.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };
        timer.start();
    }

    private void showWrappedMode() {
        DecimalFormat f = new DecimalFormat("##.00");
        setTextAsync("MOOD METER \n\n Average Mode: " + f.format(avgMode), modeTextView);
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
