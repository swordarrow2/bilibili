package com.meng.biliv3.fragment;

import android.graphics.*;
import android.os.*;
import android.preference.*;
import com.google.gson.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.javaBean.*;
import com.meng.biliv3.libAndHelper.*;
import java.io.*;

public class SettingsFragment extends PreferenceFragment {

    Preference clean;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName("settings");
        addPreferencesFromResource(R.xml.preference);

        
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
	
