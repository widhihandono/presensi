package com.presensi.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Bantuan_Activity extends AppCompatActivity {
    private WebView webView;
    private TextView tvNoInternet;
    private ImageView img;
    private Button btnTryAgain;
    private ProgressBar bar;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bantuan_);
        getSupportActionBar().setTitle("Bantuan SIABA");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = findViewById(R.id.webView);
        bar = findViewById(R.id.progressBar2);
        tvNoInternet = findViewById(R.id.tvNoInternet);
        btnTryAgain = findViewById(R.id.btnTryAgain);
        img = findViewById(R.id.img);


        if(isNetworkAvailable())
        {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new MyBrowser());
            webView.loadUrl("http://presensi.magelangkab.go.id/help");


            webView.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });

        }
        else
        {
            webView.setVisibility(View.GONE);
            img.setVisibility(View.VISIBLE);
            btnTryAgain.setVisibility(View.VISIBLE);
            tvNoInternet.setVisibility(View.VISIBLE);
        }

        btnTryAgain.setOnClickListener(l->{
            startActivity(new Intent(Bantuan_Activity.this,Bantuan_Activity.class));
            finish();
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class MyBrowser extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            bar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            webView.loadUrl(url);
            return super.shouldOverrideUrlLoading(view,url);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack())
        {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            startActivity(new Intent(Bantuan_Activity.this,Menu_Utama_Activity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Bantuan_Activity.this,Menu_Utama_Activity.class));
        finish();
    }
}
