package com.meng.bilibilihelper.javaBean;

import java.util.ArrayList;

public class LiveBag {
    public int code = 0;
    public String message = "";
    public int ttl = 0;
    public LiveBagData data = new LiveBagData();

    public class LiveBagData {
        public ArrayList<LiveBagDataList> list = new ArrayList<>();
        public long time = 0;
    }

    public class LiveBagDataList {
        public int bag_id = 0;
        public int gift_id = 0;
        public String gift_name = "";
        public int gift_num = 0;
        public int gift_type = 0;
        public long expire_at = 0;
        public String corner_mark = "";
        public ArrayList<Count_map> count_map = new ArrayList<>();
        public int type = 0;
        public String card_image = "";
        public String card_gif = "";
        public int card_id = 0;
        public int card_record_id = 0;
        public boolean is_show_send = false;
    }

    public class Count_map {
        public long num = 0;
        public String text = "";
    }
}
