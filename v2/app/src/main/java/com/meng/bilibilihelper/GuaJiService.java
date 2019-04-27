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
import java.text.*;
import java.util.*;

public class GuaJiService extends Service{

	Notification.Builder builder;
	NotificationManager manager;

	@Override
	public IBinder onBind(Intent p1){
		return null;
	  }

    int m=0;
	public static ArrayList<GuajiJavaBean> guajijavabean=new ArrayList<>();

	@Override
	public void onCreate(){
		super.onCreate();
		manager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		builder=new Notification. Builder(this);
		new Thread(new Runnable(){

			  @Override
			  public void run(){
				  while(true){
					  for(GuajiJavaBean g:guajijavabean){
						  try{
							  sendHeartBeat(g.isFirstHeartBeat,g.cookie);
							}catch(Exception e){

							}
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
					  for(GuajiJavaBean g:guajijavabean){
						  if(g.isNeedRefresh){
							  try{
								  g.liveTimeStamp=getLiveTimeStamp(g.referer,g.cookie);
								  g.isNeedRefresh=false;
								  g.isShowed=false;
								  sendStartNotification(g);
								}catch(Exception e){

								}
							}
						  if(System.currentTimeMillis()/1000>g.liveTimeStamp.data.time_end){	
							  if(g.isShowed)continue;
							  g.liveCaptcha=getLiveCaptcha(g.referer,g.cookie);							  
							  sendNotification(g);
							  g.isShowed=true;
							}
						}		
					  try{
						  Thread.sleep(2000);
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
				  MainActivity.instence.showToast("任务开始");		  
				  GuajiJavaBean gua=new GuajiJavaBean(intent.getStringExtra("name"),intent.getStringExtra("refer"),intent.getStringExtra("cookie"));		
				  for(GuajiJavaBean g:guajijavabean){
					  if(g.cookie.equals(gua.cookie)){
						  MainActivity.instence.showToast("任务已添加");
						  return;
						}
					}
				  gua.liveTimeStamp=getLiveTimeStamp(gua.referer,gua.cookie);
				  gua.liveCaptcha=getLiveCaptcha(gua.referer,gua.cookie);	
				  gua.id=m++;
				  guajijavabean.add(gua);
				  sendStartNotification(gua);
				}
			}).start();

		return super.onStartCommand(intent,flags,startId);
	  }

	private void sendStartNotification(GuajiJavaBean g){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("该账号正在运行:"+g.name)//设置通知栏标题
		  .setContentText("下一次"+timeStampToDate(g.liveTimeStamp.data.time_end*1000))
		  .setTicker("通知") //通知首次出现在通知栏，带上升动画效果的
		  .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
		  .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
		Notification notification = builder.build();
        if(notificationManager!=null){
            notificationManager.notify(g.id,notification);
		  }
	  }

	private void sendNotification(GuajiJavaBean g){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        Intent notificationIntent = new Intent(this,CaptchaDialogActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		notificationIntent.putExtra("data",new Gson().toJson(g));
		PendingIntent pendingIntent = PendingIntent.getActivity(this,g.id,notificationIntent,PendingIntent.FLAG_CANCEL_CURRENT);      	
		builder.setContentTitle(g.name+"验证")//设置通知栏标题
		  .setContentIntent(pendingIntent) //设置通知栏点击意图
		  .setContentText("为该账号输入验证码:"+g.name)
		  .setTicker("通知内容") //通知首次出现在通知栏，带上升动画效果的
		  .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
		  .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
		Notification notification = builder.build();
        notification.flags|=Notification.FLAG_NO_CLEAR;
		notification.flags|=Notification.FLAG_ONGOING_EVENT;
        if(notificationManager!=null){
            notificationManager.notify(g.id,notification);
		  }
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
	  
	public static String timeStampToDate(long tsp, String... format) {
        SimpleDateFormat sdf;
        if (format.length < 1) {
            sdf = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());
		  } else {
            sdf = new SimpleDateFormat(format[0], Locale.getDefault());
		  }
        return sdf.format(tsp);
	 } 
  }
