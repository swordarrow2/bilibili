package com.meng.bilibilihelper.adapters;

import android.app.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;

import com.meng.bilibilihelper.javaBean.PlanePlayerList;
import com.meng.bilibilihelper.libAndHelper.DownloadImageRunnable;
import com.meng.bilibilihelper.libAndHelper.HeadType;
import com.meng.bilibilihelper.R;
import com.meng.bilibilihelper.activity.*;
import java.io.*;
import java.util.*;

public class PersonInfoAdapter extends BaseAdapter {
    private Activity context;
    private ArrayList<PlanePlayerList.PlanePlayer> infos;

    public PersonInfoAdapter(Activity context, ArrayList<PlanePlayerList.PlanePlayer> infos) {
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
            holder.textViewName = (TextView) convertView.findViewById(R.id.textView_name);
            holder.textViewQQNumber = (TextView) convertView.findViewById(R.id.textView_qqnum);
            holder.textViewBilibiliUid = (TextView) convertView.findViewById(R.id.textView_bilibiliUid);
            holder.textViewBilibiliLiveId = (TextView) convertView.findViewById(R.id.textView_bilibiliLiveId);
            convertView.setTag(holder);
		  } else {
            holder = (ViewHolder) convertView.getTag();
		  }
        final PlanePlayerList.PlanePlayer personInfo = infos.get(position);
        holder.textViewName.setText(personInfo.name);
        holder.textViewQQNumber.setText(String.valueOf(personInfo.qq));
        holder.textViewBilibiliUid.setText(String.valueOf(personInfo.bid));
        holder.textViewBilibiliLiveId.setText(String.valueOf(personInfo.bliveRoom));
        File qqImageFile = new File(MainActivity.mainDic + "user/" + personInfo.qq + ".jpg");
        File bilibiliImageFile = new File(MainActivity.mainDic + "bilibili/" + personInfo.bid + ".jpg");
        if (personInfo.qq == 0) {
            holder.imageViewQQHead.setImageResource(R.drawable.stat_sys_download_anim0);
		  } else {
            if (qqImageFile.exists()) {
                holder.imageViewQQHead.setImageBitmap(BitmapFactory.decodeFile(qqImageFile.getAbsolutePath()));
			  } else {
                if (MainActivity.onWifi) {
					  MainActivity.instence.personInfoFragment.threadPool.execute(new DownloadImageRunnable(context, holder.imageViewQQHead, String.valueOf(personInfo.qq), HeadType.QQUser));
				  } else {
                    holder.imageViewQQHead.setImageResource(R.drawable.stat_sys_download_anim0);
                    holder.imageViewQQHead.setOnClickListener(new View.OnClickListener() {
						  @Override
						  public void onClick(View v) {
								MainActivity.instence.personInfoFragment.threadPool.execute(new DownloadImageRunnable(context, holder.imageViewQQHead, String.valueOf(personInfo.qq), HeadType.QQUser));
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
					  MainActivity.instence.personInfoFragment.threadPool.execute(new DownloadImageRunnable(context, holder.imageViewBilibiiliHead, String.valueOf(personInfo.bid), HeadType.BilibiliUser));
				  } else {
                    holder.imageViewBilibiiliHead.setImageResource(R.drawable.stat_sys_download_anim0);
                    holder.imageViewBilibiiliHead.setOnClickListener(new View.OnClickListener() {
						  @Override
						  public void onClick(View v) {
								MainActivity.instence.personInfoFragment.threadPool.execute(new DownloadImageRunnable(context, holder.imageViewBilibiiliHead, String.valueOf(personInfo.bid), HeadType.BilibiliUser));
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

