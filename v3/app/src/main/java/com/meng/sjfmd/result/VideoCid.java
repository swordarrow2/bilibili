package com.meng.sjfmd.result;

import android.graphics.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import org.json.*;
import com.meng.sjfmd.libs.*;

public class VideoCid {
    private String cookie;
    private String csrf;
    private String mid;
    private String aid;
    private String cid;

    private JSONObject playUrlJson;
    private String playUrl;

    public VideoCid(String cookie, String csrf, String mid, String aid, String cid) {
        this.cookie = cookie;
        this.csrf = csrf;
        this.mid = mid;
        this.aid = aid;
        this.cid = cid;
    }

    public void connectionVideoUrl() {
        try {
            playUrlJson = new JSONObject(Network.httpGet("https://api.bilibili.com/x/player/playurl?avid=" + aid + "&cid=" + cid + "&qn=16&type=mp4"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getVideoUrl() {
        try {
            playUrl = playUrlJson.getJSONObject("data").getJSONArray("durl").getJSONObject(0).getString("url");
            return playUrl;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String[] getVideoBackupUrl() {
        try {
            JSONArray backUpUrl = playUrlJson.getJSONObject("data").getJSONArray("durl").getJSONObject(0).getJSONArray("backup_url");
            ArrayList<String> backUpUrlList = new ArrayList<>();
            for (int i = 0; i < backUpUrl.length(); i++)
                backUpUrlList.add(backUpUrl.optString(i));
            return backUpUrlList.toArray(new String[]{});
        } catch (JSONException e) {
            e.printStackTrace();
            return new String[]{};
        }
    }

    public String getDanmakuUrl() {
        return "https://comment.bilibili.com/" + cid + ".xml";
    }

}

