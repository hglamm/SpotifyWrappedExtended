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


public class WrappedEnergy extends AppCompatActivity {
    private TextView energyTextView;
    private double avgEnergy = 0.0;
    private MediaPlayer mediaPlayer;
    private String[] previewURLS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped_energy);

        Bundle bundle = getIntent().getExtras();
        avgEnergy = bundle.getDouble("avgEnergy");
        previewURLS = bundle.getStringArray("previewURLs");


        energyTextView = (TextView) findViewById(R.id.wrapped_energy_text);

        showWrappedEnergy();
        CountDownTimer timer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                return;
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(WrappedEnergy.this, WrappedLoudness.class);
                intent.putExtras(bundle);
                mediaPlayer.stop();
                startActivity(intent);
            }
        };
        timer.start();
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(previewURLS[3]);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // Start playback
                    mediaPlayer.start();
                }
            });
        } catch (Exception e) {
            Toast.makeText(WrappedEnergy.this, "Failed to load media", Toast.LENGTH_SHORT).show();
        }
    }

    private void showWrappedEnergy() {
        DecimalFormat f = new DecimalFormat("##.00");
        setTextAsync("ENERGY BOOSTER: \n\nAverage Energy: " + Math.round(avgEnergy*100) + " / 100", energyTextView);
    }
    public void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }
}
