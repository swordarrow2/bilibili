package com.meng.bilibilihelper;

import android.app.*;
import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;

public class MengTextview extends LinearLayout{
	private Context context;

	private TextView textViewTitle;
	private TextView textViewSummry;
	private ProgressBar progressBar;

	public MengTextview(Context context, String title, long summry){
		this(context,title,String.valueOf(summry));
	  }
	public MengTextview(Context context, String title, float summry){
		this(context,title,String.valueOf(summry));
	  }

	public MengTextview(final Context context, String title, String summry){
		super(context);
		this.context=context;
		LayoutInflater.from(context).inflate(R.layout.meng_textview,this);
		textViewTitle=(TextView) findViewById(R.id.meng_network_TextView);
		textViewSummry=(TextView) findViewById(R.id.meng_network_TextView2);
		progressBar=(ProgressBar) findViewById(R.id.meng_network_ProgressBar);
		setTitle(title);
		setSummry(summry);
		textViewSummry.setOnLongClickListener(new OnLongClickListener(){

			  @Override
			  public boolean onLongClick(View p1){
				  ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
				  ClipData clipData = ClipData.newPlainText("text",((TextView)p1).getText().toString());
				  clipboardManager.setPrimaryClip(clipData);
				  Toast.makeText(context,"已复制到剪贴板",Toast.LENGTH_SHORT).show();
				  return true;
				}
			});
	  }
	public MengTextview(final Context context, AttributeSet attributeSet){
		super(context,attributeSet);
		this.context=context;
		LayoutInflater.from(context).inflate(R.layout.meng_textview,this);
		textViewTitle=(TextView) findViewById(R.id.meng_network_TextView);
		textViewSummry=(TextView) findViewById(R.id.meng_network_TextView2);
		progressBar=(ProgressBar) findViewById(R.id.meng_network_ProgressBar);
		textViewSummry.setOnLongClickListener(new OnLongClickListener(){

			  @Override
			  public boolean onLongClick(View p1){
				  ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
				  ClipData clipData = ClipData.newPlainText("text",((TextView)p1).getText().toString());
				  clipboardManager.setPrimaryClip(clipData);
				  Toast.makeText(context,"已复制到剪贴板",Toast.LENGTH_SHORT).show();
				  return true;
				}
			});
	  }


	public void setTitle(String s){
		textViewTitle.setText(s);
	  }

	public String getTitle(){
		return textViewTitle.getText().toString();
	  }

	public void setSummry(final String s){
		((Activity) context).runOnUiThread(new Runnable() {
			  @Override
			  public void run(){
				  textViewSummry.setText(s);
				  progressBar.setVisibility(GONE);
				  MengTextview.this.setVisibility(VISIBLE);
				}
			});
	  }

	public void setSummry(int i){
		setSummry(String.valueOf(i));
	  }

	public void setSummry(long l){
		setSummry(String.valueOf(l));
	  }

	public void setSummry(float f){
		setSummry(String.valueOf(f));
	  }

	public String getSummry(){
		return textViewSummry.getText().toString();
	  }

  }
