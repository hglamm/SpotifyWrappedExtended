package com.example.spotifywrapped;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WrappedSummary extends AppCompatActivity {

    private TextView summarySongsTextView, summaryArtistsTextView;
    private String[] topSongs, topArtists;
    private MediaPlayer mediaPlayer;
    private String[] previewURLS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped_summary);

        Bundle bundle = getIntent().getExtras();

        topSongs = bundle.getStringArray("topSongs");
        topArtists = bundle.getStringArray("topArtists");
        previewURLS = bundle.getStringArray("previewURLs");

        summaryArtistsTextView = (TextView) findViewById(R.id.wrapped_summary_artists);
        summarySongsTextView = (TextView) findViewById(R.id.wrapped_summary_songs);

        showWrappedMode();
        CountDownTimer timer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                return;
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(WrappedSummary.this, MainActivity.class);
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
            Toast.makeText(WrappedSummary.this, "Failed to load media", Toast.LENGTH_SHORT).show();
        }
    }

    private void showWrappedMode() {
        String songs = "";
        for (int i = 0; i < topSongs.length; i++) {
            songs = songs.concat((i+1) + ". " + topSongs[i] + "\n");
        }
        String artists = "";
        for (int i = 0; i < topArtists.length; i++) {
            artists = artists.concat((i+1) + ". " + topArtists[i] + "\n");
        }

        setTextAsync("SPOTIFY WRAPPED: \n\nTOP SONGS: \n\n" + songs, summarySongsTextView);
        setTextAsync("\nTOP ARTISTS: \n\n" + artists, summaryArtistsTextView);
    }

    public void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

}
