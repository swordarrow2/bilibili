package com.meng.bilibilihelper.javaBean;

import java.util.ArrayList;

public class LiveBag {
    public int code;
    public String message;
    public int ttl;
    public LiveBagData data;

    public class LiveBagData {
        public ArrayList<LiveBagDataList> list;
        public long time;
    }

    public class LiveBagDataList {
        public int bag_id;
        public int gift_id;
        public String gift_name;
        public int gift_num;
        public int gift_type;
        public long expire_at;
        public String corner_mark;
        public ArrayList<Count_map> count_map;
        public int type;
        public String card_image;
        public String card_gif;
        public int card_id;
        public int card_record_id;
        public boolean is_show_send;
    }

    public class Count_map {
        public long num;
        public String text;
    }
}
