package com.meng.bilibilihelper;

import android.graphics.*;
import android.view.*;
import android.widget.*;
import android.widget.CompoundButton.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import java.io.*;
import java.util.*;
import com.meng.bilibilihelper.javaBean.*;

public class CustomDanmakuAdapter extends BaseAdapter {
    private MainActivity context;
    private ArrayList<LoginInfoPeople> groupReplies;
	private ArrayList<Boolean> willSend=new ArrayList<>();

    public CustomDanmakuAdapter(MainActivity context, ArrayList<LoginInfoPeople> groupReplies) {
        this.context = context;
        this.groupReplies = groupReplies;
		for(LoginInfoPeople l:groupReplies){
		  willSend.add(false);
		}
	  }

    public int getCount() {
        return groupReplies.size();
	  }
	  
	  public ArrayList<Boolean> getWillSend(){
		return willSend;
	  }

    public Object getItem(int position) {
        return groupReplies.get(position);
	  }

    public long getItemId(int position) {
        return groupReplies.get(position).hashCode();
	  }

    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = context.getLayoutInflater().inflate(R.layout.list_item_image_text_switch, null);
            holder = new ViewHolder();
            holder.groupNumber = (TextView) convertView.findViewById(R.id.group_reply_list_itemTextView);
            holder.replySwitch = (Switch) convertView.findViewById(R.id.group_reply_list_itemSwitch);
            holder.imageView = (ImageView) convertView.findViewById(R.id.group_reply_list_itemImageView);
            convertView.setTag(holder);
		  } else {
            holder = (ViewHolder) convertView.getTag();
		  }
        final LoginInfoPeople groupReply = groupReplies.get(position);
        holder.groupNumber.setText(String.valueOf(groupReply.personInfo.data.mid));
        holder.replySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			  @Override
			  public void onCheckedChanged(CompoundButton p1, boolean p2) {			  
					  willSend.set(position,p2);
					  				
				}
			});

        holder.replySwitch.setChecked(willSend.get(position));
		File bilibiliImageFile = new File(MainActivity.mainDic + "bilibili/" + groupReply.personInfo.data.mid + ".jpg");
		if (bilibiliImageFile.exists()) {
			holder.imageView.setImageBitmap(BitmapFactory.decodeFile(bilibiliImageFile.getAbsolutePath()));
		  } else {
			if (MainActivity.onWifi) {
				  MainActivity.instence.personInfoFragment.threadPool.execute(new DownloadImageRunnable(context, holder.imageView, String.valueOf(groupReply.personInfo.data.mid), HeadType.BilibiliUser));
			  } else {
				holder.imageView.setImageResource(R.drawable.stat_sys_download_anim0);
				holder.imageView.setOnClickListener(new View.OnClickListener() {
					  @Override
					  public void onClick(View v) {
							MainActivity.instence.personInfoFragment.threadPool.execute(new DownloadImageRunnable(context, holder.imageView, String.valueOf(groupReply.personInfo.data.mid), HeadType.BilibiliUser));
						}
					});
			  }
		  }
        
        return convertView;
	  }

    private class ViewHolder {
        private ImageView imageView;
        private TextView groupNumber;
        private Switch replySwitch;
	  }
  }



