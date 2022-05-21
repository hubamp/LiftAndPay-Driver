package com.LnPay.driver.accounts;

import static com.LnPay.driver.API.paystack.paystack.paymentRef;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.LnPay.driver.API.paystack.paystack;
import com.LnPay.driver.Dashboard;
import com.LnPay.driver.R;
import com.LnPay.driver.fastClass.StringFunction;

public class Payment extends AppCompatActivity {

    private WebView webview;
    private String authUrl;
    private AlertDialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        //received authUrl from paymentAccountAdapter.java or AccountActivity.java
        authUrl = getIntent().getStringExtra("authUrl");
        webview = findViewById(R.id.webView);

        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setJavaScriptEnabled(true);

        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().getJavaScriptCanOpenWindowsAutomatically();
        webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webview.canGoBack();

//        https://checkout.paystack.com/fzu78mttodbqjvl

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Toast.makeText(Payment.this, "Web error: " + error.getDescription(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                Toast.makeText(Payment.this, "ssl error: " + error.getPrimaryError(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                AlertDialog.Builder waiting_progressAlert = new AlertDialog.Builder(Payment.this);
                waiting_progressAlert.setView(LayoutInflater.from(Payment.this).inflate(R.layout.alert_payment_progress, null));
                waiting_progressAlert.setCancelable(false);
                waitingDialog = waiting_progressAlert.create();
                waitingDialog.show();

                paystack response = new paystack(Payment.this);

                String refId = paymentRef;

                response.checkPendingCharge(refId, waitingDialog);
                Log.i("Charge Looper", " Responded");

                Log.i("LoadedURL", view.getUrl() + " (url) " + request.getUrl().toString());

                return false;
            }
        });
        webview.loadUrl(authUrl);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        paymentRef = "NoRef";
    }
}