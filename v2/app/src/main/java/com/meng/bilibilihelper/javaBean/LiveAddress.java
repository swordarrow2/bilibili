package com.meng.bilibilihelper.javaBean;

import java.util.ArrayList;

public class LiveAddress {
    public int code = 0;
    public String msg = "";
    public String message = "";
    public LiveAddressData data = new LiveAddressData();

    public class LiveAddressData {
        public int current_quality = 0;
        public ArrayList<String> accept_quality = new ArrayList<>();
        public int current_qn = 0;
        public ArrayList<QualityDescription> quality_description = new ArrayList<>();
        public ArrayList<LiveDurl> durl = new ArrayList<>();
    }

    public class LiveDurl {
        public String url = "";
        public int length = 0;
        public int order = 0;
        public int stream_type = 0;
    }

    public class QualityDescription {
        public int qn = 0;
        public String desc = "";
    }

}
