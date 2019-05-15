package com.meng.bilibilihelper.javaBean;

import java.util.ArrayList;

public class Choujiang {
    public int code = 0;
    public ChouJiangData data = new ChouJiangData();
    public String message = "";
    public String msg = "";

    public class ChouJiangData {
        public int last_raffle_id = 0;
        public ArrayList<ChouJiangDataList> list = new ArrayList<>();
        public String last_raffle_type = "";
        public String asset_animation_pic = "";
        public String asset_tips_pic = "";
    }

    public class ChouJiangDataList {
        public int raffleId = 0;
        public String title = "";
        public String type = "";
        public long payflow_id = 0;
        public ChouJiangDataListFromUser from_user = new ChouJiangDataListFromUser();
        public int time_wait = 0;
        public int time = 0;
        public int max_time = 0;
        public int status = 0;
        public String asset_animation_pic = "";
        public String asset_tips_pic = "";
        public int sender_type = 0;
        public String from = "";
    }

    public class ChouJiangDataListFromUser {
        public String uname = "";
        public String face = "";
    }
}
