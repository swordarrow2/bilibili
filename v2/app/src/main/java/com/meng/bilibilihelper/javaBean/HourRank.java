package com.meng.bilibilihelper.javaBean;

import java.util.ArrayList;

public class HourRank {
    public int code = 0;
    public String message = "";
    public String msg = "";
    public HourRankData data = new HourRankData();

    public class HourRankData {
        public RankInfo rank_info = new RankInfo();
        public ArrayList<Object> realtime_hour_rank_info = new ArrayList<>();
        public ArrayList<HourRankDataList> list = new ArrayList<>();
    }

    public class RankInfo {
        public String hour_rank_text = "";
        public int start_timestamp = 0;
        public int end_timestamp = 0;
    }

    public class HourRankDataList {
        public int id = 0;
        public int score = 0;
        public int rank = 0;
        public int uid = 0;
        public String uname = "";
        public String face = "";
        public int prescore = 0;
        public String cover = "";
        public int live_status = 0;
        public int roomid = 0;
        public String link = "";
        public String area_v2_name = "";
        public int area_v2_id = 0;
        public String area_v2_parent_name = "";
        public int area_v2_parent_id = 0;
        public int trend = 0;
        public int follow_status = 0;
        public String unit = "";
        public int self = 0;
        public int broadcast_type = 0;
        public int personal_verify = 0;
        public ArrayList<BestAssist> best_assist = new ArrayList<>();
        public HourRankInfoOwn own = new HourRankInfoOwn();
    }

    public class BestAssist {
        public int uid = 0;
        public String face = "";
        public String uname = "";
    }

    public class HourRankInfoOwn {
        public int id = 0;
        public int score = 0;
        public String rank = "";
        public int uid = 0;
        public String uname = "";
        public String face = "";
        public String cover = "";
        public String link = "";
        public int is_show_own = 0;
        public ArrayList<Integer> follow_list = new ArrayList<>();
        public String unit = "";
        public int prescore = 0;
        public String distance_text = "";
        public int identification = 0;
        public int broadcast_type = 0;
    }
}
