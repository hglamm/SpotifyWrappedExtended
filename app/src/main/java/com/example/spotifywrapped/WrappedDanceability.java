package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class WrappedDanceability extends AppCompatActivity {
    private TextView danceTextView;
    private double avgDanceability = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped_danceability);

        Bundle bundle = getIntent().getExtras();
        avgDanceability = bundle.getDouble("avgDanceability");

        danceTextView = (TextView) findViewById(R.id.wrapped_danceability_text);

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
        setTextAsync("DANCE FLOOR DELIGHTS \n\n Average Danceability: \n" + Math.round(avgDanceability*100) + " / 100", danceTextView);
    }

    public void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

}
