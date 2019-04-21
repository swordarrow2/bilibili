package com.meng.bilibili;

import android.os.*;
import android.webkit.*;
import android.app.*;
import com.meng.bilibili.lib.*;

public class Login extends Activity{

	private WebView webView;
    private String loginUrl = "https://www.bilibili.com";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        webView=new WebView(this);
		setContentView(webView);
		webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient() {
			  @Override
			  public boolean shouldOverrideUrlLoading(WebView view,String url){
				  view.loadUrl(url);
				  return true;
				}

			  @Override
			  public void onPageFinished(WebView view,String url){
				  super.onPageFinished(view,url);
				  CookieManager cookieManager = CookieManager.getInstance();
				  String CookieStr = cookieManager.getCookie(url)==null? "null" :cookieManager.getCookie(url);
				  SharedPreferenceHelper.putString("cookie",CookieStr);
				}
			});
        webView.loadUrl(loginUrl);
	  }
  }


