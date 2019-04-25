package com.meng.bilibilihelper;

import android.app.*;
import android.content.*;
import android.net.http.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.google.gson.*;
import com.meng.bilibili.javaBean.*;
import java.io.*;
import java.net.*;
import java.util.*;

import android.view.View.OnClickListener;

public class MainFrgment extends Fragment{

	public final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0";
       
    public EditText editText;
	
    public MainFrgment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.main_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        
		  
        Button btn2 = (Button)view.findViewById(R.id.btn2);
		Button btn3 = (Button) view.findViewById(R.id.btn3);
        
        editText=(EditText)view. findViewById(R.id.et);
             
        btn2.setOnClickListener(new OnClickListener() {

			  @Override
			  public void onClick(View p1){
				  new Thread(new Runnable() {

						@Override
						public void run(){
							for(LoginInfoPeople l :MainActivity.instence. loginInfoPeopleHashMap.values()){
								try{
									Thread.sleep(1000);
									MainActivity.instence. 	sendDanmakuData(MainActivity.instence. strings[new Random().nextInt(MainActivity.instence. strings.length)],l.cookie,editText.getText().toString());
								  }catch(Exception e){
									e.printStackTrace();
								  }
							  }
						  }
					  }).start();
				}
			});
		btn3.setOnClickListener(new OnClickListener() {

			  @Override
			  public void onClick(View p1){
				  new Thread(new Runnable() {

						@Override
						public void run(){
							for(LoginInfoPeople l :MainActivity.instence.  loginInfoPeopleHashMap.values()){
								try{
									Thread.sleep(1000);
									MainActivity.instence. 	sendSignData(l.cookie,editText.getText().toString());
								  }catch(Exception e){
									e.printStackTrace();
								  }
							  }
						  }
					  }).start();
				}
			});
    }

    

}
