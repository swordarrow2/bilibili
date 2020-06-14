package com.meng.sjfmd.activity;

import android.app.*;
import android.content.*;
import android.os.*;
import android.webkit.*;
import android.util.*;
import android.widget.*;
import com.meng.biliv3.activity.*;

public class QRlogin extends Activity {
	
	WebView wv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		wv = new WebView(this);
				wv.getSettings().setJavaScriptEnabled(true);
				wv.getSettings().setBuiltInZoomControls(true);
		wv.setWebViewClient(new MyWebViewClient());
		setContentView(wv);
		wv.loadUrl("https://passport.bilibili.com/qrcode/h5/login?oauthKey=a25f98b35abaa109a5b43fd3a9bb617b");
	}
	
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			System.out.println("----------------------shouldOverrideUrlLoading 。。 url:" + url);
			//Log.i("qr",url);
			Toast.makeText(QRlogin.this,url,Toast.LENGTH_LONG).show();
			if (url.startsWith("https://passport.bilibili.com/mobile/h5-confirm")) {
				
				
				CookieSyncManager.createInstance(QRlogin.this);  
				CookieManager cookieManager = CookieManager.getInstance();  
				cookieManager.setAcceptCookie(true);  
				cookieManager.removeSessionCookie();//移除  
				cookieManager.setCookie(url, MainActivity.instance.accountManager.get(1).cookie);//cookies是在HttpClient中获得的cookie  
				CookieSyncManager.getInstance().sync();
				
				
				view.loadUrl(url);
			}  
			 
			 return true;
		}


		@Override
		public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
		}
	}
}
