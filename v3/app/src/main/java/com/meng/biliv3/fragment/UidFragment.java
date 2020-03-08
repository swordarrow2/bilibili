package com.meng.biliv3.fragment;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.javaBean.*;
import com.meng.biliv3.libs.*;
import java.io.*;
import java.util.*;

public class UidFragment extends BaseIdFragment {

	private ListView lv;
	private ArrayAdapter<String> adapter;
	private ArrayList<String> dataSet=new ArrayList<>();

	public UidFragment(String type, long uid) {
		id = uid;
		this.type = type;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		lv = new ListView(getActivity());
		return lv;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, dataSet);
		lv.setAdapter(adapter);
		File imf = new File(MainActivity.instance.mainDic + "bilibili/" + id + ".jpg");
        ImageView iv=new ImageView(getActivity());
		if (imf.exists()) {
            iv.setImageBitmap(BitmapFactory.decodeFile(imf.getAbsolutePath()));
        } else {
            MainActivity.instance.threadPool.execute(new DownloadImageRunnable(iv, id, DownloadImageRunnable.BilibiliUser));
        }
		lv.addHeaderView(iv);
		lv.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
					Tools.AndroidContent.copyToClipboard((String)p1.getItemAtPosition(p3));
				}
			});
		MainActivity.instance.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					try {
						if (containsID(id)) {
							final BilibiliMyInfo info = MainActivity.instance.gson.fromJson(Tools.BilibiliTool.getMyInfo(MainActivity.instance.getAccount(id).cookie), BilibiliMyInfo.class);
							addData("ID", info.data.mid);
							addData("用户名", info.data.name);
							addData("性别", info.data.sex);
							addData("签名", info.data.sign);
							addData("等级", info.data.level);
							addData("经验", info.data.level_exp.current_exp + "/" + info.data.level_exp.next_exp);
							addData("注册时间", Tools.Time.getTime(info.data.jointime * 1000));
							addData("节操", info.data.moral);
							addData("绑定邮箱", info.data.email_status == 1 ? "是" : "否");
							addData("绑定手机", info.data.tel_status == 1 ? "是" : "否");
							addData("硬币", info.data.coins);
							addData("vip类型", info.data.vip.type);
							addData("vip状态", info.data.vip.status);
							getActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										MainActivity.instance.renameFragment(typeUID + id, info.data.name);
										adapter.notifyDataSetChanged();				
									}
								});
						} else {
							final BilibiliUserInfo info = MainActivity.instance.gson.fromJson(Tools.BilibiliTool.getUserInfo(id), BilibiliUserInfo.class);
							addData("UID", info.data.mid);
							addData("用户名", info.data.name);
							addData("性别", info.data.sex);
							addData("签名", info.data.sign);
							addData("等级", info.data.level);
							addData("生日", info.data.birthday);
							//addData("硬币", person.data.coins);
							addData("vip类型", info.data.vip.type);
							addData("vip状态", info.data.vip.status);
							getActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										MainActivity.instance.renameFragment(typeUID + id, info.data.name);
										adapter.notifyDataSetChanged();				
									}
								});
						}
						UidToLiveRoom sjb = MainActivity.instance.gson.fromJson(Tools.BilibiliTool.getLiveRoomInfo(id), UidToLiveRoom.class);
						addData("直播URL", sjb.data.url);
						addData("标题", sjb.data.title);
						addData("状态", sjb.data.liveStatus == 1 ? "正在直播" : "未直播");
						addData("房间号", sjb.data.roomid);
						notifyList();
						Relation r = MainActivity.instance.gson.fromJson(Tools.BilibiliTool.getRelation(id), Relation.class);
						addData("粉丝", r.data.follower);
						addData("关注", r.data.following);
						//addData("黑名单", r.data.black);
						notifyList();
						Upstat u = MainActivity.instance.gson.fromJson(Tools.BilibiliTool.getUpstat(id), Upstat.class);
						addData("播放量", u.data.archive.view);
						addData("阅读量", u.data.article.view);
						notifyList();
						if (containsID(id)) {
							addData("cookie", MainActivity.instance.getAccount(id).cookie);
							notifyList();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				private void notifyList() {
					getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								adapter.notifyDataSetChanged();
							}
						});
				}
			});
	}

	private void addData(String title, Object content) {
		dataSet.add(title + ":" + content);
	}

	private boolean containsID(long uid) {
		for (AccountInfo ai:MainActivity.instance.loginAccounts) {
			if (ai.uid == uid) {
				return true;
			}
		}
		return false;
	}
}
