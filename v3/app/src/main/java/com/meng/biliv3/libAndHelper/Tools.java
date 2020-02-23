package com.meng.biliv3.libAndHelper;

import android.content.*;
import android.net.*;
import com.google.gson.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.activity.live.*;
import com.meng.biliv3.fragment.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.security.*;
import java.text.*;
import java.util.*;
import org.jsoup.*;

public class Tools {

	public static final String DEFAULT_ENCODING = "UTF-8";

	public static class BilibiliTool {

		public static void startGuaji(int posInAccountList) {
			Intent intentOne = new Intent(MainActivity.instance, GuaJiService.class);
			intentOne.putExtra("pos", posInAccountList);
			MainActivity.instance.startService(intentOne);
		}

		public static void sendLiveSign(String cookie) {
			Connection connection = Jsoup.connect("https://api.live.bilibili.com/sign/doSign");
			connection.userAgent(MainActivity.instance.userAgent)
                .headers(MainActivity.instance.liveHead)
                .ignoreContentType(true)
                .referrer("https://live.bilibili.com/" + new Random().nextInt() % 9721949)
                .cookies(Tools.Network.cookieToMap(cookie))
                .method(Connection.Method.GET);
			Connection.Response response=null;
			try {
				response = connection.execute();
			} catch (IOException e) {
				MainActivity.instance.showToast("连接出错");
				return;
			}	if (response.statusCode() != 200) {
				MainActivity.instance.showToast(String.valueOf(response.statusCode()));
			}
			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(response.body()).getAsJsonObject();
			MainActivity.instance.showToast(obj.get("message").getAsString());
		}

		public static void sendHotStrip(long myUid, long roomMasterUid, long roomID, int count, String cookie) {
			Connection connection = Jsoup.connect("http://api.live.bilibili.com/gift/v2/gift/send");
			String csrf = Tools.Network.cookieToMap(cookie).get("bili_jct");
			connection.userAgent(MainActivity.instance.userAgent)
                .headers(MainActivity.instance.liveHead)
                .ignoreContentType(true)
                .referrer("https://live.bilibili.com/" + roomID)
                .cookies(Tools.Network.cookieToMap(cookie))
                .method(Connection.Method.POST)
                .data("uid", String.valueOf(myUid))
                .data("gift_id", "1")
                .data("ruid", String.valueOf(roomMasterUid))
                .data("gift_num", String.valueOf(count))
                .data("coin_type", "silver")
                .data("bag_id", "0")
                .data("platform", "pc")
                .data("biz_code", "live")
                .data("biz_id", String.valueOf(roomID))
                .data("rnd", String.valueOf(System.currentTimeMillis() / 1000))
                .data("metadata", "")
                .data("price", "0")
                .data("csrf_token", csrf)
                .data("csrf", csrf)
                .data("visit_id", "");
			Connection.Response response=null;
			try {
				response = connection.execute();
			} catch (IOException e) {
				MainActivity.instance.showToast("连接出错");
				return;
			}
			if (response.statusCode() != 200) {
				MainActivity.instance.showToast(String.valueOf(response.statusCode()));
			}
			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(response.body()).getAsJsonObject();
			MainActivity.instance.showToast(obj.get("message").getAsString());
		}

		public static void followUser(String cookie, long UID) {
			Connection conn1 = Jsoup.connect("https://api.bilibili.com/x/relation/modify?cross_domain=true");
			conn1.userAgent(MainActivity.instance.userAgent)
                .headers(MainActivity.instance.mainHead)
                .ignoreContentType(true)
                .referrer("https://www.bilibili.com/video/av" + new Random().nextInt() % 47957369)
                .cookies(Tools.Network.cookieToMap(cookie))
                .method(Connection.Method.POST)
                .data("fid", String.valueOf(UID))
                .data("act", "1")
                .data("re_src", "122")
                .data("csrf", Tools.Network.cookieToMap(cookie).get("bili_jct"));
			Connection.Response res1=null;
			try {
				res1 = conn1.execute();
			} catch (IOException e) {
				MainActivity.instance.showToast("连接出错");
				return;
			}
			if (res1.statusCode() != 200) {
				MainActivity.instance.showToast(String.valueOf(res1.statusCode()));
				return;
			}
			JsonParser parser = new JsonParser();
			//JsonObject obj1 = parser.parse(res1.body()).getAsJsonObject();
			//MainActivity.instence.showToast(obj1.get("message").getAsString());
			Connection conn2 = Jsoup.connect("https://api.bilibili.com/x/relation/tags/addUsers?cross_domain=true");
			conn2.userAgent(MainActivity.instance.userAgent)
                .headers(MainActivity.instance.mainHead)
                .ignoreContentType(true)
                .referrer("https://www.bilibili.com/video/av" + new Random().nextInt() % 47957369)
                .cookies(Tools.Network.cookieToMap(cookie))
                .method(Connection.Method.POST)
                .data("fids", String.valueOf(UID))
                .data("tagids", "0")
                .data("csrf", Tools.Network.cookieToMap(cookie).get("bili_jct"));
			Connection.Response res2=null;
			try {
				res2 = conn2.execute();
			} catch (IOException e) {
				MainActivity.instance.showToast("连接出错");
				return;
			}
			if (res2.statusCode() != 200) {
				MainActivity.instance.showToast(String.valueOf(res2.statusCode()));
				return;
			}
			JsonObject obj2 = parser.parse(res2.body()).getAsJsonObject();
			MainActivity.instance.showToast(obj2.get("message").getAsString());
		}

		public static void sendCoin(int count, int AID, String cookie) {
			Connection connection = Jsoup.connect("https://api.bilibili.com/x/web-interface/coin/add");
			connection.userAgent(MainActivity.instance.userAgent)
                .headers(MainActivity.instance.mainHead)
                .ignoreContentType(true)
                .referrer("https://www.bilibili.com/video/av" + AID)
                .cookies(Tools.Network.cookieToMap(cookie))
                .method(Connection.Method.POST)
                .data("aid", String.valueOf(AID))
                .data("multiply", String.valueOf(count))
                .data("select_like", "0")
                .data("cross_domain", "true")
                .data("csrf", Tools.Network.cookieToMap(cookie).get("bili_jct"));
			Connection.Response response=null;
			try {
				response = connection.execute();
			} catch (IOException e) {
				MainActivity.instance.showToast("连接出错");
				return;
			}
			if (response.statusCode() != 200) {
				MainActivity.instance.showToast(String.valueOf(response.statusCode()));
			}
			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(response.body()).getAsJsonObject();
			MainActivity.instance.showToast(obj.get("message").getAsString());
		}

		public static void sendVideoJudge(String msg, int AID, String cookie) {
			Connection connection = Jsoup.connect("https://api.bilibili.com/x/v2/reply/add");
			connection.userAgent(MainActivity.instance.userAgent)
                .headers(MainActivity.instance.mainHead)
                .ignoreContentType(true)
                .referrer("https://www.bilibili.com/video/av" + AID)
                .cookies(Tools.Network.cookieToMap(cookie))
                .method(Connection.Method.POST)
                .data("oid", String.valueOf(AID))
                .data("type", "1")
                .data("message", msg)
                .data("jsonp", "jsonp")
                .data("csrf", Tools.Network.cookieToMap(cookie).get("bili_jct"));
			Connection.Response response=null;
			try {
				response = connection.execute();
			} catch (IOException e) {
				MainActivity.instance.showToast("连接出错");
				return;
			}
			if (response.statusCode() != 200) {
				MainActivity.instance.showToast(String.valueOf(response.statusCode()));
			}
			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(response.body()).getAsJsonObject();
			MainActivity.instance.showToast(obj.get("message").getAsString());
		}

		public static void sendLike(int AID, String cookie) {
			Connection connection = Jsoup.connect("https://api.bilibili.com/x/web-interface/archive/like");
			connection.userAgent(MainActivity.instance.userAgent)
                .headers(MainActivity.instance.mainHead)
                .ignoreContentType(true)
                .referrer("https://www.bilibili.com/video/av" + AID)
                .cookies(Tools.Network.cookieToMap(cookie))
                .method(Connection.Method.POST)
                .data("aid", String.valueOf(AID))
                .data("like", "1")
                .data("csrf", Tools.Network.cookieToMap(cookie).get("bili_jct"));

			Connection.Response response=null;
			try {
				response = connection.execute();
			} catch (IOException e) {
				MainActivity.instance.showToast("连接出错");
				return;
			}
			if (response.statusCode() != 200) {
				MainActivity.instance.showToast(String.valueOf(response.statusCode()));
			}
			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(response.body()).getAsJsonObject();
			MainActivity.instance.showToast(obj.get("message").getAsString());
		}

		public static void sendLiveDanmaku(String msg, String cookie, long roomId) {
			Connection.Response response = null;
			try {
				Connection connection = Jsoup.connect("http://api.live.bilibili.com/msg/send");
				String csrf = Tools.Network.cookieToMap(cookie).get("bili_jct");
				connection.userAgent(MainActivity.instance.userAgent)
					.headers(MainActivity.instance.liveHead)
					.ignoreContentType(true)
					.referrer("https://live.bilibili.com/" + roomId)
					.cookies(Tools.Network.cookieToMap(cookie))
					.method(Connection.Method.POST)
					.data("color", "16777215")
					.data("fontsize", "25")
					.data("msg", msg)
					.data("rnd", String.valueOf(System.currentTimeMillis() / 1000))
					.data("roomid", String.valueOf(roomId))
					.data("bubble", "0")
					.data("csrf_token", csrf)
					.data("csrf", csrf);
				response = connection.execute();
				if (response.statusCode() != 200) {
					MainActivity.instance.showToast(String.valueOf(response.statusCode()));
				}
				JsonParser parser = new JsonParser();
				JsonObject obj = parser.parse(response.body()).getAsJsonObject();
				switch (obj.get("code").getAsInt()) {
					case 0:
						if (!obj.get("message").getAsString().equals("")) {
							MainActivity.instance.showToast(obj.getAsJsonObject("message").getAsString());
						} else {
							MainActivity.instance.showToast(roomId + "已奶");
						}
						break;
					case 1990000:
						if (obj.get("message").getAsString().equals("risk")) {
							ConnectivityManager connMgr = (ConnectivityManager) MainActivity.instance.getSystemService(Context.CONNECTIVITY_SERVICE);
							NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
							if (wifiNetworkInfo.isConnected()) {
								Intent intent = new Intent(MainActivity.instance, LiveWebActivity.class);
								intent.putExtra("cookie", cookie);
								intent.putExtra("url", "https://live.bilibili.com/" + roomId);
								MainActivity.instance.startActivity(intent);
							} else {
								MainActivity.instance.showToast("需要在官方客户端进行账号风险验证");
							}
						}
						break;
					default:
						MainActivity.instance.showToast(response.body());
						break;
				}
			} catch (Exception e) {
				if (response != null) {
					MainActivity.instance.showToast(response.body());
				}
			}
		}
	}

	public static class FileTool {
		public static void deleteFiles(File folder) {
			File[] fs = folder.listFiles();
			for (File f : fs) {
				if (f.isDirectory()) {
					deleteFiles(f);
					f.delete();
				} else {
					f.delete();
				}
			}
		}
		public static String getFromAssets(String fileName) {
			try {
				InputStreamReader inputReader = new InputStreamReader(MainActivity.instance.getResources().getAssets().open(fileName));
				BufferedReader bufReader = new BufferedReader(inputReader);
				String line = "";
				StringBuilder Result = new StringBuilder();
				while ((line = bufReader.readLine()) != null)
					Result.append(line);
				return Result.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}
		public static void fileCopy(String src, String des) {
			try {
				BufferedInputStream bis = null;
				bis = new BufferedInputStream(new FileInputStream(src));
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(des));
				int i = -1;
				byte[] bt = new byte[2014];
				while ((i = bis.read(bt)) != -1) {
					bos.write(bt, 0, i);
				}
				bis.close();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public static String readString(String fileName) {
			return readString(new File(fileName));
		}
		public static String readString(File f) {
			String s = "{}";
			try {      
				if (!f.exists()) {
					f.createNewFile();
				}
				long filelength = f.length();
				byte[] filecontent = new byte[(int) filelength];
				FileInputStream in = new FileInputStream(f);
				in.read(filecontent);
				in.close();
				s = new String(filecontent, StandardCharsets.UTF_8);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return s;
		}
	}

	public static class Hash {
		public static String MD5(String str) {
			try {
				return MD5(str.getBytes());
			} catch (Exception e) {
				return null;
			}
		}

		public static String MD5(byte[] bs) {
			try {
				MessageDigest mdTemp = MessageDigest.getInstance("MD5");
				mdTemp.update(bs);
				return toHexString(mdTemp.digest());
			} catch (Exception e) {
				return null;
			}
		}

		public static String MD5(File file) {
			InputStream inputStream = null;
			try {
				inputStream = new FileInputStream(file);
				return MD5(inputStream);
			} catch (Exception e) {
				return null;
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		public static String MD5(InputStream inputStream) {
			try {
				MessageDigest mdTemp = MessageDigest.getInstance("MD5");
				byte[] buffer = new byte[1024];
				int numRead = 0;
				while ((numRead = inputStream.read(buffer)) > 0) {
					mdTemp.update(buffer, 0, numRead);
				}
				return toHexString(mdTemp.digest());
			} catch (Exception e) {
				return null;
			}
		}
		private static String toHexString(byte[] md) {
			char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
			int j = md.length;
			char str[] = new char[j * 2];
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[2 * i] = hexDigits[byte0 >>> 4 & 0xf];
				str[i * 2 + 1] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		}
	}

	public static class Network {
		public static Map<String, String> cookieToMap(String value) {
			Map<String, String> map = new HashMap<>();
			String[] values = value.split("; ");
			for (String val : values) {
				String[] vals = val.split("=");
				if (vals.length == 2) {
					map.put(vals[0], vals[1]);
				} else if (vals.length == 1) {
					map.put(vals[0], "");
				}
			}
			return map;
		}
		public static String getRealUrl(String surl) throws Exception {
			URL url = new URL(surl);
			URLConnection conn = url.openConnection();
			conn.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
			String nurl = conn.getURL().toString();
			System.out.println("realUrl" + nurl);
			in.close();
			return nurl;
		}
		public static String getSourceCode(String url) {
			return getSourceCode(url, null, null);
		}

		public static String getSourceCode(String url, String cookie) {
			return getSourceCode(url, cookie, null);
		}

		public static String getSourceCode(String url, String cookie, String refer) {
			Connection.Response response = null;
			Connection connection;
			try {
				connection = Jsoup.connect(url);
				if (cookie != null) {
					connection.cookies(cookieToMap(cookie));
				}
				if (refer != null) {
					connection.referrer(refer);
				}
				connection.userAgent(MainActivity.instance.userAgent);
				connection.ignoreContentType(true).method(Connection.Method.GET);
				response = connection.execute();
				if (response.statusCode() != 200) {
					MainActivity.instance.showToast(String.valueOf(response.statusCode()));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (response != null) {
				return response.body();
			}
			return "";
		}
	}

	public static class CQ {
		public static String getTime() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
		public static String getTime(long timeStamp) {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timeStamp));
		}
		public static String getDate() {
			return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		}
		public static String getDate(long timeStamp) {
			return new SimpleDateFormat("yyyy-MM-dd").format(new Date(timeStamp));
		}
	}

	public static class ArrayTool {
		public static byte[] mergeArray(byte[]... arrays) {
			int allLen=0;
			for (byte[] bs:arrays) {
				allLen += bs.length;
			}
			byte[] finalArray=new byte[allLen];
			int flag=0;
			for (byte[] byteArray:arrays) {
				for (int i=0;i < byteArray.length;++flag,++i) {
					finalArray[flag] = byteArray[i];
				}
			}
			return finalArray;
		}

		public static String[] mergeArray(String[]... arrays) {
			int allLen=0;
			for (String[] bs:arrays) {
				allLen += bs.length;
			}
			String[] finalArray=new String[allLen];
			int flag=0;
			for (String[] byteArray:arrays) {
				for (int i=0;i < byteArray.length;++flag,++i) {
					finalArray[flag] = byteArray[i];
				}
			}
			return finalArray;
		}
	}

	public static class BitConverterlittleEndian {
		public static byte[] getBytes(short s) {
			byte[] bs=new byte[2];
			bs[0] = (byte) ((s >> 0) & 0xff);
			bs[1] = (byte) ((s >> 8) & 0xff) ;
			return bs;	
		}

		public static byte[] getBytes(int i) {
			byte[] bs=new byte[4];
			bs[0] = (byte) ((i >> 0) & 0xff);
			bs[1] = (byte) ((i >> 8) & 0xff);
			bs[2] = (byte) ((i >> 16) & 0xff);
			bs[3] = (byte) ((i >> 24) & 0xff);
			return bs;	
		}

		public static byte[] getBytes(long l) {
			byte[] bs=new byte[8];
			bs[0] = (byte) ((l >> 0) & 0xff);
			bs[1] = (byte) ((l >> 8) & 0xff);
			bs[2] = (byte) ((l >> 16) & 0xff);
			bs[3] = (byte) ((l >> 24) & 0xff);
			bs[4] = (byte) ((l >> 32) & 0xff);
			bs[5] = (byte) ((l >> 40) & 0xff);
			bs[6] = (byte) ((l >> 48) & 0xff);
			bs[7] = (byte) ((l >> 56) & 0xff);
			return bs;
		}

		public static byte[] getBytes(float f) {
			return getBytes(Float.floatToIntBits(f));
		}

		public static byte[] getBytes(Double d) {
			return getBytes(Double.doubleToLongBits(d));
		}

		public static byte[] getBytes(String s) {
			try {
				return s.getBytes(DEFAULT_ENCODING);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}

		public static short toShort(byte[] data, int pos) {
			return (short) ((data[pos] & 0xff) << 0 | (data[pos + 1] & 0xff) << 8);
		}

		public static short toShort(byte[] data) {
			return toShort(data , 0);
		}

		public static int toInt(byte[] data, int pos) {
			return (data[pos] & 0xff) << 0 | (data[pos + 1] & 0xff) << 8 | (data[pos + 2] & 0xff) << 16 | (data[pos + 3] & 0xff) << 24;
		}

		public static int toInt(byte[] data) {
			return toInt(data, 0);
		}

		public static long toLong(byte[] data, int pos) {
			return ((data[pos] & 0xffL) << 0) | (data[pos + 1] & 0xffL) << 8 | (data[pos + 2] & 0xffL) << 16 | (data[pos + 3] & 0xffL) << 24 | (data[pos + 4] & 0xffL) << 32 | (data[pos + 5] & 0xffL) << 40 | (data[pos + 6] & 0xffL) << 48 | (data[pos + 7] & 0xffL) << 56;
		}

		public static long toLong(byte[] data) {
			return toLong(data , 0);
		}

		public static float toFloat(byte[] data, int pos) {
			return Float.intBitsToFloat(toInt(data, pos));
		}

		public static float toFloat(byte[] data) {
			return toFloat(data , 0);
		}

		public static double toDouble(byte[] data, int pos) {
			return Double.longBitsToDouble(toLong(data, pos));
		}

		public static double toDouble(byte[] data) {
			return toDouble(data , 0);
		}

		public static String toString(byte[] data, int pos, int byteCount) {
			try {
				return new String(data, pos, byteCount, DEFAULT_ENCODING);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}

		public static String toString(byte[] data) {
			return toString(data, 0, data.length);
		}
	}

	public static class Base64 {
		public static final byte[] encode(String str) {
			try {
				return encode(str.getBytes(DEFAULT_ENCODING));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}
		public static final byte[] encode(byte[] byteData) {
			if (byteData == null) { 
				throw new IllegalArgumentException("byteData cannot be null");
			}
			int iSrcIdx; 
			int iDestIdx; 
			byte[] byteDest = new byte[((byteData.length + 2) / 3) * 4];
			for (iSrcIdx = 0, iDestIdx = 0; iSrcIdx < byteData.length - 2; iSrcIdx += 3) {
				byteDest[iDestIdx++] = (byte) ((byteData[iSrcIdx] >>> 2) & 077);
				byteDest[iDestIdx++] = (byte) ((byteData[iSrcIdx + 1] >>> 4) & 017 | (byteData[iSrcIdx] << 4) & 077);
				byteDest[iDestIdx++] = (byte) ((byteData[iSrcIdx + 2] >>> 6) & 003 | (byteData[iSrcIdx + 1] << 2) & 077);
				byteDest[iDestIdx++] = (byte) (byteData[iSrcIdx + 2] & 077);
			}
			if (iSrcIdx < byteData.length) {
				byteDest[iDestIdx++] = (byte) ((byteData[iSrcIdx] >>> 2) & 077);
				if (iSrcIdx < byteData.length - 1) {
					byteDest[iDestIdx++] = (byte) ((byteData[iSrcIdx + 1] >>> 4) & 017 | (byteData[iSrcIdx] << 4) & 077);
					byteDest[iDestIdx++] = (byte) ((byteData[iSrcIdx + 1] << 2) & 077);
				} else {
					byteDest[iDestIdx++] = (byte) ((byteData[iSrcIdx] << 4) & 077);
				}
			}
			for (iSrcIdx = 0; iSrcIdx < iDestIdx; iSrcIdx++) {
				if (byteDest[iSrcIdx] < 26) {
					byteDest[iSrcIdx] = (byte) (byteDest[iSrcIdx] + 'A');
				} else if (byteDest[iSrcIdx] < 52) {
					byteDest[iSrcIdx] = (byte) (byteDest[iSrcIdx] + 'a' - 26);
				} else if (byteDest[iSrcIdx] < 62) {
					byteDest[iSrcIdx] = (byte) (byteDest[iSrcIdx] + '0' - 52);
				} else if (byteDest[iSrcIdx] < 63) {
					byteDest[iSrcIdx] = '+';
				} else {
					byteDest[iSrcIdx] = '/';
				}
			}
			for (; iSrcIdx < byteDest.length; iSrcIdx++) {
				byteDest[iSrcIdx] = '=';
			}
			return byteDest;
		}

		public final static byte[] decode(String str) throws IllegalArgumentException {
			byte[] byteData = null;
			try {
				byteData = str.getBytes(DEFAULT_ENCODING);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (byteData == null) { 
				throw new IllegalArgumentException("byteData cannot be null");
			}
			int iSrcIdx; 
			int reviSrcIdx; 
			int iDestIdx; 
			byte[] byteTemp = new byte[byteData.length];
			for (reviSrcIdx = byteData.length; reviSrcIdx - 1 > 0 && byteData[reviSrcIdx - 1] == '='; reviSrcIdx--) {
				; // do nothing. I'm just interested in value of reviSrcIdx
			}
			if (reviSrcIdx - 1 == 0)	{ 
				return null; 
			}
			byte byteDest[] = new byte[((reviSrcIdx * 3) / 4)];
			for (iSrcIdx = 0; iSrcIdx < reviSrcIdx; iSrcIdx++) {
				if (byteData[iSrcIdx] == '+') {
					byteTemp[iSrcIdx] = 62;
				} else if (byteData[iSrcIdx] == '/') {
					byteTemp[iSrcIdx] = 63;
				} else if (byteData[iSrcIdx] < '0' + 10) {
					byteTemp[iSrcIdx] = (byte) (byteData[iSrcIdx] + 52 - '0');
				} else if (byteData[iSrcIdx] < ('A' + 26)) {
					byteTemp[iSrcIdx] = (byte) (byteData[iSrcIdx] - 'A');
				}  else if (byteData[iSrcIdx] < 'a' + 26) {
					byteTemp[iSrcIdx] = (byte) (byteData[iSrcIdx] + 26 - 'a');
				}
			}
			for (iSrcIdx = 0, iDestIdx = 0; iSrcIdx < reviSrcIdx && iDestIdx < ((byteDest.length / 3) * 3); iSrcIdx += 4) {
				byteDest[iDestIdx++] = (byte) ((byteTemp[iSrcIdx] << 2) & 0xFC | (byteTemp[iSrcIdx + 1] >>> 4) & 0x03);
				byteDest[iDestIdx++] = (byte) ((byteTemp[iSrcIdx + 1] << 4) & 0xF0 | (byteTemp[iSrcIdx + 2] >>> 2) & 0x0F);
				byteDest[iDestIdx++] = (byte) ((byteTemp[iSrcIdx + 2] << 6) & 0xC0 | byteTemp[iSrcIdx + 3] & 0x3F);
			}
			if (iSrcIdx < reviSrcIdx) {
				if (iSrcIdx < reviSrcIdx - 2) {
					byteDest[iDestIdx++] = (byte) ((byteTemp[iSrcIdx] << 2) & 0xFC | (byteTemp[iSrcIdx + 1] >>> 4) & 0x03);
					byteDest[iDestIdx++] = (byte) ((byteTemp[iSrcIdx + 1] << 4) & 0xF0 | (byteTemp[iSrcIdx + 2] >>> 2) & 0x0F);
				} else if (iSrcIdx < reviSrcIdx - 1) {
					byteDest[iDestIdx++] = (byte) ((byteTemp[iSrcIdx] << 2) & 0xFC | (byteTemp[iSrcIdx + 1] >>> 4) & 0x03);
				}  else {
					throw new IllegalArgumentException("Warning: 1 input bytes left to process. This was not Base64 input");
				}
			}
			return byteDest;
		}
	}
}

