package com.meng.bilibilihelper;

import android.app.*;
import android.os.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import com.meng.bilibili.javaBean.*;

public class Login extends Activity{

	private EditText editText;

    public void clearWebViewCache(){
        CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeAllCookie();
	  }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
		WebView webView = new WebView(this);
		editText =new EditText(this);
		editText.setHint("对此账号的称呼");
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
							log.name= editText.getText().toString();
							log.cookie=cookieStr;
							MainActivity.instence.loginInfo.loginInfoPeople.add(log);
							MainActivity.instence.saveConfig();
							runOnUiThread(new Runnable(){

								  @Override
								  public void run(){			
									  if(MainActivity.instence.loginInfo!=null){
										  MainActivity.instence.loginInfoPeopleHashMap.clear();
										  MainActivity.instence.arrayList.clear();									
										  for(LoginInfoPeople loginInfoPeople : MainActivity.instence.loginInfo.loginInfoPeople){											  
											  MainActivity.instence.loginInfoPeopleHashMap.put(loginInfoPeople.name,loginInfoPeople);
											  MainActivity.instence.arrayList.add(loginInfoPeople.name);
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

	@Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);     
        MenuItem add = menu.add(1,1,1,"添加");
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        add.setActionView(editText);
        return true;
	  }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
		return true;
	  }

  }


