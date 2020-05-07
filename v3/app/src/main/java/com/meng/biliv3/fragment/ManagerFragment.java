package com.meng.biliv3.fragment;

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
import com.meng.biliv3.javabean.*;
import com.meng.biliv3.update.*;
import org.java_websocket.client.*;

import android.view.View.OnClickListener;

public class ManagerFragment extends Fragment {

	private AlertDialog selectOpDialog;

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
					MainActivity.instance.showFragment(UidFragment.class, BaseIdFragment.typeUID, MainActivity.instance.loginAccounts.get(position).uid);
				}
			});
        list.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(final AdapterView<?> p11, View p2, final int position, long p4) {

					ListView lvSelectOp = new ListView(getActivity());
					lvSelectOp.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, new String[]{"设置账号", "上移", "下移", "更新登录状态","上传cookie", "删除"}));
					lvSelectOp.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> p1, View p2, int selectOp, long p4) {
								switch (((String) p1.getAdapter().getItem(selectOp))) {
									case "设置账号":
										final AccountInfo aci=MainActivity.instance.loginAccounts.get(position);
										final EditText et1=new EditText(getActivity());
										new AlertDialog.Builder(getActivity()).setTitle("账号").setView(et1).setPositiveButton("确定", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface p1, int p2) {
													aci.phone = Long.parseLong(et1.getText().toString());
													final EditText et2=new EditText(getActivity());
													new AlertDialog.Builder(getActivity())
														.setTitle("密码").setView(et2).setPositiveButton("确定", new DialogInterface.OnClickListener() {
															@Override
															public void onClick(DialogInterface p1, int p2) {
																aci.password = et2.getText().toString();
																MainActivity.instance.saveConfig();
															}
														}).show();
												}
											}).show();
										break;
									case "上移":
										if (position > 0) {
											MainActivity.instance.loginAccounts.add(position - 1, MainActivity.instance.loginAccounts.remove(position));
										} else {
											MainActivity.instance.showToast("已经是最上面了");
										}
										break;
									case "下移":
										if (position < MainActivity.instance.loginAccounts.size() - 1) {
											MainActivity.instance.loginAccounts.add(position + 1, MainActivity.instance.loginAccounts.remove(position));
										} else {
											MainActivity.instance.showToast("已经是最下面了");
										}
										break;
									case "更新登录状态":
										Intent inte=new Intent(getActivity(), Login.class);
										inte.putExtra("pos", position);
										startActivity(inte);
										break;
									case "上传cookie":
										final AccountInfo aif = MainActivity.instance.loginAccounts.get(position);
										MainActivity.instance.threadPool.execute(new Runnable(){

												@Override
												public void run() {
													try {
														RanConnect rc=RanConnect.getRanconnect();
														if (!rc.isOpen()) {
															rc.addOnOpenAction(new WebSocketOnOpenAction(){

																	@Override
																	public int useTimes() {
																		return 1;
																	}

																	@Override
																	public void action(WebSocketClient wsc) {
																		BotDataPack bdp=BotDataPack.encode(BotDataPack.cookie);
																		bdp.write((int)aif.uid).write(aif.cookie);
																		wsc.send(bdp.getData());
																	}
																});
															rc.connect();
														} else {
															BotDataPack bdp=BotDataPack.encode(BotDataPack.cookie);
															bdp.write((int)aif.uid).write(aif.cookie);
															rc.send(bdp.getData());
														}
													} catch (Exception e) {
														MainActivity.instance.showToast(e.toString());
													}
												}
											});
										MainActivity.instance.showToast(aif.name + "的cookie已上传");
										break;
									case "删除":
										new AlertDialog.Builder(getActivity()).setTitle("确定删除" + MainActivity.instance.loginAccounts.get(position).name + "吗").setPositiveButton("确定", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface p1, int p2) {
													MainActivity.instance.loginAccounts.remove(position);
												}
											}).setNegativeButton("取消", null).show();
										break;
								}
								MainActivity.instance.saveConfig();
								MainActivity.instance.mainAccountAdapter.notifyDataSetChanged();
								BaseIdFragment.createSpinnerList();
								selectOpDialog.cancel();
								selectOpDialog = null;
							}
						});
					selectOpDialog = new AlertDialog.Builder(getActivity()).setView(lvSelectOp).setTitle("选择操作").setNegativeButton("返回", null).show();
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
