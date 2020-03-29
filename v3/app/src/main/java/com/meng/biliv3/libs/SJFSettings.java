package com.meng.biliv3.libs;

import android.content.*;

public class SJFSettings {
	private SharedPreferences sp;

	public SJFSettings(Context c) {
		sp = c.getSharedPreferences("settings", 0);
	}

	public String getVersion() {
		return sp.getString("newVersion", "0.0.0");
	}

	public void setVersion(String v) {
		putString("newVersion", v);
	}

	public long getMainAccount() {
		try {
			return sp.getLong("mainAccount", -1);
		} catch (ClassCastException e) {
			return Long.parseLong(sp.getString("mainAccount", null));
		}
	}

	public void setMainAccount(long ac) {
		putLong("mainAccount", ac);
	}

	public boolean getShowNotify() {
		return sp.getBoolean("notifi", false);
	}

	public void setShowNotify(boolean b) {
		putBoolean("notifi", b);
	}

	public void setOpenDrawer(boolean b) {
		putBoolean("opendraw", b);
	}

	public boolean getOpenDrawer() {
		return sp.getBoolean("opendraw", true);
	}

	public boolean getExit0() {
		return sp.getBoolean("exit", false);
	}

	public void setExit0(boolean b) {
		putBoolean("exit", b);
	}


	private void putLong(String key, long value) {
		SharedPreferences.Editor editor = sp.edit();
		editor.putLong(key, value);
		editor.apply();
	}

	private void putBoolean(String key, Boolean value) {
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.apply();
	}

	private void putString(String key, String value) {
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(key, value);
		editor.apply();
	}
}
