package com.meng.bilibilihelper.libAndHelper;

import android.content.*;
import android.util.*;
import android.widget.*;

public class MvideoView extends VideoView {
	public MvideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
		setMediaController(new MediaController(context));
    }

	/*@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), getSuggestedMinimumHeight());
	}*/
}
