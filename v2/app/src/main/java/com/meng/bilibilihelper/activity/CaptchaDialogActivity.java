package com.meng.bilibilihelper.activity;
import Decoder.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.gson.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.javaBean.getAward.*;
import com.meng.bilibilihelper.javaBean.liveCaptcha.*;
import java.io.*;
import com.meng.bilibilihelper.javaBean.*;

public class CaptchaDialogActivity extends Activity{

	public EditText editText;
	ImageView im;
	LinearLayout ll;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.captcha);
		editText=(EditText)findViewById(R.id.et);
		ll=(LinearLayout)findViewById(R.id.drawer_layout);
		im=(ImageView)findViewById(R.id.captchaImageView);
		final GuajiJavaBean guaji=new Gson().fromJson(getIntent().getStringExtra("data"),GuajiJavaBean.class);
		Button btn = (Button) findViewById(R.id.naiAll);
		String s=guaji.liveCaptcha.data.img;
		Bitmap b=getCaptcha(s.substring(s.indexOf(",")+1));
		im.setImageBitmap(b);
		btn.setOnClickListener(new OnClickListener() {

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
							switch(ge.code){
								case 0:
								  showToast(guaji.name+"成功");
								  GuaJiService.guajijavabean.get(guaji.id).isNeedRefresh=true;
								  finish(); 
								  break;
								case -500:
								  showToast(ge.msg);
								  break;
								case -901:								
								case -902:
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
								  MainActivity.instence.showToast(ge.message);
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
  }
