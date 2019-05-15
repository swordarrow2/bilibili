package com.meng.bilibilihelper.adapters;

import android.app.*;
import android.view.*;
import android.widget.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.javaBean.*;
import java.text.*;
import java.util.*;

public class GiftAdapter extends BaseAdapter {
    private Activity activity;
    public ArrayList<LiveBag.LiveBagDataList> infos;

    public GiftAdapter(Activity context, ArrayList<LiveBag.LiveBagDataList> infos) {
        this.activity = context;
        this.infos = infos;
    }

    public int getCount() {
        return infos.size();
    }

    public Object getItem(int position) {
        return infos.get(position);
    }

    public long getItemId(int position) {
        return infos.get(position).hashCode();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.gift_list_item, null);
            holder = new ViewHolder();
            holder.tvGiftName = (TextView) convertView.findViewById(R.id.gitft_list_item_gift_name);
            holder.tvGiftCount = (TextView) convertView.findViewById(R.id.gitft_list_item_gift_count);
            holder.tvExpire = (TextView) convertView.findViewById(R.id.gitft_list_item_gift_expire);
            holder.tvMark = (TextView) convertView.findViewById(R.id.gitft_list_item_mark);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
		LiveBag.LiveBagDataList liveBagDataList = infos.get(position);
        holder.tvGiftName.setText(liveBagDataList.gift_name);
        holder.tvGiftCount.setText("数量:"+liveBagDataList.gift_num);
        holder.tvExpire.setText("过期时间:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(liveBagDataList.expire_at * 1000)));
        holder.tvMark.setText(liveBagDataList.corner_mark);
        return convertView;
    }

    private class ViewHolder {
        private TextView tvGiftName;
        private TextView tvGiftCount;
        private TextView tvExpire;
        private TextView tvMark;
    }
}

