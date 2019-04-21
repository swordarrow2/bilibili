package com.meng.bilibili;

import android.content.*;
import android.graphics.*;
import android.widget.*;

import com.google.gson.*;
import com.meng.bilibili.javaBean.bilibili.user.BilibiliPersonInfo;

import java.io.*;
import java.net.*;

public class DownloadImageThread extends Thread {
    private ImageView imageView;
    private String urlStr = "";
    private File imageFile;
    private Context context;

    public DownloadImageThread(Context context, ImageView imageView, String urlStr) {
        this.context = context;
        this.imageView = imageView;
        this.urlStr = urlStr;
    }

    @Override
    public void run() {
        imageFile = new File(MainActivity.instence.mainDic + "group/" + id + ".jpg");
        if (imageFile.exists()) {
            return;
        }
        downloadFile(urlStr);
    }

    private String getBilibiliHeadUrl(long uid) {
        try {
            URL url = new URL("https://api.bilibili.com/x/space/acc/info?mid=" + uid + "&jsonp=jsonp");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            BilibiliPersonInfo bilibiliPersonInfoJavaBean = new Gson().fromJson(stringBuilder.toString(), BilibiliPersonInfo.class);
            return bilibiliPersonInfoJavaBean.data.face;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void downloadFile(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
            InputStream is = connection.getInputStream();
            FileOutputStream fos = new FileOutputStream(imageFile);
            byte buf[] = new byte[4096];
            int len = 0;
            while ((len = is.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }
            is.close();
            connection.disconnect();
            ((MainActivity) context).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    imageView.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
