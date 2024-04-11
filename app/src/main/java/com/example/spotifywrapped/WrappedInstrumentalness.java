package com.example.spotifywrapped;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class WrappedInstrumentalness extends AppCompatActivity {
    private TextView instrumentalnessTextView;
    double avgInstrumentalness = 0.0;
    private MediaPlayer mediaPlayer;
    private String[] previewURLS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped_instrumentalness);

        Bundle bundle = getIntent().getExtras();
        avgInstrumentalness = bundle.getDouble("avgInstrumentalness");
        previewURLS = bundle.getStringArray("previewURLs");

        instrumentalnessTextView = (TextView) findViewById(R.id.wrapped_instrument_text);

        showWrappedInstrument();
        CountDownTimer timer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                return;
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(WrappedInstrumentalness.this, WrappedMode.class);
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
            Toast.makeText(WrappedInstrumentalness.this, "Failed to load media", Toast.LENGTH_SHORT).show();
        }
    }

    private void showWrappedInstrument() {
        DecimalFormat f = new DecimalFormat("##.00");
        setTextAsync("MUSICAL MASTERY \n\nAverage Instrumentalness: " + Math.round(avgInstrumentalness * 100) + " / 100", instrumentalnessTextView);
    }

    public void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

}
