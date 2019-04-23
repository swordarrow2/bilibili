package com.meng.bilibili;

import android.content.Intent;
import android.os.*;
import android.webkit.*;
import android.app.*;

import com.google.gson.Gson;
import com.meng.bilibili.javaBean.BilibiliPersonInfo;
import com.meng.bilibili.javaBean.LoginInfoPeople;

public class Login extends Activity {

    private WebView webView;
    private String loginUrl = "https://www.bilibili.com";

    public void clearWebViewCache() {
        CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeAllCookie();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        setContentView(webView);
        webView.getSettings().setUserAgentString(MainActivity.UA);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        clearWebViewCache();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                super.onPageFinished(view, url);
                CookieManager cookieManager = CookieManager.getInstance();
                final String cookieStr = cookieManager.getCookie(url) == null ? "null" : cookieManager.getCookie(url);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BilibiliPersonInfo bilibiliPersonInfo = new Gson().fromJson(
                                MainActivity.getSourceCode(
                                        "https://api.bilibili.com/x/space/acc/info?mid=" +
                                                url.substring(url.indexOf("/")+1) +
                                                "&jsonp=jsonp"),
                                BilibiliPersonInfo.class
                        );
                        LoginInfoPeople log = new LoginInfoPeople();
                        log.name = bilibiliPersonInfo.data.name;
                        log.cookie = cookieStr;
                        MainActivity.loginInfo.loginInfoPeople.add(log);
                        MainActivity.saveConfig();
                    }
                }).start();
            }
        });
        webView.loadUrl(loginUrl);
    }
}


