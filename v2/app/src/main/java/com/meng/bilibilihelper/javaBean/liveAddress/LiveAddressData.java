package com.meng.bilibilihelper.javaBean.liveAddress;

import java.util.ArrayList;

public class LiveAddressData {
    public int current_quality = 0;
    public ArrayList<String> accept_quality = new ArrayList<>();
    public int current_qn = 0;
    public ArrayList<QualityDescription> quality_description = new ArrayList<>();
    public ArrayList<LiveDurl> durl = new ArrayList<>();
}
