package com.meng.biliv3.javaBean;

import java.util.ArrayList;

public class HourRank {
    public int code;
    public String message;
    public String msg;
    public HourRankData data;

    public class HourRankData {
        public RankInfo rank_info;
        public ArrayList<Object> realtime_hour_rank_info;
        public ArrayList<HourRankDataList> list;
    }

    public class RankInfo {
        public String hour_rank_text;
        public int start_timestamp;
        public int end_timestamp;
    }

    public class HourRankDataList {
        public int id;
        public int score;
        public int rank;
        public int uid;
        public String uname;
        public String face;
        public int prescore;
        public String cover;
        public int live_status;
        public int roomid;
        public String link;
        public String area_v2_name;
        public int area_v2_id;
        public String area_v2_parent_name;
        public int area_v2_parent_id;
        public int trend;
        public int follow_status;
        public String unit;
        public int self;
        public int broadcast_type;
        public int personal_verify;
        public ArrayList<BestAssist> best_assist;
        public HourRankInfoOwn own;
    }

    public class BestAssist {
        public int uid;
        public String face;
        public String uname;
    }

    public class HourRankInfoOwn {
        public int id;
        public int score;
        public String rank;
        public int uid;
        public String uname;
        public String face;
        public String cover;
        public String link;
        public int is_show_own;
        public ArrayList<Integer> follow_list;
        public String unit;
        public int prescore;
        public String distance_text;
        public int identification;
        public int broadcast_type;
    }
}
