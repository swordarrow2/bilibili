package com.meng.biliv3.fragment;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.javaBean.*;
import com.meng.biliv3.libs.*;
import java.io.*;
import java.util.*;

public class DynamicFragment extends Fragment implements View.OnClickListener {

	public TabHost tab;
	private Spinner selectAccount;
	private ArrayAdapter<String> spinnerAccountAdapter=null;
	private ArrayList<String> spList=null;
	private ArrayList<File> selectedPic=new ArrayList<>();
	private Button btnSelect,btnSend;
	private EditText et;
	private TextView selected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.tab_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tab = (TabHost) view.findViewById(android.R.id.tabhost);
		tab.setup();
        LayoutInflater layoutInflater=LayoutInflater.from(getActivity()); 
		//layoutInflater.inflate(R.layout.av_fragment, tab.getTabContentView()); 
		layoutInflater.inflate(R.layout.send_dynamic, tab.getTabContentView());
		//tab.addTab(tab.newTabSpec("tab1").setIndicator("浏览" , null).setContent(R.id.av_fragmentLinearLayout));
        tab.addTab(tab.newTabSpec("tab2").setIndicator("发送动态" , null).setContent(R.id.send_dynamicRelativeLayout));
		et = (EditText) view.findViewById(R.id.send_dynamicEditText);
		btnSelect = (Button) view.findViewById(R.id.send_dynamicButton_pic);
		btnSend = (Button) view.findViewById(R.id.send_dynamicButton_send);
		selected = (TextView) view.findViewById(R.id.send_dynamicTextViewSelectPic);
		btnSelect.setOnClickListener(this);
		btnSend.setOnClickListener(this);
		selectAccount = (Spinner) view.findViewById(R.id.send_dynamicSpinner_account);
		if (spList == null) {
			spList = new ArrayList<>();
		} else {
			spList.clear();
		}
		for (AccountInfo ai:MainActivity.instance.loginAccounts) {
			spList.add(ai.name);
		}
		spinnerAccountAdapter = new ArrayAdapter<String>(MainActivity.instance, android.R.layout.simple_list_item_1, spList);
		selectAccount.setAdapter(spinnerAccountAdapter);
	}

	@Override
	public void onClick(View p1) {
		switch (p1.getId()) {
			case R.id.send_dynamicButton_pic:
				Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("image/*");
				startActivityForResult(intent, 9961);
				break;
			case R.id.send_dynamicButton_send:
				MainActivity.instance.threadPool.execute(new Runnable(){

						@Override
						public void run() {
							String result;
							if (selectedPic.size() == 0) {
								result = Tools.BilibiliTool.sendDynamic(et.getText().toString(), MainActivity.instance.getAccount((String)selectAccount.getSelectedItem()).cookie);
							} else {
								result = Tools.BilibiliTool.sendDynamic(et.getText().toString(), MainActivity.instance.getAccount((String)selectAccount.getSelectedItem()).cookie, selectedPic);
							}
							MainActivity.instance.showToast(result);
//							if (new JsonParser().parse(result).getAsJsonObject().get("code").getAsInt() == 0) {
//								MainActivity.instance.showToast("发送成功");
//							} else {
//								MainActivity.instance.showToast("发送失败");
//							}
							MainActivity.instance.runOnUiThread(new Runnable(){

									@Override
									public void run() {
										et.setText("");
									}
								});
						}
					});
		}
	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 9961 && data.getData() != null) {
				String path = Tools.AndroidContent.absolutePathFromUri(getActivity().getApplicationContext(), data.getData());
				MainActivity.instance.showToast(path);
				selectedPic.add(new File(path));
				selected.setText(selected.getText().toString() + "已选择:" + path + "\n");
			} 
		} else if (resultCode == Activity.RESULT_CANCELED) {
			MainActivity.instance.showToast("取消选择图片");
		}
        super.onActivityResult(requestCode, resultCode, data);
    }
}