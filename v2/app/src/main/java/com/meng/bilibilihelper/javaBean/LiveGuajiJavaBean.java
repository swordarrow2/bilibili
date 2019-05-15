package com.meng.bilibilihelper.javaBean;

public class LiveGuajiJavaBean {
    public boolean finish = false;
    public String name = "";
    public int id = 0;
    public boolean isNeedRefresh = false;
    public boolean isShowed = false;
    public String cookie = "";
    public LiveTimeStamp liveTimeStamp = new LiveTimeStamp();
    public String referer = "";
    public boolean isFirstHeartBeat = true;

    public LiveGuajiJavaBean(String name, String referer, String cookie) {
        this.name = name;
        this.referer = referer;
        this.cookie = cookie;
    }

    public class LiveTimeStamp {
        public int code = 0;
        public String msg = "";
        public String message = "";
        public LiveTimeStampData data = new LiveTimeStampData();

        public class LiveTimeStampData {
            public int minute = 0;
            public int silver = 0;
            public long time_start = 0;
            public long time_end = 0;
            public int times = 0;
            public int max_times = 0;
        }
    }
}