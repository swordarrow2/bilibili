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
		setContentView(R.layout.main_fragment);
		editText=(EditText)findViewById(R.id.et);
		ll=(LinearLayout)findViewById(R.id.drawer_layout);
		im=new ImageView(this);
		ll.addView(im);
		final GuajiJavaBean guaji=new Gson().fromJson(getIntent().getStringExtra("data"),GuajiJavaBean.class);
		Button btn = (Button) findViewById(R.id.naiAll);
		String s=guaji.liveCaptcha.data.img;
		Bitmap b=getCaptcha(s.substring(s.indexOf(",")+1));
		im.setImageBitmap(b);
		btn.setText("send");
		((Button)findViewById(R.id.signAll)).setVisibility(View.GONE);
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
							if(ge.code!=0){
								showToast("验证码错误");					  
								final LiveCaptcha liveCaptcha = getLiveCaptcha(guaji.referer,guaji.cookie);
								runOnUiThread(new Runnable(){

									  @Override
									  public void run(){
										  Bitmap b=getCaptcha(liveCaptcha.data.img.substring(liveCaptcha.data.img.indexOf(",")+1));
										  im.setImageBitmap(b);
										}
									});					  
							  }else{
								MainActivity.instence.showToast(guaji.id+"成功");
								GuaJiService.guajijavabean.get(guaji.id).isNeedRefresh=true;
								finish();
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
