package com.LnPay.driver.accounts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.LnPay.driver.R;

public class Payment extends AppCompatActivity {

    private WebView webview;
    private String authUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        //received authUrl from paymentAccountAdapter.java or AccountActivity.java
        authUrl = getIntent().getStringExtra("authUrl");
        webview = findViewById(R.id.webView);

        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setJavaScriptEnabled(false);

        webview.getSettings().setDomStorageEnabled(true);
        webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webview.canGoBack();
        webview.loadUrl(authUrl);

    }
}