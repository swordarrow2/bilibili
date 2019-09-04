package com.meng.bilibilihelper.javaBean;

public class BilibiliUserInfo {
    public int code;
    public String message;
    public bilibiliPersonInfoData data = new bilibiliPersonInfoData();

    public class bilibiliPersonInfoData {
        public long mid;
        public String name;
        public String sex;
        public String face;
        public String sign;
        public int rank;
        public int level;
        public int jointime;
        public int moral;
        public int silence;
        public String birthday;
        public float coins;
        public Official official;
        public Vip vip = new Vip();
        public boolean is_followed;
        public String top_photo;
        public Theme theme;
    }

    public class Official {
        public int mid;
        public String title;
        public String desc;
    }

    public class Theme {
    }

    public class Vip {
        public int type;
        public int status;
        public int theme_type;
    }

}
