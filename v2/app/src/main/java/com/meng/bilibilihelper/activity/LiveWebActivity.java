package com.meng.bilibilihelper.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

public class LiveWebActivity extends Activity{
    @Override
    protected void onCreate(  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webView=new WebView(this);
        setContentView(webView);
        Intent intent=getIntent();
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();//移除
        cookieManager.setCookie(intent.getStringExtra("url"),intent.getStringExtra("cookies") );//cookies是在HttpClient中获得的cookie
        CookieSyncManager.getInstance().sync();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString(MainActivity.instence.userAgent);
        webView.loadUrl(intent.getStringExtra("url"));
    }
}
