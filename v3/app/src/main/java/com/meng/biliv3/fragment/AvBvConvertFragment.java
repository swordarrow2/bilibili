package com.meng.biliv3.fragment;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.libs.*;

public class AvBvConvertFragment extends Fragment implements View.OnClickListener {

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
		btnAv.setOnClickListener(this);
		btnBv.setOnClickListener(this);
    }

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
}
