package com.meng.bilibilihelper.fragment;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.javaBean.personInfo.*;

import java.util.concurrent.*;

public class PersonInfoFragment extends Fragment {
    public ExecutorService threadPool;
    public ListView listview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        threadPool = Executors.newFixedThreadPool(3);
        listview = (ListView) view.findViewById(R.id.normal_listview);
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                long l = ((PersonInfo) p1.getItemAtPosition(p3)).bid;
                MainActivity.instence.mainFrgment.mengEditText.checkUidButton();
                MainActivity.instence.mainFrgment.mengEditText.setText(String.valueOf(l));
                MainActivity.instence.initMainFragment(true);
            }
        });
    }
}
