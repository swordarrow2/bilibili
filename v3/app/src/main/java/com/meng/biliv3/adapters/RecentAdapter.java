package com.meng.biliv3.adapters;

import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.gson.reflect.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.fragment.*;
import com.meng.biliv3.javaBean.*;
import com.meng.biliv3.libs.*;
import java.io.*;
import java.util.*;

public class RecentAdapter extends BaseAdapter {

    private ArrayList<Recent> recents = new ArrayList<>();

	private String jsonPath;

	public RecentAdapter() {
		jsonPath = MainActivity.instance.getFilesDir() + "/recent.json";
		File f = new File(jsonPath);
        if (!f.exists()) {
            saveConfig();
		}
		recents = MainActivity.instance.gson.fromJson(Tools.FileTool.readString(jsonPath), new TypeToken<ArrayList<Recent>>(){}.getType());
		if (recents == null) {
			recents = new ArrayList<>();
		}
	}

	public void add(String type, long id, String name) {
		for (int i=0;i < recents.size();++i) {
			Recent r=recents.get(i);
			if (r.id == id && r.type.equals(type)) {
				return;
			}
		}
		recents.add(0, new Recent(type, id, name));
		notifyDataSetChanged();
		saveConfig();
	}

	public void rename(String origin, String newName) {
		for (int i=0;i < recents.size();++i) {
			Recent r=recents.get(i);
			if (r.name.equals(origin)) {
				recents.remove(i);
				r.name = newName;
				recents.add(0, r);
				break;
			}
		}
		notifyDataSetChanged();
		saveConfig();
	}

	public void toFirst(String name) {
		for (int i=0;i < recents.size();++i) {
			if (recents.get(i).name.equals(name)) {
				recents.add(0, recents.remove(i));
				break;
			}
		}
		notifyDataSetChanged();
		saveConfig();
	}

	public void remove(String id) {
		for (int i=0;i < recents.size();++i) {
			if (recents.get(i).name.equals(id)) {
				recents.remove(i);
				break;
			}
		}
		notifyDataSetChanged();
		saveConfig();
	}

    public int getCount() {
        return recents.size();
    }

    public Object getItem(int position) {
        return recents.get(position);
    }

    public long getItemId(int position) {
        return recents.get(position).hashCode();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = MainActivity.instance.getLayoutInflater().inflate(R.layout.recent_list_item, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.recent_list_itemTextView);
            holder.close = (ImageButton) convertView.findViewById(R.id.recent_list_itemImageButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final String s = recents.get(position).name;
        holder.tvName.setText(s);
		holder.tvName.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1) {
					Recent r = recents.get(position);
					toFirst(r.name);		
					switch (r.type) {
						case BaseIdFragment.typeAv:
							MainActivity.instance.showFragment(AvFragment.class, r.type, r.id);
							break;
						case BaseIdFragment.typeCv:
							MainActivity.instance.showFragment(CvFragment.class, r.type, r.id);
							break;
						case BaseIdFragment.typeLive:
							MainActivity.instance.showFragment(LiveFragment.class, r.type, r.id);
							break;
						case BaseIdFragment.typeUID:
							MainActivity.instance.showFragment(UidFragment.class, r.type, r.id);
							break;
						case "AVBV转换":
							MainActivity.instance.showFragment(AvBvConvertFragment.class, "AVBV转换");
							break;
						case "管理账号":
							MainActivity.instance.showFragment(ManagerFragment.class, MainActivity.AccountManager);
							break;
						case "设置":
							MainActivity.instance.showFragment(SettingsFragment.class, MainActivity.Settings);
							break;
						case "动态":
							MainActivity.instance.showFragment(DynamicFragment.class, "动态");
					}
				}
			});
		holder.close.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1) {
					MainActivity.instance.removeFragment(s);
				}
			});
        return convertView;
    }

    private class ViewHolder {
        private TextView tvName;
        private ImageButton close;
    }

	private void saveConfig() {
        try {
            FileOutputStream fos = null;
            OutputStreamWriter writer = null;
            File file = new File(jsonPath);
            fos = new FileOutputStream(file);
            writer = new OutputStreamWriter(fos, "utf-8");
            writer.write(MainActivity.instance.gson.toJson(recents));
            writer.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



