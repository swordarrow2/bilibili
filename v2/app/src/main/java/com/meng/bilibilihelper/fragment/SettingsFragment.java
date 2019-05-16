package com.meng.bilibilihelper.fragment;

import android.graphics.BitmapFactory;
import android.os.*;
import android.preference.*;
import android.view.View;

import com.google.gson.Gson;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.InfoActivity;
import com.meng.bilibilihelper.activity.MainActivity;
import com.meng.bilibilihelper.javaBean.BilibiliMyInfo;
import com.meng.bilibilihelper.javaBean.BilibiliUserInfo;
import com.meng.bilibilihelper.javaBean.LoginInfoPeople;
import com.meng.bilibilihelper.javaBean.personInfo.PersonInfo;
import com.meng.bilibilihelper.libAndHelper.DownloadImageRunnable;
import com.meng.bilibilihelper.libAndHelper.HeadType;
import com.meng.bilibilihelper.libAndHelper.MengTextview;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

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
                File imf = new File(MainActivity.instence.mainDic + "bilibili/" + newValue + ".jpg");
                if (imf.exists()) {
                    MainActivity.instence.infoHeader.setImage(BitmapFactory.decodeFile(imf.getAbsolutePath()));
                } else {
                    MainActivity.instence.personInfoFragment.threadPool.execute(new DownloadImageRunnable(getActivity(), MainActivity.instence.infoHeader.getImageView(), (String) newValue, HeadType.BilibiliUser));
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final BilibiliUserInfo info = new Gson().fromJson(MainActivity.instence.getSourceCode("https://api.bilibili.com/x/space/acc/info?mid=" + newValue + "&jsonp=jsonp"), BilibiliUserInfo.class);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.instence.infoHeader.setTitle(info.data.name);
                                MainActivity.instence.infoHeader.setSummry("Lv."+info.data.level);
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
	private void deleteFiles(File folder){
		File[] fs=folder.listFiles();
		for(File f:fs){
			if(f.isDirectory()){
				deleteFiles(f);
				f.delete();
			  }else{
				f.delete();
			  }

		  }*/
    }
}
	
