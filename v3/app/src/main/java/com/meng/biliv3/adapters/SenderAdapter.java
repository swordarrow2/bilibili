package com.meng.biliv3.adapters;

import android.graphics.*;
import android.view.*;
import android.widget.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.javaBean.*;
import com.meng.biliv3.libAndHelper.*;
import java.io.*;
import java.util.*;

public class SenderAdapter extends BaseAdapter {
    private MainActivity activity;
	private ArrayList<DanmakuBean> danmakuList=null;

    public SenderAdapter(MainActivity context, ArrayList<DanmakuBean> adb) {
        activity = context;
		danmakuList = adb;
    }

    public int getCount() {
        return danmakuList.size();
    }

    public Object getItem(int position) {
        return danmakuList.get(position);
    }

    public long getItemId(int position) {
        return danmakuList.get(position).hashCode();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.hash_to_id_list_item, null);
            holder = new ViewHolder();
            holder.tvId = (TextView) convertView.findViewById(R.id.hash_to_id_list_itemTextView_uid);
			holder.tvTime = (TextView) convertView.findViewById(R.id.hash_to_id_list_itemTextView_time);
			holder.tvTimeStamp = (TextView) convertView.findViewById(R.id.hash_to_id_list_itemTextView_timeStamp);
			holder.tvMsg = (TextView) convertView.findViewById(R.id.hash_to_id_list_itemTextView_msg);
			convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DanmakuBean db = danmakuList.get(position);
        holder.tvId.setText("UID:" + (db.uid == -1 ?"正在获取": String.valueOf(db.uid)));
		holder.tvTime.setText("视频时间:" + db.time + "s");
		holder.tvTimeStamp.setText("发送时间:" + Tools.Time.getTime(db.timeStamp * 1000));
		holder.tvMsg.setText("内容:" + db.msg);
        return convertView;
    }

    private class ViewHolder {
		private TextView tvId;
		private TextView tvTime;
		private TextView tvTimeStamp;
		private TextView tvMsg;
    }
}
