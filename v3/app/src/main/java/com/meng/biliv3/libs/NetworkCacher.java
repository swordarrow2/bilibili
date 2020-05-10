package com.meng.biliv3.libs;

import com.meng.biliv3.activity.*;
import java.io.*;
import org.jsoup.*;

public class NetworkCacher {
	public static byte[] getNetPicture(final String url) {
		File file=new File(MainActivity.instance.mainDic + "cache/" + new String(Base64Converter.getInstance().encode(url)));
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
}
