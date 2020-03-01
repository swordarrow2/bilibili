package com.meng.biliv3.update;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import com.google.gson.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.libAndHelper.*;
import com.meng.biliv3.update.*;
import java.io.*;
import java.net.*;
import java.nio.*;
import org.java_websocket.client.*;
import org.java_websocket.handshake.*;
import com.meng.biliv3.fragment.*;

public class SanaeConnect extends WebSocketClient {

	private AvFragment.DanmakuBean afdb;
	public SanaeConnect() throws Exception {
		super(new URI("ws://123.207.65.93:9234"));
	}

	@Override
	public void onOpen(ServerHandshake p1) {

	}

	public void sendUpdate() {
		try {
			PackageInfo packageInfo = MainActivity.instance.getPackageManager().getPackageInfo(MainActivity.instance.getPackageName(), 0);
			CheckNewBean cnb=new CheckNewBean();
			cnb.packageName = packageInfo.packageName;
			cnb.nowVersionCode = packageInfo.versionCode;
			send(new Gson().toJson(cnb));
		} catch (PackageManager.NameNotFoundException e) {

		}
	}

	public void sendHash(AvFragment.DanmakuBean afdb) {
		this.afdb = afdb;
		BotDataPack toSend=BotDataPack.encode(BotDataPack.getIdFromHash);
		toSend.write((int)afdb.userHash);
		send(toSend.getData());
	}

	@Override
	public void onMessage(String p1) {
		if (!p1.equals("")) {
			final SoftwareInfo esi=new Gson().fromJson(p1, SoftwareInfo.class);
			if (!SharedPreferenceHelper.getValue("newVersion", "0.0.0").equals(esi.infoList.get(esi.infoList.size() - 1).versionName)) {	
				MainActivity.instance.runOnUiThread(new Runnable(){

						@Override
						public void run() {
							new AlertDialog.Builder(MainActivity.instance)
								.setTitle("发现新版本")
								.setMessage(esi.toString())
								.setPositiveButton("现在更新", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface p1, int p2) {
										send(BotDataPack.encode(BotDataPack.opGetApp).write(MainActivity.instance.getPackageName()).getData());
										MainActivity.instance.showToast("开始下载");
									}
								}).setNeutralButton("下次提醒我", null)
								.setNegativeButton("忽略本次更新", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										SharedPreferenceHelper.putValue("newVersion", esi.infoList.get(esi.infoList.size() - 1).versionName);
									}
								}).show();
						}
					});
			}
		}
	}

	@Override
	public void onMessage(ByteBuffer bytes) {
		BotDataPack bdp=BotDataPack.decode(bytes.array());
		if (bdp.getOpCode() == BotDataPack.opGetApp) {
			File f=new File(Environment.getExternalStorageDirectory() + "/download/" + MainActivity.instance.getPackageName() + ".apk");
			bdp.readFile(f);
			MainActivity.instance.showToast("文件已保存至" + f.getAbsolutePath());
		} else if (bdp.getOpCode() == BotDataPack.opTextNotify) {
			MainActivity.instance.showToast(bdp.readString());
		} else if (bdp.getOpCode() == BotDataPack.getIdFromHash) {
			afdb.uid = bdp.readInt();
		//	MainActivity.instance.showToast("获取到的:"+afdb.uid);
		}
		super.onMessage(bytes);
	}

	@Override
	public void onClose(int p1, String p2, boolean p3) {
		// TODO: Implement this method
	}

	@Override
	public void onError(Exception p1) {
		// TODO: Implement this method
	}
}
