package com.meng.bilibilihelper.fragment.main;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.activity.main.*;
import com.meng.bilibilihelper.javaBean.*;

import android.view.View.OnClickListener;

public class ManagerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.account_manager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView list = (ListView) view.findViewById(R.id.account_managerListView);
        list.setAdapter(MainActivity.instence.loginInfoPeopleAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
					Intent intent = new Intent(getActivity(), InfoActivity.class);
					intent.putExtra("bid", String.valueOf(((LoginInfoPeople) parent.getItemAtPosition(position)).personInfo.data.mid));
					intent.putExtra("pos", position);
					intent.putExtra("cookie", String.valueOf(((LoginInfoPeople) parent.getItemAtPosition(position)).cookie));
					startActivity(intent);
				}
			});
        list.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(final AdapterView<?> p11, View p2, final int p3, long p4) {

					new AlertDialog.Builder(getActivity())
                        .setTitle("选择操作")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface p11, int p2) {
                                new AlertDialog.Builder(getActivity())
									.setTitle("确定删除吗")
									.setPositiveButton("确定", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface p1, int p2) {
											MainActivity.instence.loginInfo.loginInfoPeople.remove(p3);
											MainActivity.instence.arrayList.remove(p3);
											MainActivity.instence.saveConfig();
											MainActivity.instence.loginInfoPeopleAdapter.notifyDataSetChanged();
										}
									}).setNegativeButton("取消", null).show();
                            }
                        }).setNegativeButton("更新登录状态", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								startActivity(new Intent(getActivity(), Login.class));
							}
						}).show();

					return true;
				}
			});
		view.findViewById(R.id.account_managerButton).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1) {
					startActivity(new Intent(getActivity(), Login.class));
				}
			});
    }

}
