package com.example.spotifywrapped.wrapped;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifywrapped.R;

import java.text.DecimalFormat;

public class WrappedLoudness extends AppCompatActivity {

    private TextView loudnessTextView;
    double avgLoudness = 0.0;
    private MediaPlayer mediaPlayer;
    private String[] previewURLS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped_loudness);

        Bundle bundle = getIntent().getExtras();
        avgLoudness = bundle.getDouble("avgLoudness");
        previewURLS = bundle.getStringArray("previewURLs");

        loudnessTextView = (TextView) findViewById(R.id.wrapped_loudness_text);

        showWrappedLoudness();
        CountDownTimer timer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                return;
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(WrappedLoudness.this, WrappedDanceability.class);
                intent.putExtras(bundle);
                mediaPlayer.stop();
                startActivity(intent);
            }
        };
        timer.start();
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(previewURLS[4]);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // Start playback
                    mediaPlayer.start();
                }
            });
        } catch (Exception e) {
            Toast.makeText(WrappedLoudness.this, "Failed to load media", Toast.LENGTH_SHORT).show();
        }
    }

    private void showWrappedLoudness() {
        DecimalFormat f = new DecimalFormat("##");
        double opp = Math.abs(avgLoudness) / 60.0;
        double format = 1.0 - opp;

        setTextAsync("VOLUME UP! \n\n Average Loudness: " + f.format(format*100) + " / 100", loudnessTextView);
    }

    public void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

}
