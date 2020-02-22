package com.meng.bilibilihelper.fragment;

import android.graphics.*;
import android.os.*;
import android.preference.*;
import com.google.gson.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.libAndHelper.*;
import java.io.*;

public class SettingsFragment extends PreferenceFragment {

    Preference clean;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName("settings");
        addPreferencesFromResource(R.xml.preference);

        EditTextPreference editTextPreference = (EditTextPreference) findPreference("mainAccount");
        editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, final Object newValue) {
					MainActivity.instance.mDrawerList.addHeaderView(MainActivity.instance.infoHeaderLeft);
					File imf = new File(MainActivity.instance.mainDic + "bilibili/" + newValue + ".jpg");
					if (imf.exists()) {
						Bitmap b = BitmapFactory.decodeFile(imf.getAbsolutePath());
						MainActivity.instance.infoHeaderLeft.setImage(b);
					} else {
						MainActivity.instance.getFragment("人员信息",PersonInfoFragment.class).threadPool.execute(new DownloadImageRunnable(getActivity(), MainActivity.instance.infoHeaderLeft.getImageView(), (String) newValue, DownloadImageRunnable.BilibiliUser));
					}
					new Thread(new Runnable() {
							@Override
							public void run() {
								Gson gson = new Gson();
								final BilibiliUserInfo info = gson.fromJson(Tools.Network.getSourceCode("https://api.bilibili.com/x/space/acc/info?mid=" + newValue + "&jsonp=jsonp"), BilibiliUserInfo.class);
								UserSpaceToLive sjb = gson.fromJson(Tools.Network.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + info.data.mid), UserSpaceToLive.class);
								String json = Tools.Network.getSourceCode("https://api.live.bilibili.com/live_user/v1/UserInfo/get_anchor_in_room?roomid=" + sjb.data.roomid);
								JsonParser parser = new JsonParser();
								JsonObject obj = parser.parse(json).getAsJsonObject();
								final JsonObject obj2 = obj.get("data").getAsJsonObject().get("level").getAsJsonObject().get("master_level").getAsJsonObject();
								getActivity().runOnUiThread(new Runnable() {
										@Override
										public void run() {
											JsonArray ja = obj2.get("next").getAsJsonArray();
											MainActivity.instance.infoHeaderLeft.setTitle(info.data.name);
											MainActivity.instance.infoHeaderLeft.setSummry("主站 Lv." + info.data.level + "\n主播 Lv." + obj2.get("level").getAsInt() + "\n" + obj2.get("anchor_score").getAsInt() + "/" + ja.get(1));
										}
									});
							}
						}).start();
					return true;
				}
			});
		/*	CheckBoxPreference cb=(CheckBoxPreference)findPreference("useLightTheme");
		 cb.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
		 @Override
		 public boolean onPreferenceChange(Preference preference,Object newValue){
		 //     Intent i = new Intent(getActivity().getApplicationContext(), PixivDownloadMain.class);
		 //     i.putExtra("setTheme", true);
		 //     getActivity().startActivity(i);
		 getActivity().startActivity(new Intent(getActivity().getApplicationContext(),MainActivity.class).putExtra("setTheme",true));
		 getActivity().finish();
		 getActivity().overridePendingTransition(0,0);
		 return true;
		 }
		 });
		 clean=findPreference(Data.preferenceKeys.cleanTmpFilesNow);
		 clean.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
		 @Override
		 public boolean onPreferenceClick(Preference preference){
		 File frameFileFolder = new File(Environment.getExternalStorageDirectory().getPath()+File.separator+"Pictures/picTool/tmp");
		 deleteFiles(frameFileFolder);
		 return true;
		 }
		 });
		 }
		 }*/

    }
}
	
