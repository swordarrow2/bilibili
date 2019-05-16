package com.meng.bilibilihelper.adapters;

import android.app.Activity;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import android.widget.CompoundButton.*;

import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;

import java.io.*;
import java.util.*;

import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.libAndHelper.DownloadImageRunnable;
import com.meng.bilibilihelper.libAndHelper.HeadType;

public class ListWithImageSwitchAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<LoginInfoPeople> loginInfoPeopleArrayList;
    private ArrayList<Boolean> checked = new ArrayList<>();

    public ListWithImageSwitchAdapter(Activity context, ArrayList<LoginInfoPeople> groupReplies) {
        this.activity = context;
        this.loginInfoPeopleArrayList = groupReplies;
        for (LoginInfoPeople l : groupReplies) {
            checked.add(false);
        }
    }

    public int getCount() {
        return loginInfoPeopleArrayList.size();
    }

    public boolean getChecked(int position) {
        return checked.get(position);
    }

    public Object getItem(int position) {
        return loginInfoPeopleArrayList.get(position);
    }

    public long getItemId(int position) {
        return loginInfoPeopleArrayList.get(position).hashCode();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.list_item_image_text_switch, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.group_reply_list_itemTextView);
            holder.switchEnable = (Switch) convertView.findViewById(R.id.group_reply_list_itemSwitch);
            holder.ivHeader = (ImageView) convertView.findViewById(R.id.group_reply_list_itemImageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final LoginInfoPeople loginInfoPeople = loginInfoPeopleArrayList.get(position);
        holder.tvName.setText(loginInfoPeople.personInfo.data.name);
        holder.switchEnable.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton p1, boolean p2) {
                checked.set(position, p2);

            }
        });

        holder.switchEnable.setChecked(checked.get(position));
        File bilibiliImageFile = new File(MainActivity.instence.mainDic + "bilibili/" + loginInfoPeople.personInfo.data.mid + ".jpg");
        if (bilibiliImageFile.exists()) {
            holder.ivHeader.setImageBitmap(BitmapFactory.decodeFile(bilibiliImageFile.getAbsolutePath()));
        } else {
            if (MainActivity.onWifi) {
                MainActivity.instence.personInfoFragment.threadPool.execute(new DownloadImageRunnable(activity, holder.ivHeader, String.valueOf(loginInfoPeople.personInfo.data.mid), HeadType.BilibiliUser));
            } else {
                holder.ivHeader.setImageResource(R.drawable.stat_sys_download_anim0);
                holder.ivHeader.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.instence.personInfoFragment.threadPool.execute(new DownloadImageRunnable(activity, holder.ivHeader, String.valueOf(loginInfoPeople.personInfo.data.mid), HeadType.BilibiliUser));
                    }
                });
            }
        }

        return convertView;
    }

    private class ViewHolder {
        private ImageView ivHeader;
        private TextView tvName;
        private Switch switchEnable;
    }
}



