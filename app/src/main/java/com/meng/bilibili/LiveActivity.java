package com.meng.bilibili;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import java.util.HashMap;
import java.util.Map;

public class LiveActivity extends Activity {
    private WebView webView;

    @Override
    protected void onCreate(  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView=new WebView(this);
        Intent intent=getIntent();
        Map<String,String> co=new HashMap<>();
        co=MainActivity.instence.cookieToMap(intent.getStringExtra("cookie"));

        setContentView(webView);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl(intent.getStringExtra("url"),co);

    }
}
