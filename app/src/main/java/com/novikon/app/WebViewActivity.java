package com.novikon.app;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progressBar;
    private Handler timeoutHandler = new Handler();
    private Runnable timeoutRunnable = () -> {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(WebViewActivity.this, "Превышено время загрузки", Toast.LENGTH_SHORT).show();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            String title = getIntent().getStringExtra("title");
            getSupportActionBar().setTitle(title != null ? title : "Новость");
        }

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setCacheMode(android.webkit.WebSettings.LOAD_NO_CACHE);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                timeoutHandler.removeCallbacks(timeoutRunnable);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                progressBar.setVisibility(View.GONE);
                timeoutHandler.removeCallbacks(timeoutRunnable);
                Toast.makeText(WebViewActivity.this, "Ошибка загрузки", Toast.LENGTH_SHORT).show();
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100 && progressBar.getVisibility() != View.VISIBLE) {
                    progressBar.setVisibility(View.VISIBLE);
                    timeoutHandler.postDelayed(timeoutRunnable, 15000);
                } else if (newProgress >= 100) {
                    progressBar.setVisibility(View.GONE);
                    timeoutHandler.removeCallbacks(timeoutRunnable);
                }
            }
        });

        String url = getIntent().getStringExtra("url");
        if (url != null && !url.isEmpty()) {
            webView.loadUrl(url);
        } else {
            Toast.makeText(this, "Ссылка не найдена", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
