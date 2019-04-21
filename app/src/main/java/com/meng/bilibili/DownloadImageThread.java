package com.meng.bilibili;

import android.content.*;
import android.graphics.*;
import android.widget.*;

import com.meng.bilibili.javaBean.BilibiliPersonInfo;

import java.io.*;
import java.net.*;
import com.google.gson.*;
import java.security.*;
import java.math.*;

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
        imageFile = new File(MainActivity.instence.mainDic + "group/" + getMD5(urlStr) + ".jpg");
        if (imageFile.exists()) {
            return;
        }
        downloadFile(urlStr);
    }

    private String getBilibiliHeadUrl(long uid) {
        try {
            URL url = new URL("https://api.myBilibili.com/x/space/acc/info?mid=" + uid + "&jsonp=jsonp");
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
	
	public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("md5");
            // 计算md5函数
            md.update(str.getBytes());
            System.out.println("aaaaaaaaaaaaaaa:"+str);
            System.out.println("aaaaaaaaaaaaaaa:"+md.digest());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
		  } catch (Exception e) {
            e.printStackTrace();
            return str;
		  }
	  }
}
