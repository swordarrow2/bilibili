package com.meng.bilibilihelper.javaBean;

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
