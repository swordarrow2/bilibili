package com.meng.bilibilihelper.fragment.live;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.activity.live.*;

public class GiftFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView mainListview = (ListView) view.findViewById(R.id.normal_listview);
        mainListview.setAdapter(MainActivity.instance.mainAccountAdapter);
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
