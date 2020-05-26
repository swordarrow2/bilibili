package com.meng.sjfmd.libs;

import android.app.*;
import android.content.res.*;
import android.view.*;
import com.meng.sjfmd.*;
import com.meng.sjfmd.activity.*;
import com.meng.sjfmd.enums.*;
import java.util.*;

public class ColorManager {
	private int colorBackground;
	private int colorText;
	private int colorDrawerHeader;
	private int themeId;

	private HashMap<View,ColorType> views = new HashMap<>();

	public void addView(View v, ColorType ct) {
		views.put(v, ct);
		setViewColor(v);
	}

	public void setColor(int themeId) {
		switch (themeId) {
			case R.style.AppTheme:
				for (View v:views.keySet()) {
					setViewColor(v);
				}
				break;
			default:
				throw new RuntimeException("unknown theme");
		}
		TypedArray array = MainActivity.instance.getTheme().obtainStyledAttributes(new int[] {android.R.attr.textColorPrimary});
		colorText = array.getColor(0, 0xFF00FF);
		array.recycle();
	}

	private void setViewColor(View v) {
		Resources resources = MainActivity.instance.getResources();
		switch (views.get(v)) {
			case Navigation:
				v.setBackgroundColor(resources.getColor(R.color.app_primary_dark));
				break;
			case DrawerHeader:
				v.setBackgroundColor(resources.getColor(R.color.app_primary));
				break;
			case StatusBar:
				((Activity)(v.getContext())).getWindow().setStatusBarColor(resources.getColor(R.color.app_primary_dark));
				break;
			case ToolBar:
				v.setBackgroundColor(resources.getColor(R.color.app_primary_dark));
				break;
			case RightDrawer:
				v.setBackgroundColor(0xffeeeeee);
				break;
		}
	}
	
	public int getColorText() {
		return colorText;
	}
}
