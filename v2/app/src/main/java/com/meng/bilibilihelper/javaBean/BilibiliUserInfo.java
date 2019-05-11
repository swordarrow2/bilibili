package com.meng.bilibilihelper.javaBean;

import java.util.ArrayList;

public class BilibiliUserInfo {
    public int code = 0;
    public String message = "";
    public bilibiliPersonInfoData data = new bilibiliPersonInfoData();

    public class bilibiliPersonInfoData {
        public long mid = 0;
        public String name = "";
        public String sex = "";
        public String face = "";
        public String sign = "";
        public int rank = 0;
        public int level = 0;
        public int jointime = 0;
        public int moral = 0;
        public int silence = 0;
        public String birthday = "";
        public float coins = 0f;
        public Official official = new Official();
        public Vip vip = new Vip();
        public boolean is_followed = false;
        public String top_photo = "";
        public Theme theme = new Theme();
    }

    public class Official {
        public int mid = 0;
        public String title = "";
        public String desc = "";
    }

    public class Theme {
    }

    public class Vip {
        public int type = 0;
        public int status = 0;
        public int theme_type = 0;
    }

}
