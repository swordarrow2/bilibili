package com.meng.bilibilihelper.activity;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.google.gson.*;
import com.meng.bilibilihelper.DownloadImageThread;
import com.meng.bilibilihelper.MengNetworkTextview;
import com.meng.bilibilihelper.R;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.javaBean.relation.*;
import com.meng.bilibilihelper.javaBean.spaceToLive.*;
import com.meng.bilibilihelper.javaBean.upstat.*;
import com.meng.bilibilihelper.javaBean.user.*;
import java.io.*;
import java.net.*;
import android.view.View.*;

public class InfoActivity extends Activity{
	public ProgressBar progressBar;
	public static String mainDic = "";

	private LinearLayout l1;
	private Context c;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		final Intent intent=getIntent();
		if(intent.getStringExtra("bid")==null){
			finish();
		  }	
		mainDic=Environment.getExternalStorageDirectory()+"/Pictures/grzx/";

		setContentView(R.layout.info_list);
		c=this;
		progressBar=(ProgressBar) findViewById(R.id.info_listProgressBar1);	
		l1=(LinearLayout)findViewById(R.id.info_listLinearLayout_MengNetworkTextview);
		final ImageView im=new ImageView(this);
		im.setOnClickListener(new OnClickListener(){

			  @Override
			  public void onClick(View p1){
				  File imf=new File(mainDic+"bilibili/"+intent.getStringExtra("bid")+".jpg");	
				  imf.delete();
				  new DownloadImageThread(InfoActivity.this,im,intent.getStringExtra("bid")).start();
				}
			});

		File imf=new File(mainDic+"bilibili/"+intent.getStringExtra("bid")+".jpg");
		if(imf.exists()){
			im.setImageBitmap(BitmapFactory.decodeFile(imf.getAbsolutePath()));
		  }else{
			new DownloadImageThread(this,im,intent.getStringExtra("bid")).start();
		  }
		l1.addView(im);

		getBilibiliUserInfo(intent.getStringExtra("bid"));
	  }

	private String readHttpString(String urlstr) throws MalformedURLException, IOException{
		URL url = new URL(urlstr);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		String line;
		StringBuilder stringBuilder = new StringBuilder();
		while((line=reader.readLine())!=null){
			stringBuilder.append(line);
		  }
		return stringBuilder.toString();
	  }

	private void getBilibiliUserInfo(final String uid){
		new Thread(new Runnable(){

			  @Override
			  public void run(){
				  Gson gson=new Gson();
				  try{				  
					  final BilibiliPersonInfo person = gson.fromJson(readHttpString("https://api.bilibili.com/x/space/acc/info?mid="+uid+"&jsonp=jsonp"),BilibiliPersonInfo.class);
					  runOnUiThread(new Runnable(){
							@Override
							public void run(){
								progressBar.setVisibility(View.GONE);
								l1.addView(new MengNetworkTextview(c,"ID",person.data.mid));
								l1.addView(new MengNetworkTextview(c,"用户名",person.data.name));
								l1.addView(new MengNetworkTextview(c,"性别",person.data.sex));
								l1.addView(new MengNetworkTextview(c,"签名",person.data.sign));
								l1.addView(new MengNetworkTextview(c,"等级",person.data.level));
								l1.addView(new MengNetworkTextview(c,"生日",person.data.birthday));										
								l1.addView(new MengNetworkTextview(c,"硬币",person.data.coins));
								l1.addView(new MengNetworkTextview(c,"vip类型",person.data.vip.type));
								l1.addView(new MengNetworkTextview(c,"vip状态",person.data.vip.status));	

								int ii=InfoActivity.this.getIntent().getIntExtra("pos",0);
								if(!MainActivity.instence.loginInfo.loginInfoPeople.get(ii).personInfo.data.name.equals(person.data.name)){
									MainActivity.instence.loginInfo.loginInfoPeople.get(ii).personInfo.data.name=person.data.name;

									runOnUiThread(new Runnable() {

										  @Override
										  public void run(){
											  MainActivity.instence.loginInfoPeopleHashMap.clear();
											  MainActivity.instence.arrayList.clear();
											  for(LoginInfoPeople loginInfoPeople : MainActivity.instence.loginInfo.loginInfoPeople){
												  MainActivity.instence.loginInfoPeopleHashMap.put(loginInfoPeople.personInfo.data.name,loginInfoPeople);
												  MainActivity.instence.arrayList.add(loginInfoPeople.personInfo.data.name);
												}											
											  MainActivity.instence.adapter.notifyDataSetChanged();
											  MainActivity.instence.saveConfig();
											}
										});
								  } 		
							  }
						  });				
					  final SpaceToLiveJavaBean sjb = gson.fromJson(readHttpString("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid="+uid),SpaceToLiveJavaBean.class);
					  runOnUiThread(new Runnable(){
							@Override
							public void run(){
								l1.addView(new MengNetworkTextview(c,"直播URL",sjb.data.url));
								l1.addView(new MengNetworkTextview(c,"标题",sjb.data.title));
								l1.addView(new MengNetworkTextview(c,"状态",sjb.data.liveStatus==1?"正在直播":"未直播"));
								l1.addView(new MengNetworkTextview(c,"房间号",sjb.data.roomid));							
							  }
						  });			
					  final Relation r=gson.fromJson(readHttpString("https://api.bilibili.com/x/relation/stat?vmid="+uid+"&jsonp=jsonp"),Relation.class);
					  runOnUiThread(new Runnable(){
							@Override
							public void run(){
								l1.addView(new MengNetworkTextview(c,"粉丝",r.data.follower));
								l1.addView(new MengNetworkTextview(c,"关注",r.data.following));												
							  }
						  });			
					  final Upstat u=gson.fromJson(readHttpString("https://api.bilibili.com/x/space/upstat?mid="+uid+"&jsonp=jsonp"),Upstat.class);
					  runOnUiThread(new Runnable(){
							@Override
							public void run(){
								l1.addView(new MengNetworkTextview(c,"播放量",u.data.archive.view));
								l1.addView(new MengNetworkTextview(c,"阅读量",u.data.article.view));											
							  }
						  });			
					}catch(Exception e){
					  e.printStackTrace();
					}
				}
			}).start();
	  }
  }
