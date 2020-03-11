package com.meng.biliv3.fragment;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.javaBean.*;
import com.meng.biliv3.libs.*;
import java.io.*;
import org.jsoup.*;

import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

public class CvFragment extends BaseIdFragment {

	private Button send,editPre,preset,zan,coin1,coin2,favorite;
	private EditText et;
	private TextView info;
	private Spinner selectAccount;
	private CvInfo cvInfo;
	private ImageView ivPreview;
	private Bitmap preview;

	public CvFragment(String type, long cvId) {
		this.type = type;
		id = cvId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.cv_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		send = (Button) view.findViewById(R.id.cv_fragmentButton_send);
		//editPre = (Button) view.findViewById(R.id.live_fragmentButton_edit_pre);
		preset = (Button) view.findViewById(R.id.cv_fragmentButton_preset);
		zan = (Button) view.findViewById(R.id.cv_fragmentButton_zan);
		coin1 = (Button) view.findViewById(R.id.cv_fragmentButton_coin1);
		coin2 = (Button) view.findViewById(R.id.cv_fragmentButton_coin2);
		favorite = (Button) view.findViewById(R.id.cv_fragmentButton_favorite);
		et = (EditText) view.findViewById(R.id.cv_fragmentEditText_msg);
		ivPreview = (ImageView) view.findViewById(R.id.cv_fragmentImageView);  
		info = (TextView) view.findViewById(R.id.cv_fragmentTextView_info);
		selectAccount = (Spinner) view.findViewById(R.id.cv_fragmentSpinner);
		preset.setOnClickListener(onclick);
		zan.setOnClickListener(onclick);
		coin1.setOnClickListener(onclick);
		coin2.setOnClickListener(onclick);
		favorite.setOnClickListener(onclick);
		send.setOnClickListener(onclick);
		//editPre.setOnClickListener(onclick);
		selectAccount.setAdapter(spinnerAccountAdapter);
		ivPreview.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View p1) {
					try {
						saveBitmap(type + id, preview);
						MainActivity.instance.showToast("图片已保存至" + MainActivity.instance.mainDic + type + id + ".png");
					} catch (Exception e) {}
					return true;
				}
			});
		MainActivity.instance.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					cvInfo = MainActivity.instance.gson.fromJson(Tools.BilibiliTool.getCvInfo(id), CvInfo.class);	
					if (cvInfo.code != 0) {
						MainActivity.instance.showToast(cvInfo.message);
						return;
					}
					getActivity().runOnUiThread(new Runnable(){

							@Override
							public void run() {
								info.setText(cvInfo.toString());
								MainActivity.instance.renameFragment(type + id, cvInfo.data.title);
							}
						});
					try {
						Connection.Response response = Jsoup.connect(cvInfo.data.banner_url).ignoreContentType(true).execute();
						byte[] img = response.bodyAsBytes();
						preview = BitmapFactory.decodeByteArray(img, 0, img.length);
						getActivity().runOnUiThread(new Runnable(){

								@Override
								public void run() {
									ivPreview.setImageBitmap(preview);
								}

							});
					} catch (IOException e) {
						throw new RuntimeException(e.toString());
					}
				}
			});
	}
	private void saveBitmap(String bitName, Bitmap mBitmap) throws Exception {
		File f = new File(Environment.getExternalStorageDirectory() + "/pictures/" + bitName + ".png");
		f.createNewFile();
		FileOutputStream fOut = null;
		fOut = new FileOutputStream(f);
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		fOut.flush();
		fOut.close();
		getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f)));
	}

	private OnClickListener onclick=new OnClickListener(){

		@Override
		public void onClick(final View p1) {
			switch (p1.getId()) {
				case R.id.cv_fragmentButton_preset:
					ListView naiSentenseListview = new ListView(getActivity());
					naiSentenseListview.setAdapter(sencencesAdapter);
					naiSentenseListview.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
								sendBili((String) selectAccount.getSelectedItem(), SendCvJudge, (String)p1.getAdapter().getItem(p3));
							}
						});
					new AlertDialog.Builder(getActivity()).setView(naiSentenseListview).setTitle("选择预设语句").setNegativeButton("返回", null).show();
					break;
				case R.id.cv_fragmentButton_send:
					sendBili((String) selectAccount.getSelectedItem(), SendCvJudge, et.getText().toString());
					break;
				case R.id.cv_fragmentButton_zan:
					sendBili((String) selectAccount.getSelectedItem(), LikeArtical, "");
					break;
				case R.id.cv_fragmentButton_coin1:
					sendBili((String) selectAccount.getSelectedItem(), CvCoin1, "");
					break;
				case R.id.cv_fragmentButton_coin2:
					sendBili((String) selectAccount.getSelectedItem(), CvCoin2, "");
					break;
				case R.id.cv_fragmentButton_favorite:
					sendBili((String) selectAccount.getSelectedItem(), Favorite, "");
					break;
			}
		}
	};
}
