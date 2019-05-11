package com.meng.bilibilihelper.fragment;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.MainActivity;
import com.meng.bilibilihelper.javaBean.LoginInfoPeople;

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
								MainActivity.instence.showToast(MainActivity.instence.getSourceCode("https://www.bilibili.com",((LoginInfoPeople)(p1.getItemAtPosition(p3))).cookie));
							  }catch(Exception e){
								MainActivity.instence.showToast(e.toString());
								e.printStackTrace();
							  }
						  }
					  }).start();             
				}
			});	
	  }
  }
