package com.meng.bilibilihelper;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import java.io.*;
import java.net.*;

public class SignFragment extends Fragment{


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
								sendSignData(MainActivity.instence.loginInfoPeopleHashMap.get(p1.getItemAtPosition(p3)).cookie,MainActivity.instence.mainFrgment.editText.getText().toString());
							  }catch(Exception e){
								e.printStackTrace();
							  }
						  }
					  }).start();             
				}
			});	
	  }

    public void sendSignData(String cookie,String roomId) throws IOException{
        URL postUrl = new URL("https://api.live.bilibili.com/sign/doSign");
        HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
        connection.setDoOutput(false);
        connection.setDoInput(true);
        connection.setRequestMethod("GET");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Host","api.live.bilibili.com");
        connection.setRequestProperty("Connection","keep-alive");
        connection.setRequestProperty("Accept","application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty("Origin","https://live.bilibili.com");
        connection.setRequestProperty("User-Agent",MainActivity.instence.userAgent);
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty("Referer","https://live.bilibili.com/"+roomId);
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
