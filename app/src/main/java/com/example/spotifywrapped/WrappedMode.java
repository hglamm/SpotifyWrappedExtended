package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;

public class WrappedMode extends AppCompatActivity {
    private TextView modeTextView;
    private double avgMode = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped_mode);

        Bundle bundle = getIntent().getExtras();
        avgMode = bundle.getDouble("avgMode");

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
                startActivity(intent);
            }
        };
        timer.start();
    }

    private void showWrappedMode() {
        DecimalFormat f = new DecimalFormat("##.00");
        setTextAsync("MOOD METER \n\n Average Mode: " + Math.round(avgMode*100) + " / 100", modeTextView);
    }

    public void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

}
