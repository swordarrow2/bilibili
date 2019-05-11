package com.meng.bilibilihelper.javaBean;


public class SendDanmakuReturnedData {
    public int code = 0;
    public String message = "";
    public String msg = "";
    public ReturnDataData data = new ReturnDataData();

    public class ReturnDataData {
        public String verify_url = "";
    }
}
