package com.meng.biliv3.libs;

import com.meng.biliv3.activity.*;
import java.io.*;
import org.jsoup.*;
import java.nio.charset.*;

public class NetworkCacher {
	public static byte[] getNetPicture(String url) {
		File file=new File(MainActivity.instance.mainDic + "cache/" + new String(Hash.getMd5Instance().calculate(url.getBytes(StandardCharsets.UTF_8))).toUpperCase());
		if (file.exists()) {
			return Tools.FileTool.readBytes(file);
		} else {
			try {
				byte[] img = Jsoup.connect(url).ignoreContentType(true).execute().bodyAsBytes();
				FileOutputStream fos=new FileOutputStream(file);
				fos.write(img);
				fos.flush();
				fos.close();
				return img;
			} catch (Exception e) {
				return null;
			}
		}
	}

	public static String getNetJson(String url) {
		return getNetJson(url, null, null);
	}

	public static String getNetJson(String url, String cookie) {
		return getNetJson(url, cookie, null);
	}

	public static String getNetJson(String url, String cookie, String referer) {
		File file = new File(MainActivity.instance.mainDic + "cache/" + Hash.getMd5Instance().calculate(url + " " + cookie + " " + referer));
		if (file.exists()) {
			return Tools.FileTool.readString(file);
		} else {
			try {
				String json=Tools.Network.httpGet(url, cookie, referer);
				FileOutputStream fos=new FileOutputStream(file);
				fos.write(json.getBytes(StandardCharsets.UTF_8));
				fos.flush();
				fos.close();
				return json;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
