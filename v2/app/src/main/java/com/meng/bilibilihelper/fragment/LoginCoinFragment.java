package com.meng.bilibilihelper.fragment;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.MainActivity;

import java.io.*;
import java.net.*;

public class LoginCoinFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.list_fragment,container,false);
	  }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        ListView l=(ListView)view.findViewById(R.id.list);
		l.setAdapter(MainActivity.instence.adapter);
		l.setOnItemClickListener(new OnItemClickListener(){

			  @Override
			  public void onItemClick(final AdapterView<?> p1,View p2,final int p3,long p4){
				  new Thread(new Runnable() {

						@Override
						public void run(){
							try{
								MainActivity.instence.showToast(MainActivity.instence.getSourceCode("https://www.bilibili.com",MainActivity.instence.loginInfoPeopleHashMap.get(p1.getItemAtPosition(p3)).cookie));
							  }catch(Exception e){
								MainActivity.instence.showToast(e.toString());
								e.printStackTrace();
							  }
						  }
					  }).start();             
				}
			});	
	  }

	  
    public void sendSignData(String cookie) throws IOException{
        URL postUrl = new URL("https://www.bilibili.com");
        HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
        connection.setDoOutput(false);
        connection.setDoInput(true);
        connection.setRequestMethod("GET");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);    
        connection.setRequestProperty("Accept","application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty("User-Agent",MainActivity.instence.userAgent);
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, br");
        connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
        connection.setRequestProperty("cookie",cookie);
        connection.connect();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder s = new StringBuilder();
        while((line=reader.readLine())!=null){
            s.append(line);
		  }
        final String ss = s.toString();
        reader.close();
        connection.disconnect();
        MainActivity.instence.showToast("结果"+ss);
	  }
  }
