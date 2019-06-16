package com.meng.bilibilihelper.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.*;
import android.preference.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.MainActivity;
import com.meng.bilibilihelper.javaBean.BilibiliUserInfo;
import com.meng.bilibilihelper.javaBean.UserSpaceToLive;
import com.meng.bilibilihelper.libAndHelper.DownloadImageRunnable;
import com.meng.bilibilihelper.libAndHelper.HeadType;

import java.io.File;

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
                MainActivity.instence.mDrawerList.addHeaderView(MainActivity.instence.infoHeaderLeft);
                MainActivity.instence.rightList.addHeaderView(MainActivity.instence.infoHeaderRight);
                File imf = new File(MainActivity.instence.mainDic + "bilibili/" + newValue + ".jpg");
                if (imf.exists()) {
                    Bitmap b = BitmapFactory.decodeFile(imf.getAbsolutePath());
                    MainActivity.instence.infoHeaderLeft.setImage(b);
                    MainActivity.instence.infoHeaderRight.setImage(b);
                } else {
                    MainActivity.instence.personInfoFragment.threadPool.execute(new DownloadImageRunnable(getActivity(), MainActivity.instence.infoHeaderLeft.getImageView(), (String) newValue, HeadType.BilibiliUser));
                    MainActivity.instence.personInfoFragment.threadPool.execute(new DownloadImageRunnable(getActivity(), MainActivity.instence.infoHeaderRight.getImageView(), (String) newValue, HeadType.BilibiliUser));
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        final BilibiliUserInfo info = gson.fromJson(MainActivity.instence.getSourceCode("https://api.bilibili.com/x/space/acc/info?mid=" + newValue + "&jsonp=jsonp"), BilibiliUserInfo.class);
                        UserSpaceToLive sjb = gson.fromJson(MainActivity.instence.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + info.data.mid), UserSpaceToLive.class);
                        String json = MainActivity.instence.getSourceCode("https://api.live.bilibili.com/live_user/v1/UserInfo/get_anchor_in_room?roomid=" + sjb.data.roomid);
                        JsonParser parser = new JsonParser();
                        JsonObject obj = parser.parse(json).getAsJsonObject();
                        final JsonObject obj2 = obj.get("data").getAsJsonObject().get("level").getAsJsonObject().get("master_level").getAsJsonObject();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                JsonArray ja = obj2.get("next").getAsJsonArray();
                                MainActivity.instence.infoHeaderLeft.setTitle("主播 Lv." + obj2.get("level").getAsInt());
                                MainActivity.instence.infoHeaderLeft.setSummry(obj2.get("anchor_score").getAsInt() + "/" + ja.get(1));
                                MainActivity.instence.infoHeaderRight.setTitle(info.data.name);
                                MainActivity.instence.infoHeaderRight.setSummry("主站 Lv." + info.data.level);
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
	
