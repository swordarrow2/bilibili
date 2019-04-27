package com.meng.bilibilihelper;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import com.google.gson.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.javaBean.liveCaptcha.*;
import com.meng.bilibilihelper.javaBean.liveTimeStamp.*;

public class GuaJiService extends IntentService{

    int m=0;

	public GuaJiService(){
		super("guaji");
	  }

    @Override
	protected void onHandleIntent(final Intent intent){
		/*	Intent ii=new Intent(getApplicationContext(),CaptchaDialogActivity.class);
		 ii.putExtra("start","liveTimeStamp.data.time_start");
		 ii.putExtra("end","liveTimeStamp.data.time_end");
		 ii.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		 getApplicationContext().startActivity(ii);

		 if(1==1)return;*/
		MainActivity.instence.showToast("start");
		LoginInfoPeople loginInfoPeople = MainActivity.instence.loginInfo.loginInfoPeople.get(intent.getIntExtra("pos",0));
		String cookie = loginInfoPeople.cookie;
		String refer = intent.getStringExtra("refer");
		sendHeartBeat(true);
		while(true){
			LiveTimeStamp liveTimeStamp = getLiveTimeStamp(refer,cookie);
			LiveCaptcha liveCaptcha = getLiveCaptcha(refer,cookie);	
			MainActivity.instence.showToast(refer+"\n"+liveTimeStamp.data.time_start+"\n"+liveCaptcha.data.img);	
			if(System.currentTimeMillis()/1000>liveTimeStamp.data.time_end){
				//	showDialog(liveTimeStamp, liveCaptcha);
				Intent i=new Intent(getApplicationContext(),CaptchaDialogActivity.class);
				i.putExtra("start",liveTimeStamp.data.time_start);
				i.putExtra("end",liveTimeStamp.data.time_end);
				i.putExtra("cookie",cookie);
				i.putExtra("base64",liveCaptcha.data.img);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				getApplicationContext().startActivity(i);
			  }
			try{
				Thread.sleep(30000);
			  }catch(InterruptedException e){
				e.printStackTrace();
			  }
			sendHeartBeat(false);
		  }

	  }

    public LiveTimeStamp getLiveTimeStamp(String refer,String cookie){
        return new Gson().fromJson(
		  MainActivity.instence.getSourceCode(
			"https://api.live.bilibili.com/lottery/v1/SilverBox/getCurrentTask",
			cookie,refer),
		  LiveTimeStamp.class);
	  }

    public LiveCaptcha getLiveCaptcha(String refer,String cookie){
        return new Gson().fromJson(
		  MainActivity.instence.getSourceCode(
			"https://api.live.bilibili.com/lottery/v1/SilverBox/getCaptcha?ts="+System.currentTimeMillis(),
			cookie,refer),
		  LiveCaptcha.class);
	  }



    public void sendHeartBeat(boolean isFirst){
        if(isFirst){
			/*      return new Gson().fromJson(
			 MainActivity.instence.getSourceCode(
			 "https://api.live.bilibili.com/relation/v1/feed/heartBeat?_=" + System.currentTimeMillis()),
			 GetAward.class);*/
            MainActivity.instence.getSourceCode("https://api.live.bilibili.com/relation/v1/feed/heartBeat?_="+System.currentTimeMillis());
		  }else{
			/*    return new Gson().fromJson(
			 MainActivity.instence.getSourceCode(
			 "https://api.live.bilibili.com/relation/v1/Feed/heartBeat"),
			 GetAward.class);*/
            MainActivity.instence.getSourceCode("https://api.live.bilibili.com/relation/v1/Feed/heartBeat");
		  }
	  }



    public boolean showDialog(final LiveTimeStamp liveTimeStamp,LiveCaptcha liveCaptcha){
        boolean succeess = false;

        //Bitmap bitmap = getCaptcha(liveCaptcha.data.img);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getApplicationContext());
        dialogBuilder.setTitle("提示");
        dialogBuilder.setMessage("xxxxxxxxxxxxx");
        dialogBuilder.setCancelable(false);
        dialogBuilder.setNegativeButton("确定",new DialogInterface.OnClickListener() {
			  @Override
			  public void onClick(DialogInterface dialog,int which){
				  /*  GetAward award = getGetAward(liveTimeStamp.data.time_start, liveTimeStamp.data.time_end, 0);
				   if (award.code != 0) {
				   MainActivity.instence.showToast(award.message);
				   } else {
				   MainActivity.instence.showToast("成功");
				   }*/
				}
			});
        AlertDialog alertDialog = dialogBuilder.create();
        if(Build.VERSION.SDK_INT>=26){ //安卓8.0
            alertDialog.getWindow().setType(2037);
		  }else{
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
		  }
        alertDialog.show();
        return succeess;
	  }

  }
