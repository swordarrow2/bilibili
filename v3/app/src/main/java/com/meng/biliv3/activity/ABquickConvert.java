package com.meng.biliv3.activity;

import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;
import com.meng.biliv3.libs.*;
import java.util.regex.*;

public class ABquickConvert extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CharSequence text = getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
		// boolean readonly = getIntent().getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false);
		String s=null;
		if (text == null) {
			finish();
		}
		s = text.toString();
		long avid=getAVId(s);
		if (avid != -1) {
			ClipData clipData = ClipData.newPlainText("text", AvBvConverter.encode(avid));
			((ClipboardManager)this.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(clipData);
			Toast.makeText(this, "已复制" + AvBvConverter.encode(avid) + "到剪贴板", Toast.LENGTH_SHORT).show();
			finish();
		}
		String bvid=getBVId(s);
		if (bvid != null) {
			ClipData clipData = ClipData.newPlainText("text", "av" + AvBvConverter.decode(bvid));
			((ClipboardManager)this.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(clipData);
			Toast.makeText(this, "已复制av" + AvBvConverter.decode(bvid) + "到剪贴板", Toast.LENGTH_SHORT).show();
			finish();
		}
		Toast.makeText(this, "没有发现有效的链接", Toast.LENGTH_SHORT);
	}

	private long getAVId(String s) {  
		Matcher m2 = Pattern.compile("[Aa][Vv](\\d{1,})\\D{0,}").matcher(s);  
		if (m2.find()) {  
			return Long.parseLong(m2.group(1));
		}
		return -1;
	}
	private String getBVId(String s) {  
		Matcher m2 = Pattern.compile("\\D{0,}([Bb][Vv]1.{2}4.{1}1.{1}7.{2})\\D{0,}").matcher(s);  
		if (m2.find()) {  
			return m2.group(1);
		}
		return null;
	}
}
