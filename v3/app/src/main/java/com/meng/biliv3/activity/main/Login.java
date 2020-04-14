package com.meng.biliv3.activity.main;

import android.app.*;
import android.os.*;
import android.webkit.*;
import com.google.gson.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.javaBean.*;
import com.meng.biliv3.libs.*;

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
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(true);
        clearWebViewCache();
        webView.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					return true;
				}

				@Override
				public void onPageFinished(final WebView view, final String url) {
					super.onPageFinished(view, url);
					//	MainActivity.instance.showToast(url);
					if (url.equals("https://passport.bilibili.com/login")) {
						AccountInfo aci=MainActivity.instance.loginAccounts.get(getIntent().getIntExtra("pos", -1));
						if (aci.phone != 0 && aci.password != null) {
							view.evaluateJavascript(Tools.AndroidContent.readAssetsString("patchDelete"), null);
							view.evaluateJavascript(String.format(Tools.AndroidContent.readAssetsString("patchInput"), aci.phone, aci.password), null);
							MainActivity.instance.threadPool.execute(new Runnable(){

									@Override
									public void run() {
										try {
											Thread.sleep(2000);
										} catch (InterruptedException e) {}
										MainActivity.instance.runOnUiThread(new Runnable(){

												@Override
												public void run() {
													view.evaluateJavascript("javascript:document.querySelectorAll('.btn-login')[0].click();", null);
												}
											});
									}
								});
						}
					}
					if (!url.equals("https://www.bilibili.com/")) {
						return;
					}
					CookieManager cookieManager = CookieManager.getInstance();
					final String cookieStr = cookieManager.getCookie(url) == null ? "null" : cookieManager.getCookie(url);
					new Thread(new Runnable() {
							@Override
							public void run() {
								BilibiliUserInfo bilibiliPersonInfo = new Gson().fromJson(Tools.Network.getSourceCode("https://api.bilibili.com/x/space/myinfo?jsonp=jsonp", cookieStr), BilibiliUserInfo.class);
								AccountInfo account = MainActivity.instance.loginAccounts.get(getIntent().getIntExtra("pos", -1));
								account.cookie = cookieStr;
								account.name = bilibiliPersonInfo.data.name;
								account.uid = bilibiliPersonInfo.data.mid;
								account.setCookieExceed(false);
								account.setSigned(false);
								int i,j;
								for (i = 0,j = MainActivity.instance.loginAccounts.size();i < j; ++i) {
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

