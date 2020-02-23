package com.meng.biliv3.javaBean;

import java.util.ArrayList;

public class LivePartList {
    public int code;
    public String message;
    public String msg;
    public ArrayList<DataListItem> data;

    public class DataListItem {
        public int id;
        public String name;
        public ArrayList<ListItemInListItem> list;
    }

    public class ListItemInListItem {
        public String id;
        public String parent_id;
        public String old_area_id;
        public String name;
        public String act_id;
        public String pk_status;
        public int hot_status;
        public String lock_status;
        public String pic;
        public String parent_name;
        public int area_type;
    }

    public ArrayList<ListItemInListItem> getPartInfo() {
        ArrayList<ListItemInListItem> list = new ArrayList<>();
        for (DataListItem dataListItem : data) {
            list.addAll(dataListItem.list);
        }
        return list;
    }
}
