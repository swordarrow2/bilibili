package com.meng.sjfmd.fragment;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.github.clans.fab.*;
import com.meng.sjfmd.*;
import com.meng.sjfmd.activity.*;
import com.meng.sjfmd.enums.*;
import com.meng.sjfmd.javabean.*;
import com.meng.sjfmd.libs.*;
import com.meng.sjfmd.result.*;
import com.meng.sjfmd.update.*;
import org.java_websocket.client.*;

import android.view.View.OnClickListener;

public class ManagerFragment extends Fragment {

	private AlertDialog selectOpDialog;

	private FloatingActionMenu menuGroup;
	private FloatingActionButton fabCookie;
    private FloatingActionButton fabLogin;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.account_manager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
		menuGroup = (FloatingActionMenu) view.findViewById(R.id.account_managerButton);
		menuGroup.setClosedOnTouchOutside(true);

        ListView list = (ListView) view.findViewById(R.id.account_managerListView);
        list.setAdapter(MainActivity.instance.mainAccountAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
					MainActivity.instance.showFragment(UidFragment.class, IDType.UID, MainActivity.instance.loginAccounts.get(position).uid);
				}
			});

		fabCookie = (FloatingActionButton) view.findViewById(R.id.account_manager_fab_cookie);
		fabLogin = (FloatingActionButton) view.findViewById(R.id.account_manager_fab_login);
		fabCookie.setOnClickListener(onClick);
		fabLogin.setOnClickListener(onClick);

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
																	public void action(WebSocketClient wsc) {
																		wsc.send(BotDataPack.encode(BotDataPack.cookie).write((int)aif.uid).write(aif.cookie).getData());
																	}
																});
															rc.connect();
														} else {
															rc.send(BotDataPack.encode(BotDataPack.cookie).write((int)aif.uid).write(aif.cookie).getData());
														}
														MainActivity.instance.showToast(aif.name + "的cookie已上传");
													} catch (Exception e) {
														MainActivity.instance.showToast(e.toString());
													}
												}
											});
										break;
									case "删除":
										new AlertDialog.Builder(getActivity()).setTitle("确定删除" + MainActivity.instance.loginAccounts.get(position).name + "吗").setPositiveButton("确定", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface p1, int p2) {
													MainActivity.instance.loginAccounts.remove(position);
													MainActivity.instance.saveConfig();
													MainActivity.instance.mainAccountAdapter.notifyDataSetChanged();
													BaseIdFragment.createSpinnerList();
												}
											}).setNegativeButton("取消", null).show();
										break;
								}
								MainActivity.instance.saveConfig();
								MainActivity.instance.mainAccountAdapter.notifyDataSetChanged();
								BaseIdFragment.createSpinnerList();
								selectOpDialog.cancel();
							}
						});
					selectOpDialog = new AlertDialog.Builder(getActivity()).setView(lvSelectOp).setTitle("选择操作").setNegativeButton("返回", null).show();
					return true;
				}
			});
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != 0x9961) {
			super.onActivityResult(requestCode, resultCode, data);
			return;
		}
		if (resultCode == Activity.RESULT_OK && data != null) {
			final AccountInfo aci=GSON.fromJson(data.getStringExtra("aci"), AccountInfo.class);
			MainActivity.instance.loginAccounts.add(aci);
			MainActivity.instance.saveConfig();
			MainActivity.instance.mainAccountAdapter.notifyDataSetChanged();
			BaseIdFragment.createSpinnerList();
		} else if (resultCode == Activity.RESULT_CANCELED) {
			MainActivity.instance.showToast("取消登录");
		} else if (data == null) {
			MainActivity.instance.showToast("一个错误导致无结果");
		}
	}

	OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
			menuGroup.close(true);
            switch (v.getId()) {
                case R.id.account_manager_fab_cookie:
					final EditText et = new EditText(getActivity());
					new AlertDialog.Builder(getActivity()).setTitle("输入cookie").setView(et)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface p1, int p2) {
								addByCookie(et.getText().toString());						
							}
						}).show();
                    break;
                case R.id.account_manager_fab_login:
					Intent i=new Intent(getActivity(), LoginActivity.class);
					startActivityForResult(i, 0x9961);
//					final AccountInfo aci=new AccountInfo();
//					final EditText et1=new EditText(getActivity());
//					new AlertDialog.Builder(getActivity()).setTitle("账号").setView(et1).setPositiveButton("确定", new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface p1, int p2) {
//								aci.phone = Long.parseLong(et1.getText().toString());
//								final EditText et2=new EditText(getActivity());
//								new AlertDialog.Builder(getActivity())
//									.setTitle("密码").setView(et2).setPositiveButton("确定", new DialogInterface.OnClickListener() {
//										@Override
//										public void onClick(DialogInterface p1, int p2) {
//											aci.password = et2.getText().toString();
//											MainActivity.instance.threadPool.execute(new Runnable(){
//
//													@Override
//													public void run() {
//														UserLoginApi ula=new UserLoginApi();
//														addByCookie(ula.Login(aci.phone + "", aci.password));
//													}
//												});
//										}
//									}).show();
//							}
//						}).show();
                    break;
            }
        }
    };

	private void addByCookie(final String cookie) {
		MainActivity.instance.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					MyInfo myInfo=Tools.BilibiliTool.getMyInfo(cookie);
					for (AccountInfo ai:MainActivity.instance.loginAccounts) {
						if (ai.uid == myInfo.data.mid) {
							MainActivity.instance.showToast("已添加过此帐号");
							return;
						}
					}
					AccountInfo accountInfo=new AccountInfo();
					accountInfo.cookie = cookie;
					accountInfo.name = myInfo.data.name;
					accountInfo.uid = myInfo.data.mid;
					MainActivity.instance.loginAccounts.add(accountInfo);
					MainActivity.instance.saveConfig();
					MainActivity.instance.runOnUiThread(new Runnable(){

							@Override
							public void run() {
								MainActivity.instance.mainAccountAdapter.notifyDataSetChanged();
								BaseIdFragment.createSpinnerList();
							}
						});
				}
			});
	}
}
