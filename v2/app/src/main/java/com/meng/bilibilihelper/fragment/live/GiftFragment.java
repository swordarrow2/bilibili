package com.meng.bilibilihelper.fragment.live;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.meng.bilibilihelper.R;
import com.meng.bilibilihelper.activity.live.GiftActivity;
import com.meng.bilibilihelper.activity.MainActivity;
import com.meng.bilibilihelper.fragment.BaseFrgment;

public class GiftFragment extends BaseFrgment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView mainListview = (ListView) view.findViewById(R.id.normal_listview);
        mainListview.setAdapter(MainActivity.instence.loginInfoPeopleAdapter);
        mainListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), GiftActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }
}
