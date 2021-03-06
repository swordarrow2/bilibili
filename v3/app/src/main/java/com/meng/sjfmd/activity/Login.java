package com.meng.sjfmd.activity;

import android.app.*;
import android.content.*;
import android.os.*;
import android.webkit.*;
import com.meng.biliv3.activity.*;
import com.meng.sjfmd.javabean.*;
import com.meng.sjfmd.libs.*;
import com.meng.sjfmd.result.*;

public class Login extends Activity {

	String loginUrl = "https://passport.bilibili.com/login";

    public void clearWebViewCache() {
        CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeAllCookie();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webView = new WebView(this);
        setContentView(webView);
        WebSettings settings = webView.getSettings();
		settings.setUserAgentString(MainActivity.instance.userAgent);
        settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setBuiltInZoomControls(true);
		settings.setAllowFileAccess(true);
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
					//	view.evaluateJavascript(Tools.AndroidContent.readAssetsString("patchDelete.js"), null);
					if (url.equals(loginUrl)) {
						int po=getIntent().getIntExtra("pos", -1);
						if (po == -1) {
							return;
						}
						AccountInfo aci=MainActivity.instance.accountManager.get(po);
						if (aci.phone != 0 && aci.password != null) {
							view.evaluateJavascript(String.format(AndroidContent.readAssetsString("patchInput.js"), aci.phone, aci.password), null);
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
					MainActivity.instance.threadPool.execute(new Runnable() {
							@Override
							public void run() {
								UserInfo bilibiliPersonInfo = GSON.fromJson(Network.httpGet("https://api.bilibili.com/x/space/myinfo?jsonp=jsonp", cookieStr), UserInfo.class);
								int po=getIntent().getIntExtra("pos", -1);
								final AccountInfo account =po == -1 ?new AccountInfo(): MainActivity.instance.accountManager.get(po);
								account.cookie = cookieStr;
								account.name = bilibiliPersonInfo.data.name;
								account.uid = bilibiliPersonInfo.data.mid;
								account.setCookieExceed(false);
								account.setSigned(false);
								Login.this.runOnUiThread(new Runnable(){

										@Override
										public void run() {
											Intent in = new Intent();
											in.putExtra("aci", account.toString());
											setResult(RESULT_OK, in);
											finish();
										}
									});
							}
						});
				}
			});
        webView.loadUrl(loginUrl);
    }
}

