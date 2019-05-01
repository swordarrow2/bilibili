package com.meng.bilibilihelper.fragment;

import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.javaBean.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class MainFrgment extends Fragment{

    public EditText editText;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.main_fragment,container,false);
	  }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
		editText=(EditText)view.findViewById(R.id.et);

		Button btn = (Button) view.findViewById(R.id.naiAll);
        Button btn2 = (Button)view.findViewById(R.id.signAll);

		btn.setOnClickListener(new OnClickListener() {

			  @Override
			  public void onClick(View p1){
				  new Thread(new Runnable() {

						@Override
						public void run(){
							for(LoginInfoPeople l :MainActivity.instence.loginInfoPeopleHashMap.values()){
								try{
									Thread.sleep(1000);
									String co="";
									if(editText.getText().toString().equals("")){
									  co=editText.getHint().toString();
									}else{
									  co=editText.getText().toString();
									}
									MainActivity.instence.naiFragment.sendDanmakuData(
									  MainActivity.instence.naiFragment.cs.sent.get(
										new Random().nextInt(
										  MainActivity.instence.naiFragment.cs.sent.size()
										)),
									  l.cookie,
									  co
									);
								  }catch(Exception e){
									e.printStackTrace();
								  }
							  }
						  }
					  }).start();
				}
			});

		btn2.setOnClickListener(new OnClickListener() {

			  @Override
			  public void onClick(View p1){
				  new Thread(new Runnable() {

						@Override
						public void run(){
							for(LoginInfoPeople l : MainActivity.instence.loginInfoPeopleHashMap.values()){
								try{
									Thread.sleep(1000);
									String co="";
									if(editText.getText().toString().equals("")){
										co=editText.getHint().toString();
									  }else{
										co=editText.getText().toString();
									  }
									MainActivity.instence.signFragment.sendSignData(
									  l.cookie,co
									);
								  }catch(Exception e){
									e.printStackTrace();
								  }
							  }
						  }
					  }).start();
				}
			});
	  }
	  
	public String readStringFromNetwork(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            InputStream in = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
			  }
            return sb.toString();
		  } catch (Exception e) {
            return "{}";
		  }
	  }
	
  }
