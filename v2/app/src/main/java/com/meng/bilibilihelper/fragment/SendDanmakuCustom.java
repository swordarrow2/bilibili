package com.meng.bilibilihelper.fragment;
import android.app.*;
import android.os.*;
import android.view.*;
import com.meng.bilibilihelper.*;
import android.widget.*;
import com.meng.bilibilihelper.activity.*;
import android.view.View.*;
import java.util.*;
import com.meng.bilibilihelper.javaBean.*;
import java.io.*;

public class SendDanmakuCustom extends Fragment {
	ListView listview;
	Button btn;
	EditText et;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.send_danmaku_custom, container, false);
	  }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		listview = (ListView) view.findViewById(R.id.send_danmaku_customListView);
		btn = (Button)view.findViewById(R.id.send_danmaku_customButton);
		et = (EditText)view.findViewById(R.id.send_danmaku_customEditText);

		listview.setAdapter(new CustomDanmakuAdapter(MainActivity.instence, MainActivity.instence.loginInfo.loginInfoPeople));
		btn.setOnClickListener(new OnClickListener(){

			  @Override
			  public void onClick(View p1) {
				  new Thread(new Runnable(){

						@Override
						public void run() {
							CustomDanmakuAdapter cda=(CustomDanmakuAdapter) listview.getAdapter();
						
							for (int i=0;i < cda.getCount();++i) {
								if (cda.getChecked(i)) {
									try {
										MainActivity.instence.naiFragment.sendDanmakuData(et.getText().toString(), ((LoginInfoPeople)cda.getItem(i)).cookie, MainActivity.instence.naiFragment.getLiveId());
									  } catch (IOException e) {

									  }
								  }
							  }
						  }
					  }).start();
				}
			});
	  }

  }
