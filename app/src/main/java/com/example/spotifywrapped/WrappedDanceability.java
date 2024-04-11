package com.example.spotifywrapped;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class WrappedDanceability extends AppCompatActivity {
    private TextView danceTextView;
    private double avgDanceability = 0.0;
    private MediaPlayer mediaPlayer;
    private String[] previewURLs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped_danceability);

        Bundle bundle = getIntent().getExtras();
        avgDanceability = bundle.getDouble("avgDanceability");
        previewURLs = bundle.getStringArray("previewURLs");

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
                mediaPlayer.stop();
                startActivity(intent);
            }
        };
        timer.start();

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(previewURLs[0]);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // Start playback
                    mediaPlayer.start();
                }
            });
        } catch (Exception e) {
            Toast.makeText(WrappedDanceability.this, "Failed to load media", Toast.LENGTH_SHORT).show();
        }
    }

    private void showWrappedDance() {
        DecimalFormat f = new DecimalFormat("##.00");
        setTextAsync("DANCE FLOOR DELIGHTS \n\n Average Danceability: \n" + Math.round(avgDanceability*100) + " / 100", danceTextView);
    }

    public void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

}
