package com.meng.sjfmd.tasks;

import com.google.gson.*;
import com.meng.biliv3.activity.*;
import com.meng.sjfmd.javabean.*;
import com.meng.sjfmd.libs.*;

public class AutoSign implements Runnable {

	@Override
	public void run() {
		StringBuilder sb = new StringBuilder();
		int succesd=0;
		int failed=0;
		int noAction=0;
		for (AccountInfo ai:MainActivity.instance.loginAccounts) {
			if (!TimeFormater.getDate().equals(TimeFormater.getDate(ai.lastSign))) {
				ai.setSigned(false);
				++noAction;
			}
			if (!ai.isSigned() && !ai.isCookieExceed()) {
				int rc =new JsonParser().parse(Bilibili.sendLiveSign(ai.cookie)).getAsJsonObject().get("code").getAsInt();
				ai.lastSign = System.currentTimeMillis();
				switch (rc) {
					case -101:
						ai.setCookieExceed(true);
						sb.append("\n").append(ai.name).append(":cookie过期");
						++failed;
						break;
					case 0:
						ai.lastSign = System.currentTimeMillis();
						sb.append("\n").append(ai.name).append(":成功");
						ai.setSigned(true);
						++succesd;
						break;
					case 1011040:
						sb.append("\n").append(ai.name).append(":已在其他设备签到");
						ai.setSigned(true);
						break;
					default:
						++noAction;
						break;
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {}
			} else if (ai.isSigned()) {
				sb.append("\n").append(ai.name).append(":今日已签到");
				++noAction;
			} else if (ai.isCookieExceed()) {
				sb.append("\n").append(ai.name).append(":cookie过期");
				++failed;
			} else {
				sb.append("\n").append(ai.name).append(":未知错误");
				++failed;
			}
		}
		MainActivity.instance.showToast(String.format("签到成功%d个,失败%d个,无动作%d个", succesd, failed, noAction), sb.toString());
		MainActivity.instance.saveConfig();
	}
}
