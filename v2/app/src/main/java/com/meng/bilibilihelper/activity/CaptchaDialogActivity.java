package com.meng.bilibilihelper.activity;
import Decoder.*;
import android.app.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.gson.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.javaBean.getAward.*;
import java.io.*;
import java.util.*;
import android.content.*;

public class CaptchaDialogActivity extends Activity{

	public EditText editText;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_fragment);
		editText=(EditText)findViewById(R.id.et);
		final Intent inte=getIntent();
		Button btn = (Button) findViewById(R.id.naiAll);
        editText.setText(inte.getStringExtra("base64"));
		btn.setText("send");
		((Button)findViewById(R.id.signAll)).setVisibility(View.GONE);
		btn.setOnClickListener(new OnClickListener() {

			  @Override
			  public void onClick(View p1){
				
				  new Thread(new Runnable(){

						@Override
						public void run(){
						MainActivity.instence.showToast(new Gson().toJson(getGetAward(inte.getStringExtra("start"),
										inte.getStringExtra("end"),
										editText.getText().toString(),
										inte.getStringExtra("cookie"))));
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

	public GetAward getGetAward(String time_start,String time_end,String captcha,String cookie){
        return new Gson().fromJson(
		  MainActivity.instence.getSourceCode(
			"https://api.live.bilibili.com/lottery/v1/SilverBox/getAward?time_start="+time_start+
			"&end_time="+time_end+
			"&captcha="+captcha,cookie),
		  GetAward.class);
	  }

  }
