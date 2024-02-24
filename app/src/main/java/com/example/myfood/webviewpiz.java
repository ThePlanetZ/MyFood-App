package com.example.myfood;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class webviewpiz extends AppCompatActivity {
    WebView webview;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webviewpiz);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        webview = findViewById(R.id.webview1);
        webview.setWebViewClient(new WebViewClient());

        String url = getIntent().getStringExtra("URL");
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webview.loadUrl(url); // Load the URL into WebView
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            super.onBackPressed();
        }
    }
}