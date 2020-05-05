package com.meng.biliv3.libs;

import com.meng.biliv3.activity.*;
import java.io.*;
import java.nio.charset.*;
import org.jsoup.*;

public class NetworkCacher {
	public static byte[] getNetPicture(final String url) {
		File file=new File(MainActivity.instance.mainDic + "cache/" + new String(Base64Converter.getInstance().encode(url)));
		if (file.exists()) {
			return Tools.FileTool.readBytes(file);
		} else {
			try {
				final byte[] img = Jsoup.connect(url).ignoreContentType(true).execute().bodyAsBytes();
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

	public static String getNetJson(final String url) {
		File file=new File(MainActivity.instance.mainDic + "cache/" + new String(Base64Converter.getInstance().encode(url)));
		if (file.exists()) {
			return Tools.FileTool.readString(file);
		} else {
			try {
				final String str = Jsoup.connect(url).ignoreContentType(true).execute().body();
				FileOutputStream fos=new FileOutputStream(file);
				fos.write(str.getBytes(StandardCharsets.UTF_8));
				fos.flush();
				fos.close();
				return str;
			} catch (Exception e) {
				return null;
			}
		}
	}

	public static String getNetJson(String url, int exceedHours) {
		File file=new File(MainActivity.instance.mainDic + "cache/" + new String(Base64Converter.getInstance().encode(url)));
		if (System.currentTimeMillis() - file.lastModified() > exceedHours * 60 * 60 * 1000) {
			file.delete();
		}
		try {
			final String str = Jsoup.connect(url).ignoreContentType(true).execute().body();
			FileOutputStream fos=new FileOutputStream(file);
			fos.write(str.getBytes(StandardCharsets.UTF_8));
			fos.flush();
			fos.close();
			return str;
		} catch (Exception e) {
			return null;
		}
	}

}
