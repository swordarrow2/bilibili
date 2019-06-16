package com.meng.bilibilihelper.javaBean;

public class UserSpaceToLive {
    public int code;
    public String msg;
    public String message;
    public SpaceToLiveData data;

    public class SpaceToLiveData {
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
}
