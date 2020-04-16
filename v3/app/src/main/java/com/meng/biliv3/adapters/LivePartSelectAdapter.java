package com.meng.biliv3.adapters;

import android.content.*;
import android.view.*;
import android.widget.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.javaBean.*;

public class LivePartSelectAdapter extends BaseExpandableListAdapter {

	private LivePart livePart;

	public LivePartSelectAdapter(LivePart livePartList) {
		livePart = livePartList;

	}
	//得到子item需要关联的数据
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return livePart.data.get(groupPosition).list.get(childPosition);
	}

	//得到子item的ID
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	//设置子item的组件
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		LivePart.GroupData.PartData pd = livePart.data.get(groupPosition).list.get(childPosition);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) MainActivity.instance.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.live_part_select_layout_children, null);
		}
		TextView tv = (TextView) convertView.findViewById(R.id.layoutchildren_TextView1);
		tv.setText(pd.name);
		return tv;
	}

	//获取当前父item下的子item的个数
	@Override
	public int getChildrenCount(int groupPosition) {
		return livePart.data.get(groupPosition).list.size();
	}

	//获取当前父item的数据
	@Override
	public Object getGroup(int groupPosition) {
		return livePart.data.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return livePart.data.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}
	//设置父item组件
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) MainActivity.instance.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.live_part_select_layout_parent, null);
		}
		TextView tv = (TextView) convertView.findViewById(R.id.layoutparent_TextView1);
		tv.setText(livePart.data.get(groupPosition).name);
		return tv;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
