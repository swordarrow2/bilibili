package com.meng.bilibili.fragments;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import com.meng.bilibili.*;

public class SettingsFragment extends PreferenceFragment {

	public void onKeyDown(int keyCode,KeyEvent event){
		// TODO: Implement this method
	  }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName("main");
        addPreferencesFromResource(R.xml.preference);
        CheckBoxPreference cb=(CheckBoxPreference)findPreference("useLightTheme");
        cb.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
            @Override
            public boolean onPreferenceChange(Preference preference,Object newValue){
                //     Intent i = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                //     i.putExtra("setTheme", true);
                //     getActivity().startActivity(i);
                getActivity().startActivity(new Intent(getActivity().getApplicationContext(),MainActivity.class).putExtra("setTheme",true));
                getActivity().finish();
                getActivity().overridePendingTransition(0,0);
                return true;
            }
        });
    }

}
