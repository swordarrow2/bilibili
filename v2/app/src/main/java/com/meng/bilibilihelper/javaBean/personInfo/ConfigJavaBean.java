package com.meng.bilibilihelper.javaBean.personInfo;

import java.util.ArrayList;

public class ConfigJavaBean {
    public ArrayList<GroupConfig> groupConfigs = new ArrayList<>();
    public ArrayList<Long> QQNotReply = new ArrayList<>();
    public ArrayList<String> wordNotReply = new ArrayList<>();
    public ArrayList<PersonInfo> personInfo = new ArrayList<>();

    public class GroupConfig {
        public long groupNumber = 0;
        public boolean reply = true;
        public ArrayList<Boolean> booleans = new ArrayList<Boolean>(16);
        public int repeatMode = 0;
    }
}
