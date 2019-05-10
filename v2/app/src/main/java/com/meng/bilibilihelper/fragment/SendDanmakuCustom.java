package com.meng.bilibilihelper.fragment;
import android.app.*;
import android.os.*;
import android.view.*;
import com.meng.bilibilihelper.*;
import android.widget.*;
import com.meng.bilibilihelper.activity.*;

public class SendDanmakuCustom extends Fragment {
ListView listview;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.send_danmaku_custom, container, false);
	  }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		listview=(ListView) view.findViewById(R.id.send_danmaku_customListView);
		listview.setAdapter(new CustomDanmakuAdapter(MainActivity.instence,MainActivity.instence.loginInfo.loginInfoPeople));
	  }

  }
