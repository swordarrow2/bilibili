package com.meng.biliv3.libs;

import android.app.*;
import android.content.res.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;

public class ColorManager {
	private final int colorBackground;
	private final int colorText;
	private final int colorDrawerHeader;
	private int themeId;

	public ColorManager(int themeId) {
		this.themeId = themeId;
		switch (themeId) {
			case R.style.AppThemeLight:
			case R.style.AppThemeHoloL:
				colorBackground = 0xffeeeeee;
				colorDrawerHeader = 0xff009688;
				break;
			case R.style.AppThemeDark:
			case R.style.AppThemeHolo:
				colorBackground = 0x2feeeeee;
				colorDrawerHeader = 0x7f009688;
				break;
			default:
				throw new RuntimeException("unknown theme");
		}
		TypedArray array = MainActivity.instance.getTheme().obtainStyledAttributes(new int[] {android.R.attr.textColorPrimary});
		colorText = array.getColor(0, 0xFF00FF);
		array.recycle();
	}

	public int getColorDrawerHeader() {
		return colorDrawerHeader;
	}

	public int getColorText() {
		return colorText;
	}

	public int getColorBackground() {
		return colorBackground;
	}

	public void doRun(Activity a) {
		//if (themeId == R.style.AppThemeHolo) {
		ActionBar ab=a.getActionBar();
		ab.setDisplayOptions(ab.getDisplayOptions() ^ ActionBar.DISPLAY_HOME_AS_UP);
		//	}
	}
}
