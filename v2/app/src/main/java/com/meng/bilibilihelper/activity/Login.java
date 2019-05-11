package com.meng.bilibilihelper.activity;

import android.app.*;
import android.os.*;
import android.webkit.*;
import com.google.gson.*;
import com.meng.bilibilihelper.javaBean.*;

public class Login extends Activity{

    public void clearWebViewCache(){
        CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeAllCookie();
	  }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        WebView webView = new WebView(this);
        setContentView(webView);
        webView.getSettings().setUserAgentString(MainActivity.instence.userAgent);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        clearWebViewCache();
        webView.setWebViewClient(new WebViewClient() {
			  @Override
			  public boolean shouldOverrideUrlLoading(WebView view,String url){
				  view.loadUrl(url);
				  return true;
				}

			  @Override
			  public void onPageFinished(WebView view,final String url){
				  super.onPageFinished(view,url);
				  if(!url.equals("https://www.bilibili.com/")){
					  return;
					}
				  CookieManager cookieManager = CookieManager.getInstance();
				  final String cookieStr = cookieManager.getCookie(url)==null? "null" :cookieManager.getCookie(url);
				  new Thread(new Runnable() {
						@Override
						public void run(){
							LoginInfoPeople log = new LoginInfoPeople();
							log.cookie=cookieStr;

							String myInfoJson = MainActivity.instence.getSourceCode("https://api.bilibili.com/x/space/myinfo?jsonp=jsonp",cookieStr);
							BilibiliUserInfo bilibiliPersonInfo = new Gson().fromJson(myInfoJson,BilibiliUserInfo.class);
							String json2 = MainActivity.instence.getSourceCode("https://api.bilibili.com/x/space/acc/info?mid="+bilibiliPersonInfo.data.mid+"&jsonp=jsonp");

							log.personInfo=new Gson().fromJson(json2,BilibiliUserInfo.class);

							MainActivity.instence.loginInfo.loginInfoPeople.add(log);
							MainActivity.instence.saveConfig();
							runOnUiThread(new Runnable() {

								  @Override
								  public void run(){
									  if(MainActivity.instence.loginInfo!=null){
										  MainActivity.instence.arrayList.clear();
										  for(LoginInfoPeople loginInfoPeople : MainActivity.instence.loginInfo.loginInfoPeople){
											  MainActivity.instence.arrayList.add(loginInfoPeople.personInfo.data.name);
											}
										}
									  MainActivity.instence.adapter.notifyDataSetChanged();
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

