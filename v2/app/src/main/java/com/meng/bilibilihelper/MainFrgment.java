package com.meng.bilibilihelper;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;

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
	  }
  }
