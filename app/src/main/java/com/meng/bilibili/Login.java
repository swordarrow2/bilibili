package com.meng.bilibili;

import android.content.Intent;
import android.os.*;
import android.webkit.*;
import android.app.*;

import com.meng.bilibili.javaBean.BilibiliPersonInfo;
import com.meng.bilibili.javaBean.LoginInfoPeople;

public class Login extends Activity {

    private WebView webView;
    private String loginUrl = "https://www.bilibili.com";
    private long id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        id = intent.getLongExtra("id", 0);
        webView = new WebView(this);
        setContentView(webView);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                CookieManager cookieManager = CookieManager.getInstance();
                final String cookieStr = cookieManager.getCookie(url) == null ? "null" : cookieManager.getCookie(url);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BilibiliPersonInfo bilibiliPersonInfo = MainActivity.instence.gson.fromJson(
                                MainActivity.instence.getSourceCode("https://api.bilibili.com/x/space/acc/info?mid= " + id + "&jsonp=jsonp"),
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


