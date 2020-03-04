package com.meng.biliv3.javaBean;

public class Relation {
	public int code;
	public String message;
	public int ttl;
	public Data data;

	public class Data {
		public int mid=0;
		public int following=0;
		public int whisper=0;
		public int follower=0;
		public int black=0;
	}
}
