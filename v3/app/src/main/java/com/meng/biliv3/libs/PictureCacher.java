package com.meng.biliv3.libs;

import com.meng.biliv3.activity.*;
import java.io.*;
import org.jsoup.*;

public class PictureCacher {
	public static byte[] getNetPicture(final String url) {
		File file=new File(MainActivity.instance.mainDic + "cache/" + new String(Base64Converter.getInstance().encode(url)));
		if (file.exists()) {
			return Tools.FileTool.readBytes(MainActivity.instance.mainDic + "cache/" + new String(Base64Converter.getInstance().encode(url)));
		} else {
			try {
				Connection.Response response = Jsoup.connect(url).ignoreContentType(true).execute();
				final byte[] img = response.bodyAsBytes();
				MainActivity.instance.threadPool.execute(new Runnable(){

						@Override
						public void run() {
							try {
								FileOutputStream fos=new FileOutputStream(new File(MainActivity.instance.mainDic + "cache/" + new String(Base64Converter.getInstance().encode(url))));
								fos.write(img);
								fos.flush();
								fos.close();
							} catch (Exception e) {

							}
						}
					});
				return img;
			} catch (Exception e) {
				return null;
			}
		}
	}
}
