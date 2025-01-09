package com.example.thirdeyecar;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import me.pushy.sdk.Pushy;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_PARK_MODE = "park_mode";
    private boolean isParkMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Pushy.isRegistered(this)) {
            new RegisterForPushNotificationsAsync(this).execute();
        }

        Pushy.listen(this);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        isParkMode = prefs.getBoolean(KEY_PARK_MODE, true);

        Button toggleButton = findViewById(R.id.toggleButton);
        updateToggleButton(toggleButton);
        toggleButton.setOnClickListener(v -> {
            isParkMode = !isParkMode;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_PARK_MODE, isParkMode);
            editor.apply();
            updateToggleButton(toggleButton);
            showDialog("Mode set to " + (isParkMode ? "Park" : "Drive"));
        });

        Button streamButton = findViewById(R.id.streamButton);
        streamButton.setOnClickListener(v -> {
            if (!isParkMode) {
                showDialog("Please stop your vehicle and turn on Park mode");
            } else {
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                String url = sharedPreferences.getString("url", "");

                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });
    }

    private void updateToggleButton(Button toggleButton) {
        toggleButton.setText(isParkMode ? "P (Park)" : "D (Drive)");
    }

    private void showDialog(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}
