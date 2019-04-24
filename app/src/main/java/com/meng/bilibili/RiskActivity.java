package com.meng.bilibili;

import android.app.*;
import android.os.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import com.meng.bilibili.javaBean.*;
import android.content.*;

public class RiskActivity extends Activity{

	private WebView webView;
	private String loginUrl = "";

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		webView=new WebView(this);
		Intent intent=getIntent();
		loginUrl=intent.getStringExtra("url");
		setContentView(webView);
		webView.getSettings().setUserAgentString(MainActivity.UA);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.setWebViewClient(new WebViewClient() {
			  @Override
			  public boolean shouldOverrideUrlLoading(WebView view,String url){
				  view.loadUrl(url);
				  return true;
				}

			  @Override
			  public void onPageFinished(WebView view,final String url){
				  super.onPageFinished(view,url);
				  runOnUiThread(new Runnable(){

						@Override
						public void run(){
							Toast.makeText(RiskActivity.this,url,Toast.LENGTH_LONG).show();
						  }
					  });
		/*		  if(!url.equals("https://www.bilibili.com/")){
					  return;
					}
			      CookieManager cookieManager = CookieManager.getInstance();
				  final String cookieStr = cookieManager.getCookie(url)==null? "null" :cookieManager.getCookie(url);

				  new Thread(new Runnable() {
						@Override
						public void run(){		  			                   
							LoginInfoPeople log = new LoginInfoPeople();						
							log.name=et.getText().toString();
							log.cookie=cookieStr;
							MainActivity.loginInfo.loginInfoPeople.add(log);
							MainActivity.saveConfig();
							runOnUiThread(new Runnable(){

								  @Override
								  public void run(){			
									  if(MainActivity.loginInfo!=null){
										  MainActivity.instence.hashMap.clear();
										  MainActivity.instence.arrayList.clear();									
										  for(LoginInfoPeople loginInfoPeople : MainActivity.loginInfo.loginInfoPeople){											  
											  MainActivity.instence.hashMap.put(loginInfoPeople.name,loginInfoPeople);
											  MainActivity.instence.arrayList.add(loginInfoPeople.name);
											}
										}
									  MainActivity.instence.adapter.notifyDataSetChanged();  
									  finish();
									}
								});
						  }
					  }).start();*/
				}
			});
		webView.loadUrl(loginUrl);
	  }
  }
