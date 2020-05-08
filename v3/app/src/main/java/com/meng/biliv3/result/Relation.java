package com.meng.biliv3.result;

import com.meng.biliv3.activity.*;
import com.meng.biliv3.libs.*;

	public class Relation {
	public int code;
	public String message;
	public int ttl;
	public Data data;

	public class Data {
		public int mid;
		public int following;
		public int whisper;
		public int follower;
		public int black;
	}
	
	@Override
	public String toString() {
		return GSON.toJson(this);
	}
}
