package com.meng.bilibilihelper.fragment.live;

import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.fragment.*;
import com.meng.bilibilihelper.javaBean.*;

public class GuaJiFragment extends BaseFrgment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView l = (ListView) view.findViewById(R.id.normal_listview);
        l.setAdapter(MainActivity.instence.loginInfoPeopleAdapter);
        l.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> p1, View p2, final int p3, long p4) {
                Intent intentOne = new Intent(getActivity(), GuaJiService.class);
				intentOne.putExtra("name",((LoginInfoPeople)p1.getItemAtPosition(p3)).personInfo.data.name);
                intentOne.putExtra("cookie", MainActivity.instence.loginInfo.loginInfoPeople.get(p3).cookie);
				intentOne.putExtra("refer","https://live.bilibili.com/"+MainActivity.instence.getFragment("Main",MainFragment.class).mengEditText.getLiveId());
                getActivity().startService(intentOne);
            }
        });
    }
}
