package com.meng.biliv3.fragment.main;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.activity.main.*;
import com.meng.biliv3.javaBean.*;

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
        list.setAdapter(MainActivity.instance.mainAccountAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
					Intent intent = new Intent(getActivity(), InfoActivity.class);
					intent.putExtra("bid", String.valueOf(MainActivity.instance.loginAccounts.get(position).uid));
					intent.putExtra("pos", position);
					intent.putExtra("cookie", MainActivity.instance.loginAccounts.get(position).cookie);
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
											MainActivity.instance.loginAccounts.remove(p3);
											MainActivity.instance.saveConfig();
											MainActivity.instance.mainAccountAdapter.notifyDataSetChanged();
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
