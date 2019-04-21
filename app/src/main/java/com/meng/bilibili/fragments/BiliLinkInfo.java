package com.meng.bilibili.fragments;

import com.google.gson.Gson;
import com.meng.bilibili.MainActivity;
import com.meng.bilibili.javaBean.ArticleInfoBean;
import com.meng.bilibili.javaBean.VideoInfoBean;

public class BiliLinkInfo {
    private final String videoUrl = "www.bilibili.com/video/";
    private final String articleUrl = "www.bilibili.com/read/";
    private Gson gson = new Gson();

    public BiliLinkInfo() {
    }

    public boolean check(long id) {

        ArticleInfoBean articleInfoBean = gson.fromJson(
                MainActivity.getSourceCode(
                        "https://api.bilibili.com/x/article/viewinfo?id="
                                + id + "&mobi_app=pc&jsonp=jsonp"),
                ArticleInfoBean.class);

        VideoInfoBean videoInfoBean = gson.fromJson(
                MainActivity.getSourceCode(
                        "http://api.bilibili.com/archive_stat/stat?aid=" + id + "&type=jsonp"),
                VideoInfoBean.class);

        return false;
    }

}
