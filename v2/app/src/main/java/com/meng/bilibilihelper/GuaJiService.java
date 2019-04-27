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
								  LiveTimeStamp liveTimeStamp = getLiveTimeStamp(g.referer,g.cookie);
								  LiveCaptcha liveCaptcha = getLiveCaptcha(g.referer,g.cookie);	
								  g.timeStart=liveTimeStamp.data.time_start;
								  g.timeEnd=liveTimeStamp.data.time_end;
								  g.bitmap=liveCaptcha.data.img;
								  g.isNeedRefresh=false;
								  g.isShowed=false;
								}catch(Exception e){

								}
							}
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
					  try{
						  Thread.sleep(5000);
						}catch(InterruptedException e){}
					}
				}
			}).start();

	  }


	@Override
	public int onStartCommand(final Intent intent,int flags,int startId){
		sendNotification();
		new Thread(new Runnable(){

			  @Override
			  public void run(){
				  MainActivity.instence.showToast("任务"+intent.getIntExtra("pos",0)+"开始");
				  GuajiJavaBean gua=new GuajiJavaBean();
				  LoginInfoPeople loginInfoPeople = MainActivity.instence.loginInfo.loginInfoPeople.get(intent.getIntExtra("pos",0));
				  gua.cookie=loginInfoPeople.cookie;
				  for(GuajiJavaBean g:guajijavabean){
					  if(g.cookie.equals(gua.cookie)){
						  MainActivity.instence.showToast("任务已添加");
						  return;
						}
					}
				  gua.referer=intent.getStringExtra("refer");
				  LiveTimeStamp liveTimeStamp = getLiveTimeStamp(gua.referer,gua.cookie);
				  LiveCaptcha liveCaptcha = getLiveCaptcha(gua.referer,gua.cookie);	
				  gua.timeStart=liveTimeStamp.data.time_start;
				  gua.timeEnd=liveTimeStamp.data.time_end;
				  gua.bitmap=liveCaptcha.data.img;
				  gua.id=m++;
				  guajijavabean.add(gua);
				}
			});

		return super.onStartCommand(intent,flags,startId);
	  }

	public void sendNotify(int iii){	

		builder.setContentTitle("title")  
		  .setContentText("content"+iii)
		  .setSmallIcon(R.drawable.ic_launcher); 	  
		Intent itt=new Intent(new Intent(GuaJiService.this,CaptchaDialogActivity.class));
		itt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		PendingIntent intent = PendingIntent.getActivity(GuaJiService.this,0,itt,0);
		builder.setContentIntent(intent);
		manager.notify(iii,builder.build());

	  }


	private void sendNotification(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        Intent notificationIntent = new Intent(this,CaptchaDialogActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);      
		builder.setContentTitle("通知标题"+m)//设置通知栏标题
		  .setContentIntent(pendingIntent) //设置通知栏点击意图
		  .setContentText("通知内容"+m)
		  .setTicker("通知内容") //通知首次出现在通知栏，带上升动画效果的
		  .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
		  .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
		Notification notification = builder.build();
        notification.flags|=Notification.FLAG_AUTO_CANCEL;
        if(notificationManager!=null){
            notificationManager.notify(m++,notification);
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

  }
