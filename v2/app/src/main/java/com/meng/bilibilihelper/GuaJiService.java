package com.meng.bilibilihelper;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import com.google.gson.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.activity.live.*;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.libAndHelper.*;
import java.text.*;
import java.util.*;

public class GuaJiService extends Service {

    Notification.Builder builder;
    NotificationManager manager;
	
    @Override
    public IBinder onBind(Intent p1) {
        return null;
	  }

    int m = 0;
    public static ArrayList<LiveGuajiJavaBean> guajijavabean = new ArrayList<>();
    public static boolean using = false;

    @Override
    public void onCreate() {
        super.onCreate();
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new Notification.Builder(this);
        MainActivity.instance.threadPool.execute(new Runnable() {

			  @Override
			  public void run() {
				  while (true) {
					  using = true;
					  for (LiveGuajiJavaBean g : guajijavabean) {
						  try {
							  sendHeartBeat(g.isFirstHeartBeat, g.cookie);
							} catch (Exception e) {

							}
						  g.isFirstHeartBeat = false;
						}
					  using = false;
					  try {
						  Thread.sleep(30000);
						} catch (InterruptedException e) {
						}
					}
				}
			});
        MainActivity.instance.threadPool.execute(new Runnable() {

			  @Override
			  public void run() {
				  while (true) {
					  using = true;
					  for (LiveGuajiJavaBean g : guajijavabean) {
						  if (g.isNeedRefresh) {
							  try {
								  if (g.finish) {
									  continue;
									}
								  g.liveTimeStamp = getLiveTimeStamp(g.referer, g.cookie);
								  Log.e("iii|,", new Gson().toJson(g));
								  if (g.liveTimeStamp.code == -10017) {
									  g.finish = true;
									  sendRunningNotifi();
									  MainActivity.instance.showToast(g.liveTimeStamp.message);
									  if (!SharedPreferenceHelper.getBoolean("notifi", false)) {
										  sendEndNotification(g);
										}
									  continue;
									}
								  g.isNeedRefresh = false;
								  g.isShowed = false;
								  if (SharedPreferenceHelper.getBoolean("notifi", false)) {
									  sendRunningNotifi();
									} else {
									  sendStartNotification(g);
									}
								} catch (Exception e) {

								}
							}
						  if (System.currentTimeMillis() / 1000 > g.liveTimeStamp.data.time_end) {
							  if (g.isShowed || g.finish) continue;
							  try {
								  sendNotification(g);
								  g.isShowed = true;
								} catch (Exception e) {

								}
							}
						}
					  using = false;
					  try {
						  Thread.sleep(500);
						} catch (InterruptedException e) {
						}
					}
				}
			});

	  }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        MainActivity.instance.threadPool.execute(new Runnable() {

			  @Override
			  public void run() {
				  MainActivity.instance.showToast("任务开始");
				  AccountInfo ai=MainActivity.instance.loginAccounts.get(intent.getIntExtra("pos",-1));
				  LiveGuajiJavaBean gua = new LiveGuajiJavaBean(ai.name, "https://live.bilibili.com/"+new Random().nextInt(2500000), ai.cookie);
				  for (LiveGuajiJavaBean g : guajijavabean) {
					  if (g.cookie.equals(gua.cookie)) {
						  MainActivity.instance.showToast("任务已添加");
						  return;
						}
					}
				  gua.liveTimeStamp = getLiveTimeStamp(gua.referer, gua.cookie);
				  gua.id = m++;
				  while (using) {
					  try {
						  Thread.sleep(10);
						} catch (InterruptedException e) {
						  e.printStackTrace();
						}
					}
				  guajijavabean.add(gua);
				  if (SharedPreferenceHelper.getBoolean("notifi", false)) {
					  sendRunningNotifi();
					} else {
					  sendStartNotification(gua);
					}

				}
			});
        return super.onStartCommand(intent, flags, startId);
	  }

    private void sendStartNotification(LiveGuajiJavaBean g) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("该账号正在运行:" + g.name)//设置通知栏标题
		  .setContentText("下一次" + timeStampToDate(g.liveTimeStamp.data.time_end * 1000) + "  正在进行第" + g.liveTimeStamp.data.times + "波")
		  .setTicker("通知") //通知首次出现在通知栏，带上升动画效果的
		  .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
		  .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
        Notification notification = builder.build();
        if (notificationManager != null) {
            notificationManager.notify(g.id, notification);
		  }
	  }

    private void sendEndNotification(LiveGuajiJavaBean g) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("该账号已挂机完毕:" + g.name)//设置通知栏标题
		  .setContentText(g.name + "没有新的宝箱了")
		  .setTicker("通知") //通知首次出现在通知栏，带上升动画效果的
		  .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
		  .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
        Notification notification = builder.build();
        if (notificationManager != null) {
            notificationManager.notify(g.id, notification);
		  }
	  }

    private void sendRunningNotifi() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        StringBuilder sb = new StringBuilder("");
        for (LiveGuajiJavaBean gu : guajijavabean) {
            if (gu.finish) continue;
            sb.append(gu.name).append(" ");
		  }
        if (sb.toString().equals("")) {
            stopSelf();
            manager.cancel(-1);
            return;
		  }
        builder.setContentTitle("直播间挂机正在进行")//设置通知栏标题
		  .setContentText(sb.toString())
		  .setTicker("发发发") //通知首次出现在通知栏，带上升动画效果的
		  .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
		  .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        if (notificationManager != null) {
            notificationManager.notify(-1, notification);
		  }
	  }

    private void sendNotification(LiveGuajiJavaBean g) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        Intent notificationIntent = new Intent(this, CaptchaDialogActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra("data", new Gson().toJson(g));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, g.id, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentTitle(g.name + "验证")//设置通知栏标题
		  .setContentIntent(pendingIntent) //设置通知栏点击意图
		  .setContentText("为该账号输入验证码:" + g.name)
		  .setTicker("通知内容") //通知首次出现在通知栏，带上升动画效果的
		  .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
		  .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        if (notificationManager != null) {
            notificationManager.notify(g.id, notification);
		  }
	  }

    public LiveGuajiJavaBean.LiveTimeStamp getLiveTimeStamp(String refer, String cookie) {
        return new Gson().fromJson(
		  Tools.Network.getSourceCode(
			"https://api.live.bilibili.com/lottery/v1/SilverBox/getCurrentTask",
			cookie),
		  LiveGuajiJavaBean.LiveTimeStamp.class);
	  }

    public void sendHeartBeat(boolean isFirst, String cookie) {
        if (isFirst) {
			/*      return new Gson().fromJson(
			 Tools.Network.getSourceCode(
			 "https://api.live.bilibili.com/relation/v1/feed/heartBeat?_=" + System.currentTimeMillis()),
			 LiveGetAward.class);*/
            Tools.Network.getSourceCode("https://api.live.bilibili.com/relation/v1/feed/heartBeat?_=" + System.currentTimeMillis(), cookie);
		  } else {
			/*    return new Gson().fromJson(
			 Tools.Network.getSourceCode(
			 "https://api.live.bilibili.com/relation/v1/Feed/heartBeat"),
			 LiveGetAward.class);*/
            Tools.Network.getSourceCode("https://api.live.bilibili.com/relation/v1/Feed/heartBeat", cookie);
		  }
	  }

    public static String timeStampToDate(long tsp, String... format) {
        SimpleDateFormat sdf;
        if (format.length < 1) {
            sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
		  } else {
            sdf = new SimpleDateFormat(format[0], Locale.getDefault());
		  }
        return sdf.format(tsp);
	  }
  }
