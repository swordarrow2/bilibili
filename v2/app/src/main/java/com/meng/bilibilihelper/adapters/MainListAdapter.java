package com.meng.bilibilihelper.adapters;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.meng.bilibilihelper.R;
import com.meng.bilibilihelper.activity.MainActivity;
import com.meng.bilibilihelper.javaBean.LoginInfoPeople;
import com.meng.bilibilihelper.libAndHelper.DownloadImageRunnable;
import com.meng.bilibilihelper.libAndHelper.HeadType;

import java.io.File;
import java.util.ArrayList;

public class MainListAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<LoginInfoPeople> loginInfoPeopleArrayList;

    public MainListAdapter(Activity context, ArrayList<LoginInfoPeople> groupReplies) {
        this.activity = context;
        this.loginInfoPeopleArrayList = groupReplies;
    }

    public int getCount() {
        return loginInfoPeopleArrayList.size();
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
            holder.aSwitch = (Switch) convertView.findViewById(R.id.group_reply_list_itemSwitch);
            holder.aSwitch.setVisibility(View.GONE);
            holder.ivHead = (ImageView) convertView.findViewById(R.id.group_reply_list_itemImageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final LoginInfoPeople loginInfoPeople = loginInfoPeopleArrayList.get(position);
        holder.tvName.setText(loginInfoPeople.personInfo.data.name);
        File bilibiliImageFile = new File(MainActivity.mainDic + "bilibili/" + loginInfoPeople.personInfo.data.mid + ".jpg");
        if (bilibiliImageFile.exists()) {
            holder.ivHead.setImageBitmap(BitmapFactory.decodeFile(bilibiliImageFile.getAbsolutePath()));
        } else {
            if (MainActivity.onWifi) {
                MainActivity.instence.personInfoFragment.threadPool.execute(new DownloadImageRunnable(activity, holder.ivHead, String.valueOf(loginInfoPeople.personInfo.data.mid), HeadType.BilibiliUser));
            } else {
                holder.ivHead.setImageResource(R.drawable.stat_sys_download_anim0);
                holder.ivHead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.instence.personInfoFragment.threadPool.execute(new DownloadImageRunnable(activity, holder.ivHead, String.valueOf(loginInfoPeople.personInfo.data.mid), HeadType.BilibiliUser));
                    }
                });
            }
        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView ivHead;
        private TextView tvName;
        private Switch aSwitch;
    }
}


