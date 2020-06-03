package com.meng.sjfmd.fragment;

import android.app.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.github.clans.fab.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.sjfmd.adapters.*;
import com.meng.sjfmd.enums.*;
import com.meng.sjfmd.libs.*;
import com.meng.sjfmd.result.*;

public class AvFragment extends BaseIdFragment implements View.OnClickListener,View.OnLongClickListener {

	private Button preset;
	private ImageButton send;

	private FloatingActionMenu menuGroup;
	private FloatingActionButton fabZan;
    private FloatingActionButton fabCoin1;
	private FloatingActionButton fabCoin2;
    private FloatingActionButton fabFavorite;

	private EditText et;
	private TextView info;
	private Spinner selectAccount;
	private VideoInfo videoInfo;
	private ImageView ivPreview;
	private Bitmap preview;
	private LinearLayout llInput;
	//private ArrayList<DanmakuBean> danmakuList=null;

	public ExpandableListView judgeList;

	public AvFragment(IDType type, long id) {
		super(type, id);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.av_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
  		send = (ImageButton) view.findViewById(R.id.av_fragmentButton_send);
		preset = (Button) view.findViewById(R.id.av_fragmentButton_preset);
		llInput = (LinearLayout) view.findViewById(R.id.av_fragmentLinearLayout_input);
		llInput.setVisibility(View.GONE);
		llInput.setBackgroundColor(0xffffffff);
		menuGroup = (FloatingActionMenu) view.findViewById(R.id.av_float_menu);
		fabZan = (FloatingActionButton) view.findViewById(R.id.av_fragmentButton_zan);
		fabCoin1 = (FloatingActionButton) view.findViewById(R.id.av_fragmentButton_coin1);
		fabCoin2 = (FloatingActionButton) view.findViewById(R.id.av_fragmentButton_coin2);
		fabFavorite = (FloatingActionButton) view.findViewById(R.id.av_fragmentButton_favorite);
		et = (EditText) view.findViewById(R.id.av_fragmentEditText_msg);
		ivPreview = (ImageView) view.findViewById(R.id.av_fragmentImageView);  
		info = (TextView) view.findViewById(R.id.av_fragmentTextView_info);
		selectAccount = (Spinner) view.findViewById(R.id.av_fragmentSpinner);
		judgeList = (ExpandableListView) view.findViewById(R.id.av_fragment_ListView);
		judgeList.setGroupIndicator(null);
		preset.setOnClickListener(this);
		fabZan.setOnClickListener(this);
		fabCoin1.setOnClickListener(this);
		fabCoin2.setOnClickListener(this);
		fabFavorite.setOnClickListener(this);
		fabFavorite.setEnabled(false);
		send.setOnClickListener(this);
		selectAccount.setAdapter(spinnerAccountAdapter);
		ivPreview.setOnLongClickListener(this);
		final Animation animShow = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_slide_in_from_right);
		final Animation animHide = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_slide_out_to_right);
		menuGroup.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
				@Override
				public void onMenuToggle(boolean opened) {
					if (opened) {
						llInput.startAnimation(animShow);
						llInput.setVisibility(View.VISIBLE);
					} else {
						llInput.startAnimation(animHide);
						llInput.setVisibility(View.GONE);
					}
				}
			});
		MainActivity.instance.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					videoInfo = GSON.fromJson(Tools.Network.httpGet("http://api.bilibili.com/x/web-interface/view?aid=" + id), VideoInfo.class);	
					if (videoInfo.code != 0) {
						MainActivity.instance.showToast(videoInfo.message);
						return;
					}
					final VideoReply aj = Tools.BilibiliTool.getVideoJudge(id);
					getActivity().runOnUiThread(new Runnable(){

							@Override
							public void run() {
								info.setText(videoInfo.toString());
								if (aj != null && aj.data != null && aj.data.replies != null && aj.data.replies.size() > 0) {
									judgeList.setAdapter(new JudgeListAdapter(aj));
								}
								MainActivity.instance.renameFragment(type.toString() + id, videoInfo.data.title);
							}
						});
					byte[] img = NetworkCacher.getNetPicture(videoInfo.data.pic);
					if (img == null) {
						MainActivity.instance.showToast("封面图获取失败");
						return;
					}
					preview = BitmapFactory.decodeByteArray(img, 0, img.length);
					getActivity().runOnUiThread(new Runnable(){

							@Override
							public void run() {
								ivPreview.setImageBitmap(preview);
							}
						});
				}
			});
	}

	@Override
	public boolean onLongClick(View p1) {
		try {
			saveBitmap(type.toString() + id, preview);
			MainActivity.instance.showToast("图片已保存至" + MainActivity.instance.mainDic + type + id + ".png");
		} catch (Exception e) {
			MainActivity.instance.showToast("保存出错:" + e.toString());
		}
		return true;
	}

	@Override
	public void onClick(final View p1) {
		switch (p1.getId()) {
			case R.id.av_fragmentButton_preset:
				ListView naiSentenseListview = new ListView(getActivity());
				naiSentenseListview.setAdapter(sencencesAdapter);
				naiSentenseListview.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
							sendBili((String) selectAccount.getSelectedItem(), SendVideoJudge, (String)p1.getAdapter().getItem(p3));
						}
					});
				new AlertDialog.Builder(getActivity()).setView(naiSentenseListview).setTitle("选择预设语句").setNegativeButton("返回", null).show();
				break;
			case R.id.av_fragmentButton_send:
				sendBili((String) selectAccount.getSelectedItem(), SendVideoJudge, et.getText().toString());
				break;
			case R.id.av_fragmentButton_zan:
				sendBili((String) selectAccount.getSelectedItem(), LikeVideo, "");
				break;
			case R.id.av_fragmentButton_coin1:
				sendBili((String) selectAccount.getSelectedItem(), VideoCoin1, "");
				break;
			case R.id.av_fragmentButton_coin2:
				sendBili((String) selectAccount.getSelectedItem(), VideoCoin2, "");
				break;
			case R.id.av_fragmentButton_favorite:
				sendBili((String) selectAccount.getSelectedItem(), Favorite, "");
				break;
//			case R.id.av_fragment2_ButtonGetDanmakuSender:
//				p1.setVisibility(View.GONE);
//				MainActivity.instance.showToast("开始连接");
//				MainActivity.instance.threadPool.execute(new Runnable(){
//
//						@Override
//						public void run() {
//							boom2();
//						}
//					});
//				break;
		}
	}

//	private void boom2() {
//		try {
//			danmakuList = new ArrayList<>();
//			int cid=videoInfo.data.pages.get(Integer.parseInt(((String)part.getSelectedItem()).replace("page", "")) - 1).cid;
//			Connection.Response response = Jsoup.connect("http://comment.bilibili.com/" + cid + ".xml").ignoreContentType(true).execute();
//			FileOutputStream out = (new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/" + cid + ".xml")));
//			out.write(response.bodyAsBytes());           
//			out.close();
//			HashSet<Long> danmakuIdSet=new HashSet<>();
//			InputStream is=new FileInputStream(new File(Environment.getExternalStorageDirectory() + "/" + cid + ".xml"));
//			XmlPullParser xp = Xml.newPullParser();
//			xp.setInput(is, "utf-8");
//			int type = xp.getEventType();
//			while (type != XmlPullParser.END_DOCUMENT) {
//				if (type == XmlPullParser.START_TAG) {
//					if ("d".equals(xp.getName())) {  
//						long id = Long.parseLong(xp.getAttributeValue(0).split(",")[6], 16);  
//						danmakuIdSet.add(id);				
//						String[] d=xp.getAttributeValue(0).split(",");
//						DanmakuBean db=new DanmakuBean();
//						db.time = Float.parseFloat(d[0]);
//						db.mode = Integer.parseInt(d[1]);
//						db.fontSize = Integer.parseInt(d[2]);
//						db.color = Integer.parseInt(d[3]);
//						db.timeStamp = Long.parseLong(d[4]);
//						db.danmakuPool = Integer.parseInt(d[5]);
//						db.userHash = Long.parseLong(d[6], 16);
//						db.databaseId = Long.parseLong(d[7]);
//						db.msg = xp.nextText();
//						danmakuList.add(db);
//					}
//				}
//				type = xp.next();
//			}
//		} catch (Exception e) {
//			throw new RuntimeException(e.toString());
//		}
//	}
}
