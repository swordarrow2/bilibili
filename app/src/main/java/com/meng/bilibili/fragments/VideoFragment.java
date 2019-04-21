package com.meng.bilibili.fragments;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import com.meng.bilibili.*;
import com.meng.bilibili.javaBean.*;
import com.google.gson.*;

public class VideoFragment extends Fragment{

  LinearLayout ll;
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		return inflater.inflate(R.layout.text_fragment,container,false);
	  }

	@Override
	public void onViewCreated(View view,Bundle savedInstanceState){
		super.onViewCreated(view,savedInstanceState);
		ll=(LinearLayout)view.findViewById(R.id.text_fragmentLinearLayout);
		welcome();
	  }

	private void welcome(){
	  
	//	VideoInfoBean videoInfoBean = new Gson().fromJson(  MainActivity.getSourceCode("http://api.bilibili.com/archive_stat/stat?aid=" + id + "&type=jsonp"), VideoInfoBean.class);
		
	  
	    
	  }

  }
