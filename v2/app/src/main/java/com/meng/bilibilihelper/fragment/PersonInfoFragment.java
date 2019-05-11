package com.meng.bilibilihelper.fragment;
import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.meng.bilibilihelper.*;
import java.util.concurrent.*;
import com.meng.bilibilihelper.activity.*;
import android.widget.AdapterView.*;

import com.meng.bilibilihelper.javaBean.PlanePlayerList;

public class PersonInfoFragment extends Fragment {
	public ExecutorService threadPool;
	public ListView listview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list_fragment, container, false);
	  }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		threadPool=Executors.newFixedThreadPool(3);
		listview=(ListView)view.findViewById(R.id.list);
		listview.setOnItemClickListener(new OnItemClickListener(){

			  @Override
			  public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
				 long l= ((PlanePlayerList.PlanePlayer)p1.getItemAtPosition(p3)).bid;
				 MainActivity.instence.mainFrgment.radioButtonUID.setChecked(true);
				 MainActivity.instence.mainFrgment.autoCompleteTextView.setText(String.valueOf(l));
				 MainActivity.instence.initMainFragment(true);
				}
			});
	  }
  }
