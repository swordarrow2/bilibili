package com.meng.sjfmd.libs;

import com.meng.sjfmd.activity.*;
import com.meng.sjfmd.javabean.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;
import com.meng.biliv3.activity.*;

public class VideoApi {
    private String cookie;
    private String csrf;
    private String mid;
    private String access_key;
    public String aid;
    public String bvid;
    private ArrayList<String> appHeaders = new ArrayList<>();
    private ArrayList<String> webHeaders = new ArrayList<String>();

    private VideoModel videoModel;

    public VideoApi(final String cookie, String csrf, String mid, String access_key, String aid, String bvid) {
        this.cookie = cookie;
        this.csrf = csrf;
        this.mid = mid;
        this.access_key = access_key;
        this.aid = aid;
        this.bvid = bvid;
        appHeaders = new ArrayList<String>(){{
				add("Cookie"); add(cookie);
				add("User-Agent"); add(MainActivity.instance.userAgent);
			}};
        webHeaders = new ArrayList<String>(){{
				add("Cookie"); add(cookie);
				add("Referer"); add("https://www.bilibili.com/anime");
				add("User-Agent"); add(MainActivity.instance.userAgent);
			}};
    }

    public VideoModel getVideoDetails() {
        try {
            String url = "https://app.bilibili.com/x/v2/view";
            String temp_per;
            if (!bvid.equals(""))
                temp_per = "access_key=" + access_key + "&appkey=" + ConfInfoApi.getConf("appkey") +
					"&build=" + ConfInfoApi.getConf("build") + "&bvid=" + bvid + "&mobi_app=" + ConfInfoApi.getConf("mobi_app") +
					"&plat=0&platform=" + ConfInfoApi.getConf("platform") + "&ts=" + (int) (System.currentTimeMillis() / 1000);
            else
                temp_per = "access_key=" + access_key + "&aid=" + aid + "&appkey=" + ConfInfoApi.getConf("appkey") +
					"&build=" + ConfInfoApi.getConf("build") + "&mobi_app=" + ConfInfoApi.getConf("mobi_app") +
					"&plat=0&platform=" + ConfInfoApi.getConf("platform") + "&ts=" + (int) (System.currentTimeMillis() / 1000);
            String sign = ConfInfoApi.calc_sign(temp_per, ConfInfoApi.getConf("app_secret"));
            JSONObject result = new JSONObject(Network.httpGet(url + "?" + temp_per + "&sign=" + sign));
            if (result.optInt("code") == 0) {
                videoModel = new VideoModel(result.optJSONObject("data"));
                this.aid = videoModel.video_aid;
                this.bvid = videoModel.video_bvid;
                return videoModel;
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }
}

