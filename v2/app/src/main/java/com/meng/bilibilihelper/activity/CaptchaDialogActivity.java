package com.meng.bilibilihelper.activity;
import Decoder.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.webkit.*;
import android.widget.*;
import com.google.gson.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.javaBean.getAward.*;
import com.meng.bilibilihelper.javaBean.liveCaptcha.*;
import java.io.*;

public class CaptchaDialogActivity extends Activity{
	WebView webView;
	public EditText editText;
	ImageView im;
	LinearLayout ll;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.captcha);
		editText=(EditText)findViewById(R.id.autoCompleteTextview);
		ll=(LinearLayout)findViewById(R.id.drawer_layout);
		im=(ImageView)findViewById(R.id.captchaImageView);
		final GuajiJavaBean guaji=new Gson().fromJson(getIntent().getStringExtra("data"),GuajiJavaBean.class);
		Button btn = (Button) findViewById(R.id.naiAll);
		String s=guaji.liveCaptcha.data.img;
		Bitmap b=getCaptcha(s.substring(s.indexOf(",")+1));
		im.setImageBitmap(b);
/*
		try{
			saveMyBitmap("/storage/emulated/0/a.bmp",b);
		  }catch(IOException e){}

		
		long start = System.currentTimeMillis();

		webView=(WebView)findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("file:///android_asset/show.html");
		webView.addJavascriptInterface(this,"justTest");
		
		WebSettings ws=webView.getSettings();
		ws.setAppCacheEnabled(true);//启用localstorage本地存储api
		ws.setLightTouchEnabled(true);//启用选中功能
		ws.setDomStorageEnabled(true);//启用dom存储(关键就是这句)，貌似网上twitter显示有问题也是这个属性没有设置的原因
		ws.setDatabaseEnabled(true);//启用html5数据库功能
		
		im.setOnClickListener(new OnClickListener(){

			  @Override
			  public void onClick(View p1){
				  testJS();
				}
			});

		long timeRequired = System.currentTimeMillis()-start;
		//autoCompleteTextView.setHint("识别结果"+textResult+"时间"+timeRequired);
	*/	btn.setOnClickListener(new OnClickListener() {

			  @Override
			  public void onClick(View p1){

				  new Thread(new Runnable(){

						@Override
						public void run(){
							GetAward ge=getGetAward(guaji.liveTimeStamp.data.time_start,
													guaji.liveTimeStamp.data.time_end,
													editText.getText().toString(),
													guaji.cookie,
													guaji.referer);
							MainActivity.instence.showToast(new Gson().toJson(ge));
							NotificationManager manager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);						
							switch(ge.code){
								case 0:
								  showToast(guaji.name+"成功");
								  GuaJiService.guajijavabean.get(guaji.id).isNeedRefresh=true;
								  if(SharedPreferenceHelper.getBoolean("notifi",false)){								  
									  manager.cancel(guaji.id);
									}
								  finish(); 
								  break;
								case -500:  //时间未到
								  showToast(ge.msg);
								  if(SharedPreferenceHelper.getBoolean("notifi",false)){								  
									  manager.cancel(guaji.id);
									}
								  GuaJiService.guajijavabean.get(guaji.id).isNeedRefresh=true;
								  finish();
								  break;
								case -901:	//过期					  
								case -902:  //错误
								  showToast(ge.message);			
								  final LiveCaptcha liveCaptcha = getLiveCaptcha(guaji.referer,guaji.cookie);
								  runOnUiThread(new Runnable(){

										@Override
										public void run(){
											Bitmap b=getCaptcha(liveCaptcha.data.img.substring(liveCaptcha.data.img.indexOf(",")+1));
											im.setImageBitmap(b);
										  }
									  });				
								  break;
								case -903:
								  MainActivity.instence.showToast("已经领取过了");
								  if(SharedPreferenceHelper.getBoolean("notifi",false)){								  
									  manager.cancel(guaji.id);
									}
								  GuaJiService.guajijavabean.get(guaji.id).isNeedRefresh=true;
								  finish();
								  break;
							  }
						  }						
					  }).start();

				}
			});
	  }
	public Bitmap getCaptcha(String base64String){
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] result = new byte[0];
        try{
            result=decoder.decodeBuffer(base64String);
		  }catch(IOException e){
            e.printStackTrace();
		  }
        return BitmapFactory.decodeByteArray(result,0,result.length);
	  }

	public LiveCaptcha getLiveCaptcha(String refer,String cookie){
        return new Gson().fromJson(
		  MainActivity.instence.getSourceCode(
			"https://api.live.bilibili.com/lottery/v1/SilverBox/getCaptcha?ts="+System.currentTimeMillis(),
			cookie,refer),
		  LiveCaptcha.class);
	  }

	public GetAward getGetAward(long time_start,long time_end,String captcha,String cookie,String refer){
        return new Gson().fromJson(
		  MainActivity.instence.getSourceCode(
			"https://api.live.bilibili.com/lottery/v1/SilverBox/getAward?time_start="+time_start+
			"&end_time="+time_end+
			"&captcha="+captcha,cookie,refer),
		  GetAward.class);
	  }
	public void showToast(final String msg){
        runOnUiThread(new Runnable() {

			  @Override
			  public void run(){
				  Toast.makeText(CaptchaDialogActivity.this,msg,Toast.LENGTH_LONG).show();
				}
			});
	  }

	  
	public String saveMyBitmap(String bitName,Bitmap mBitmap) throws IOException{
        File f = new File(bitName);
        if(!f.getParentFile().exists()){
            f.getParentFile().mkdirs();
		  }
        f.createNewFile();
        FileOutputStream fOut = null;
        fOut=new FileOutputStream(f);
        mBitmap.compress(Bitmap.CompressFormat.PNG,100,fOut);
        fOut.flush();
        fOut.close();
        return f.getAbsolutePath();
	  }
	
	  
	public void testJS(){
		webView.loadUrl("javascript:test()");
	  }

 @JavascriptInterface
	public void hello(final String msg){
		Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
	 runOnUiThread(new Runnable(){

		   @Override
		   public void run(){
			   editText.setText(msg);
			 }
		 });
	  }
  }
