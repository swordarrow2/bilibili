package com.meng.biliv3.fragment;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.activity.main.*;
import com.meng.biliv3.fragment.*;

import android.view.View.OnClickListener;
import com.meng.biliv3.libs.*;

public class AvBvConvertFragment extends Fragment {

	private Button btnAv,btnBv;
	private EditText etAv,etBv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.av_bv, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
		btnAv = (Button) view.findViewById(R.id.av_bvButtonav);
		btnBv = (Button) view.findViewById(R.id.av_bvButtonbv);
		etAv = (EditText) view.findViewById(R.id.av_bvEditTextav);
		etBv = (EditText) view.findViewById(R.id.av_bvEditTextbv);
		btnAv.setOnClickListener(onclick);
		btnBv.setOnClickListener(onclick);
    }

	private OnClickListener onclick=new OnClickListener(){

		@Override
		public void onClick(View p1) {
			switch (p1.getId()) {
				case R.id.av_bvButtonav:
					try {
						etBv.setText(AvBvConverter.encode(Long.parseLong(etAv.getText().toString())));
					} catch (Exception e) {
						MainActivity.instance.showToast("请输入数字");
					}
					break;
				case R.id.av_bvButtonbv:
					try {
						etAv.setText(String.valueOf(AvBvConverter.decode(etBv.getText().toString())));
					} catch (Exception e) {
						MainActivity.instance.showToast("请输入BV号");
					}
					break;
			}
		}

	};
}
