package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;


public class WrappedEnergy extends AppCompatActivity {
    private TextView energyTextView;
    private double avgEnergy = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped_energy);

        Bundle bundle = getIntent().getExtras();
        avgEnergy = bundle.getDouble("avgEnergy");


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
                startActivity(intent);
            }
        };
        timer.start();
    }

    private void showWrappedEnergy() {
        DecimalFormat f = new DecimalFormat("##.00");
        setTextAsync("ENERGY BOOSTER: \n\nAverage Energy: " + Math.round(avgEnergy*100) + " / 100", energyTextView);
    }
    public void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }
}
