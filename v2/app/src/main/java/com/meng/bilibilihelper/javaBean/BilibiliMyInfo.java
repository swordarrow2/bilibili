package com.meng.bilibilihelper.javaBean;

public class BilibiliMyInfo {
    public int code = 0;
    public String message = "";
    public int ttl = 0;
    public MyInfoData data = new MyInfoData();

    public class MyInfoData {
        public long mid = 0;
        public String name = "";
        public String sex = "";
        public String face = "";
        public String sign = "";
        public int rank = 0;
        public int level = 0;
        public long jointime = 0;
        public int moral = 0;
        public int silence = 0;
        public int email_status = 0;
        public int tel_status = 0;
        public int identification = 0;
        public Vip vip = new Vip();
        public Pendant pendant = new Pendant();
        public Official official = new Official();
        public long birthday = 0;
        public int is_tourist = 0;
        public LevelExp level_exp = new LevelExp();
        public float coins = 0f;
        public int following = 0;
        public int follower = 0;
    }

    public class LevelExp {
        public int current_level = 0;
        public int current_min = 0;
        public int current_exp = 0;
        public int next_exp = 0;
    }

    public class Nameplate {
        public int nid = 0;
        public String name = "";
        public String image = "";
        public String image_small = "";
        public String level = "";
        public String condition = "";
    }

    public class Official {
        public int role = 0;
        public String title = "";
        public String desc = "";
    }

    public class Pendant {
        public int pid = 0;
        public String name = "";
        public String image = "";
        public int expire = 0;
    }

    public class Vip {
        public int type = 0;
        public int status = 0;
        public int due_date = 0;
        public int vip_pay_type = 0;
        public int theme_type = 0;
    }


}
