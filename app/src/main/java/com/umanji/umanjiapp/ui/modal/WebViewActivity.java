package com.umanji.umanjiapp.ui.modal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.umanji.umanjiapp.R;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        String linkUrl = "http://blog.naver.com/mothcar/220673208427";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            linkUrl = extras.getString("url");
        }



        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(linkUrl);
    }
}
