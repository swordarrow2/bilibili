package com.meng.bilibilihelper.javaBean;

public class LiveCaptcha {
    public int code = 0;
    public String msg = "";
    public String message = "";
    public LiveCaptchaData data = new LiveCaptchaData();

    public class LiveCaptchaData {
        public String img = "";
    }
}
