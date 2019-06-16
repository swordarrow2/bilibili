package com.meng.bilibilihelper.javaBean;

import java.util.ArrayList;

public class Choujiang {
    public int code;
    public ChouJiangData data;
    public String message;
    public String msg;

    public class ChouJiangData {
        public int last_raffle_id;
        public ArrayList<ChouJiangDataList> list;
        public String last_raffle_type;
        public String asset_animation_pic;
        public String asset_tips_pic;
    }

    public class ChouJiangDataList {
        public int raffleId;
        public String title;
        public String type;
        public long payflow_id;
        public ChouJiangDataListFromUser from_user;
        public int time_wait;
        public int time;
        public int max_time;
        public int status;
        public String asset_animation_pic;
        public String asset_tips_pic;
        public int sender_type;
        public String from;
    }

    public class ChouJiangDataListFromUser {
        public String uname;
        public String face;
    }
}
