package com.meng.bilibilihelper.javaBean;

public class BilibiliUpStatus {
    public int code = 0;
    public String message = "";
    public int ttl = 0;
    public UpstatData data = new UpstatData();

    public class UpstatData {
        public UpstatDataArchive archive = new UpstatDataArchive();
        public UpstatDataArticle article = new UpstatDataArticle();
    }

    public class UpstatDataArchive {
        public int view = 0;
    }

    public class UpstatDataArticle {
        public int view = 0;
    }

}
