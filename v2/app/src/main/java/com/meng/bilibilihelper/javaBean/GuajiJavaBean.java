package com.meng.bilibilihelper.javaBean;
import com.meng.bilibilihelper.javaBean.liveTimeStamp.*;
import com.meng.bilibilihelper.javaBean.liveCaptcha.*;

public class GuajiJavaBean{
	public boolean finish=false;
	public String name="";
	public int id=0;
	public boolean isNeedRefresh=false;
	public boolean isShowed=false;
	public String cookie="";
	public LiveTimeStamp liveTimeStamp=new LiveTimeStamp();
	public LiveCaptcha liveCaptcha = new LiveCaptcha();
	public String referer="";
	public boolean isFirstHeartBeat=true;

	public GuajiJavaBean(String name,String referer,String cookie){
		this.name=name;
		this.referer=referer;
		this.cookie=cookie;
	  }
  }
