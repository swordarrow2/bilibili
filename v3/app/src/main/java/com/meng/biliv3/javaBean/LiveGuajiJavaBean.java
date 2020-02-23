package com.meng.biliv3.javaBean;

public class LiveGuajiJavaBean {
    public boolean finish;
    public String name;
    public int id;
    public boolean isNeedRefresh;
    public boolean isShowed;
    public String cookie;
    public LiveTimeStamp liveTimeStamp;
    public String referer;
    public boolean isFirstHeartBeat = true;

    public LiveGuajiJavaBean(String name, String referer, String cookie) {
        this.name = name;
        this.referer = referer;
        this.cookie = cookie;
    }

    public class LiveTimeStamp {
        public int code;
        public String msg;
        public String message;
        public LiveTimeStampData data;

        public class LiveTimeStampData {
            public int minute;
            public int silver;
            public long time_start;
            public long time_end;
            public int times;
            public int max_times;
        }
    }
}
