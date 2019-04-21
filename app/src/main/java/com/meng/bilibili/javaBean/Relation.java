package com.meng.bilibili.javaBean;

public class Relation {
    public int code = 0;
    public String message = "";
    public int ttl = 0;
    public RelationData data = new RelationData();

    public class RelationData {
        public int mid = 0;
        public int following = 0;
        public int whisper = 0;
        public int follower = 0;
        public int black = 0;
    }

}
