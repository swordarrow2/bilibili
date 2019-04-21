package com.meng.bilibili.fragments;

import com.google.gson.Gson;
import com.meng.bilibili.MainActivity;
import com.meng.bilibili.javaBean.NewArticleBean;
import com.meng.bilibili.javaBean.NewVideoBean;

public class NewUpdateManager {

    public boolean check(long id) {
        Gson gson = new Gson();
        NewVideoBean vlist = gson
                .fromJson(MainActivity
                                .getSourceCode("https://space.bilibili.com/ajax/member/getSubmitVideos?mid="
                                        + id + "&page=1&pagesize=1")
                                .replace("\"3\":", "\"n3\":").replace("\"4\":", "\"n4\":"),
                        NewVideoBean.class);

        NewArticleBean articles = gson
                .fromJson(
                        MainActivity.getSourceCode("http://api.bilibili.com/x/space/article?mid=" + id
                                + "&pn=1&ps=1&sort=publish_time&jsonp=jsonp"),
                        NewArticleBean.class);

        return false;
    }

}
