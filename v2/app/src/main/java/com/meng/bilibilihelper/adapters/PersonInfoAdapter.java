package com.meng.bilibilihelper.adapters;

import android.app.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.fragment.*;
import com.meng.bilibilihelper.javaBean.personInfo.*;
import com.meng.bilibilihelper.libAndHelper.*;
import java.io.*;
import java.util.*;

public class PersonInfoAdapter extends BaseAdapter {
    private Activity context;
    private ArrayList<PersonInfo> infos;

    public PersonInfoAdapter(Activity context, ArrayList<PersonInfo> infos) {
        this.context = context;
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
            convertView = context.getLayoutInflater().inflate(R.layout.person_info_list_item, null);
            holder = new ViewHolder();
            holder.imageViewQQHead = (ImageView) convertView.findViewById(R.id.imageView_qqHead);
            holder.imageViewBilibiiliHead = (ImageView) convertView.findViewById(R.id.imageView_bilibiliHead);
            holder.textViewName = (TextView) convertView.findViewById(R.id.gitft_list_item_gift_name);
            holder.textViewQQNumber = (TextView) convertView.findViewById(R.id.gitft_list_item_gift_count);
            holder.textViewBilibiliUid = (TextView) convertView.findViewById(R.id.gitft_list_item_gift_expire);
            holder.textViewBilibiliLiveId = (TextView) convertView.findViewById(R.id.gitft_list_item_mark);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final PersonInfo personInfo = infos.get(position);
        holder.textViewName.setText(personInfo.name);
        holder.textViewQQNumber.setText(String.valueOf(personInfo.qq));
        holder.textViewBilibiliUid.setText(String.valueOf(personInfo.bid));
        holder.textViewBilibiliLiveId.setText(String.valueOf(personInfo.bliveRoom));
        File qqImageFile = new File(MainActivity.instence.mainDic + "user/" + personInfo.qq + ".jpg");
        File bilibiliImageFile = new File(MainActivity.instence.mainDic + "bilibili/" + personInfo.bid + ".jpg");
        if (personInfo.qq == 0) {
            holder.imageViewQQHead.setImageResource(R.drawable.stat_sys_download_anim0);
        } else {
            if (qqImageFile.exists()) {
                holder.imageViewQQHead.setImageBitmap(BitmapFactory.decodeFile(qqImageFile.getAbsolutePath()));
            } else {
                if (MainActivity.onWifi) {
                    MainActivity.instence.getFragment("人员信息",PersonInfoFragment.class).threadPool.execute(new DownloadImageRunnable(context, holder.imageViewQQHead, String.valueOf(personInfo.qq), DownloadImageRunnable.QQUser));
                } else {
                    holder.imageViewQQHead.setImageResource(R.drawable.stat_sys_download_anim0);
                    holder.imageViewQQHead.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MainActivity.instence.getFragment("人员信息",PersonInfoFragment.class).threadPool.execute(new DownloadImageRunnable(context, holder.imageViewQQHead, String.valueOf(personInfo.qq), DownloadImageRunnable.QQUser));
                        }
                    });
                }
            }
        }
        if (personInfo.bid == 0) {
            holder.imageViewBilibiiliHead.setImageResource(R.drawable.stat_sys_download_anim0);
        } else {
            if (bilibiliImageFile.exists()) {
                holder.imageViewBilibiiliHead.setImageBitmap(BitmapFactory.decodeFile(bilibiliImageFile.getAbsolutePath()));
            } else {
                if (MainActivity.onWifi) {
                    MainActivity.instence.getFragment("人员信息",PersonInfoFragment.class).threadPool.execute(new DownloadImageRunnable(context, holder.imageViewBilibiiliHead, String.valueOf(personInfo.bid), DownloadImageRunnable.BilibiliUser));
                } else {
                    holder.imageViewBilibiiliHead.setImageResource(R.drawable.stat_sys_download_anim0);
                    holder.imageViewBilibiiliHead.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MainActivity.instence.getFragment("人员信息",PersonInfoFragment.class).threadPool.execute(new DownloadImageRunnable(context, holder.imageViewBilibiiliHead, String.valueOf(personInfo.bid), DownloadImageRunnable.BilibiliUser));
                        }
                    });
                }
            }
        }
        return convertView;
    }

    private final class ViewHolder {
        private ImageView imageViewQQHead;
        private ImageView imageViewBilibiiliHead;
        private TextView textViewName;
        private TextView textViewQQNumber;
        private TextView textViewBilibiliUid;
        private TextView textViewBilibiliLiveId;
    }
}

