package com.meng.bilibilihelper.fragment.live;

import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.adapters.*;
import com.meng.bilibilihelper.fragment.*;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.libAndHelper.*;

public class SendDanmakuFragment extends BaseFrgment {
    public ListView listview;
    public Button btn;
    public EditText et;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.send_danmaku, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listview = (ListView) view.findViewById(R.id.send_danmaku_listView);
        btn = (Button) view.findViewById(R.id.send_danmaku_button);
        et = (EditText) view.findViewById(R.id.send_danmaku_editText);

        listview.setAdapter(new ListWithImageSwitchAdapter(MainActivity.instence, MainActivity.instence.loginInfo.loginInfoPeople));
        btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View p1) {
					new Thread(new Runnable() {

							@Override
							public void run() {
								ListWithImageSwitchAdapter cda = (ListWithImageSwitchAdapter) listview.getAdapter();
								for (int i = 0; i < cda.getCount(); ++i) {
									if (cda.getChecked(i)) {
										Tools.BilibiliTool.sendLiveDanmaku(et.getText().toString(), ((LoginInfoPeople) cda.getItem(i)).cookie, MainActivity.instence.getFragment("Main",MainFragment.class).mengEditText.getLiveId());
									}
								}
							}
						}).start();
				}
			});
    }

}
