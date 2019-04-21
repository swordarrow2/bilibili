package com.meng.bilibili.javaBean;

import java.util.ArrayList;

public class LiveToSpace {
    public int code = 0;
    public String msg = "";
    public String message = "";
    public LiveToSpaceData data = new LiveToSpaceData();

    public class LiveToSpaceData {
        public LiveToSpaceDataInfo info = new LiveToSpaceDataInfo();
        public LiveToSpaceDataLevel level = new LiveToSpaceDataLevel();
        public int san = 0;
    }

    public class LiveToSpaceDataInfo {
        public int uid = 0;
        public String uname = "";
        public String face = "";
        public String rank = "";
        public int identification = 0;
        public int mobile_verify = 0;
        public int platform_user_level = 0;
        public int vip_type = 0;
        public int gender = 0;
        public LiveToSpaceDataInfoOfficialVerify official_verify = new LiveToSpaceDataInfoOfficialVerify();
    }

    public class LiveToSpaceDataInfoOfficialVerify {
        public int type = 0;
        public String desc = "";
        public int role = 0;
    }
    public class LiveToSpaceDataLevelMasterLevel {
        public int level = 0;
        ArrayList<Integer> current = new ArrayList<>();
        ArrayList<Integer> next = new ArrayList<>();
        public int color = 0;
        public int anchor_score = 0;
        public int upgrade_score = 0;
        public int master_level_color = 0;
        public String sort = "";
    }
    public class LiveToSpaceDataLevel {
        public int uid = 0;
        public int cost = 0;
        public int rcost = 0;
        public String user_score = "";
        public int vip = 0;
        public String vip_time = "";
        public int svip = 0;
        public String svip_time = "";
        public String update_time = "";
        public LiveToSpaceDataLevelMasterLevel master_level = new LiveToSpaceDataLevelMasterLevel();
        public int user_level = 0;
        public int color = 0;
        public int anchor_score = 0;
    }


}
