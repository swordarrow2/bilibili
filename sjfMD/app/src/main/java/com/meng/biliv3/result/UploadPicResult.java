package com.meng.biliv3.result;

import com.meng.biliv3.libs.*;

public class UploadPicResult {
	public int img_width;
	public int img_height;
	public String img_src;
	public float img_size;
	
	@Override
	public String toString() {
		return GSON.toJson(this);
	}
}
