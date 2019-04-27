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
import java.util.*;

public class GuaJiService extends Service{

	@Override
	public IBinder onBind(Intent p1){
		return null;
	  }

    int m=0;
	public static ArrayList<GuajiJavaBean> guajijavabean=new ArrayList<>();

	@Override
	public void onCreate(){
		super.onCreate();
		new Thread(new Runnable(){

			  @Override
			  public void run(){
				  while(true){
					  for(GuajiJavaBean g:guajijavabean){
						  sendHeartBeat(g.isFirstHeartBeat,g.cookie);
						  g.isFirstHeartBeat=false;
						}
					  try{
						  Thread.sleep(30000);
						}catch(InterruptedException e){}
					}
				}
			}).start();
		new Thread(new Runnable(){

			  @Override
			  public void run(){
				  while(true){
					String s="";
					  for(GuajiJavaBean g:guajijavabean){
						if(g.isNeedRefresh){
							LiveTimeStamp liveTimeStamp = getLiveTimeStamp(g.referer,g.cookie);
							LiveCaptcha liveCaptcha = getLiveCaptcha(g.referer,g.cookie);	
							g.timeStart=liveTimeStamp.data.time_start;
							g.timeEnd=liveTimeStamp.data.time_end;
							g.bitmap=liveCaptcha.data.img;
							g.isNeedRefresh=false;
							g.isShowed=false;
						}
						s+=g.id+"正在运行\n";
						  if(System.currentTimeMillis()/1000>g.timeEnd){	
						  if(g.isShowed)continue;
							  Intent i=new Intent(getApplicationContext(),CaptchaDialogActivity.class);
							  i.putExtra("start",g.timeStart);
							  i.putExtra("end",g.timeEnd);
							  i.putExtra("cookie",g.cookie);
							  i.putExtra("base64",g.bitmap);
							  i.putExtra("refer",g.referer);
							  i.putExtra("pos",g.id);
							  i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
							  getApplicationContext().startActivity(i);
							  g.isShowed=true;
							}
						}
						MainActivity.instence.showToast(s);
					  try{
						  Thread.sleep(5000);
						}catch(InterruptedException e){}
					}
				}
			}).start();
			
	  }

	
	@Override
	public int onStartCommand(final Intent intent,int flags,int startId){
		new Thread(new Runnable(){

			  @Override
			  public void run(){
				  MainActivity.instence.showToast("任务"+intent.getIntExtra("pos",0)+"开始");
				  GuajiJavaBean gua=new GuajiJavaBean();
				  LoginInfoPeople loginInfoPeople = MainActivity.instence.loginInfo.loginInfoPeople.get(intent.getIntExtra("pos",0));
				  gua.cookie = loginInfoPeople.cookie;
				  for(GuajiJavaBean g:guajijavabean){
					if(g.cookie.equals(gua.cookie)){
					  MainActivity.instence.showToast("任务已添加");
					  return;
					}
				  }
				  gua.referer= intent.getStringExtra("refer");
				  LiveTimeStamp liveTimeStamp = getLiveTimeStamp(gua.referer,gua.cookie);
				  LiveCaptcha liveCaptcha = getLiveCaptcha(gua.referer,gua.cookie);	
				  gua.timeStart=liveTimeStamp.data.time_start;
				  gua.timeEnd=liveTimeStamp.data.time_end;
				  gua.bitmap=liveCaptcha.data.img;
				  gua.id=m++;
				  guajijavabean.add(gua);
				}
			}).start();
		
		return super.onStartCommand(intent,flags,startId);
	  }

    public LiveTimeStamp getLiveTimeStamp(String refer,String cookie){
        return new Gson().fromJson(
		  MainActivity.instence.getSourceCode(
			"https://api.live.bilibili.com/lottery/v1/SilverBox/getCurrentTask",
			cookie),
		  LiveTimeStamp.class);
	  }

    public LiveCaptcha getLiveCaptcha(String refer,String cookie){
        return new Gson().fromJson(
		  MainActivity.instence.getSourceCode(
			"https://api.live.bilibili.com/lottery/v1/SilverBox/getCaptcha?ts="+System.currentTimeMillis(),
			cookie,refer),
		  LiveCaptcha.class);
	  }



    public void sendHeartBeat(boolean isFirst,String cookie){
        if(isFirst){
			/*      return new Gson().fromJson(
			 MainActivity.instence.getSourceCode(
			 "https://api.live.bilibili.com/relation/v1/feed/heartBeat?_=" + System.currentTimeMillis()),
			 GetAward.class);*/
            MainActivity.instence.getSourceCode("https://api.live.bilibili.com/relation/v1/feed/heartBeat?_="+System.currentTimeMillis(),cookie);
		  }else{
			/*    return new Gson().fromJson(
			 MainActivity.instence.getSourceCode(
			 "https://api.live.bilibili.com/relation/v1/Feed/heartBeat"),
			 GetAward.class);*/
            MainActivity.instence.getSourceCode("https://api.live.bilibili.com/relation/v1/Feed/heartBeat",cookie);
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
