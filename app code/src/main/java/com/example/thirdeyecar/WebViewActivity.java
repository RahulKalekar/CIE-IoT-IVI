package com.example.thirdeyecar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_STREAM_URL = "stream_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        WebView webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // page load events can be handled here :)
            }
        });

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String url = prefs.getString(KEY_STREAM_URL, "https://youtube.com/");

        if (url != null && !url.isEmpty()) {
            webView.loadUrl(url);
        } else {
            webView.loadUrl("about:blank"); 
        }

        Log.d("WebViewActivity", "Loaded URL: " + url);

        Button btnStopStreaming = findViewById(R.id.btn_stop_streaming);
        Button btnStopAndClose = findViewById(R.id.btn_stop_and_close);

        btnStopStreaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopStreaming();
                finish();
            }
        });

        btnStopAndClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopStreaming();
                finishAffinity(); 
            }
        });
    }

    private void stopStreaming() {
        WebView webView = findViewById(R.id.webView);
        webView.stopLoading();
        webView.loadUrl("about:blank");
    }
}
