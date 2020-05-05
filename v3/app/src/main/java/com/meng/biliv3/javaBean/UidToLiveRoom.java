package com.meng.biliv3.javaBean;

import com.meng.biliv3.activity.*;

public class UidToLiveRoom {
	public int code;
    public String msg;
    public String message;
    public Data data = new Data();

    public class Data {
        public int roomStatus;
        public int roundStatus;
        public int liveStatus;
        public String url;
        public String title;
        public String cover;
        public int online;
        public int roomid;
        public int broadcast_type;
    }
	
	@Override
	public String toString() {
		return MainActivity.instance.gson.toJson(this);
	}
}
