package com.meng.bilibilihelper.activity.main;

import android.app.*;
import android.os.*;
import android.webkit.*;
import com.google.gson.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.libAndHelper.*;

public class Login extends Activity {

    public void clearWebViewCache() {
        CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeAllCookie();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webView = new WebView(this);
        setContentView(webView);
        webView.getSettings().setUserAgentString(MainActivity.instance.userAgent);
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
                if (!url.equals("https://www.bilibili.com/")) {
                    return;
                }
                CookieManager cookieManager = CookieManager.getInstance();
                final String cookieStr = cookieManager.getCookie(url) == null ? "null" : cookieManager.getCookie(url);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
						BilibiliUserInfo bilibiliPersonInfo = new Gson().fromJson(Tools.Network.getSourceCode("https://api.bilibili.com/x/space/myinfo?jsonp=jsonp", cookieStr), BilibiliUserInfo.class);
                        AccountInfo account = new AccountInfo();
                        account.cookie = cookieStr;
                        account.name = bilibiliPersonInfo.data.name;
						account.uid=bilibiliPersonInfo.data.mid;
                        int i,j;
                        for (i = 0,j= MainActivity.instance.loginAccounts.size();i<j; ++i) {
                            if (MainActivity.instance.loginAccounts.get(i).uid == account.uid) {
                                break;
                            }
                        }
                        if (i != MainActivity.instance.loginAccounts.size()) {
                            MainActivity.instance.loginAccounts.set(i, account);
                        } else {
                            MainActivity.instance.loginAccounts.add(account);
                        }
                        MainActivity.instance.saveConfig();
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                MainActivity.instance.mainAccountAdapter.notifyDataSetChanged();
                                finish();
                            }
                        });
                    }
                }).start();
            }
        });
        String loginUrl = "https://passport.bilibili.com/login";
        webView.loadUrl(loginUrl);
    }
}

