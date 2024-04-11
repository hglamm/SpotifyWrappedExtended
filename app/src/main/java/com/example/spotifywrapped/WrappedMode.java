package com.example.spotifywrapped;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;

public class WrappedMode extends AppCompatActivity {
    private TextView modeTextView;
    private double avgMode = 0.0;
    private MediaPlayer mediaPlayer;
    private String[] previewURLS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped_mode);

        Bundle bundle = getIntent().getExtras();
        avgMode = bundle.getDouble("avgMode");
        previewURLS = bundle.getStringArray("previewURLs");

        modeTextView = (TextView) findViewById(R.id.wrapped_mode_text);

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
                mediaPlayer.stop();
                startActivity(intent);
            }
        };
        timer.start();
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(previewURLS[2]);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // Start playback
                    mediaPlayer.start();
                }
            });
        } catch (Exception e) {
            Toast.makeText(WrappedMode.this, "Failed to load media", Toast.LENGTH_SHORT).show();
        }
    }

    private void showWrappedMode() {
        DecimalFormat f = new DecimalFormat("##.00");
        setTextAsync("MOOD METER \n\n Average Mode: " + Math.round(avgMode*100) + " / 100", modeTextView);
    }

    public void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

}
