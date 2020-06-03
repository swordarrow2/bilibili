package com.meng.sjfmd.activity;

/**
 * Created by liupe on 2018/10/6.
 * 各位大佬好
 */

import android.util.*;
import com.meng.*;
import com.meng.biliv3.activity.*;
import com.meng.sjfmd.libs.*;
import com.meng.sjfmd.result.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.security.interfaces.*;
import java.security.spec.*;
import java.util.*;
import javax.crypto.*;
import org.json.*;
import org.jsoup.*;
import org.jsoup.helper.*;

public class UserLoginApi {
	//  private String oauthKey;
    private static String sid = String.valueOf(Math.round(Math.random() * 100000000));
	// private static ArrayList<String> defaultHeaders = new ArrayList<String>();
//	static{
//		defaultHeaders = new ArrayList<String>(){
//			{
//				add("User-Agent");
//				add(ConfInfoApi.USER_AGENT_OWN);
//			}
//		};
//	}

    private UserLoginApi() {

    }

//    public Bitmap getLoginQR() throws Exception {
//        ArrayList<String> headers = new ArrayList<String>()
//        {{
//				add("Cookie"); add("sid=" + sid);
//				add("User-Agent"); add(ConfInfoApi.USER_AGENT_OWN);
//			}};
//
//        String url = "https://passport.bilibili.com/qrcode/getLoginUrl";
//        JSONObject loginUrlJson = new JSONObject(NetWorkUtil.get(url, headers).body().string()).getJSONObject("data");
//        oauthKey = (String) loginUrlJson.get("oauthKey");
//        return QRCodeUtil.createQRCodeBitmap((String) loginUrlJson.get("url"), 120, 120);
//    }

//    public static Response getLoginState() throws IOException {
//        return NetWorkUtil.post("https://passport.bilibili.com/qrcode/getLoginInfo", "oauthKey=" + oauthKey + "&gourl=https://www.bilibili.com/", defaultHeaders);
//    }

	public static LoginResult login(String name, String pw) {
		try {
            JSONObject value = getRequestKey();
            String key = value.getString("key");
            String hash = value.getString("hash");
            pw = encrypt(hash + pw, key);
            name = URLEncoder.encode(name, "UTF-8");
            pw = URLEncoder.encode(pw, "UTF-8");
			String temp_params = "appkey=" + ConfInfoApi.getBConf("appkey") + "&build=" + ConfInfoApi.getBConf("build") +
				"&captcha=&mobi_app=" + ConfInfoApi.getBConf("mobi_app") + "&password=" + pw + "&platform=" +
				ConfInfoApi.getBConf("platform") + "&ts=" + (int) (System.currentTimeMillis() / 1000) + "&username=" + name;
            String sign = ConfInfoApi.calc_sign(temp_params, ConfInfoApi.getBConf("app_secret"));
			Connection connection=Jsoup.connect("https://passport.bilibili.com/api/oauth2/login");
			connection.method(Connection.Method.POST)
				.header("Referer", "http://www.bilibili.com/")
				.header("Cookie", "sid=" + sid)
				.header("User-Agent", MainActivity.instance.userAgent)
				.ignoreContentType(true)
				.requestBody(temp_params + "&sign=" + sign);
			return GSON.fromJson(connection.execute().body(), LoginResult.class);
        } catch (Exception e) {
			SJFException sjfe=new SJFException();
			sjfe.initCause(e);
			throw sjfe;
		}
	}

	public static String getCookie(String access_key) {
        try {
            String temp_params = "access_key=" + access_key + "&appkey=" + ConfInfoApi.getBConf("appkey") +
				"&build=" + ConfInfoApi.getBConf("build") + "&gourl=" + URLEncoder.encode("https://account.bilibili.com/account/home", "utf-8") +
				"&mobi_app=" + ConfInfoApi.getBConf("mobi_app") + "&platform=" + ConfInfoApi.getBConf("platform") +
				"&ts=" + (int) (System.currentTimeMillis() / 1000);
            String sign = ConfInfoApi.calc_sign(temp_params, ConfInfoApi.getBConf("app_secret"));
			String url = "https://passport.bilibili.com/api/login/sso?" + temp_params + "&sign=" + sign;
			Connection connection=Jsoup.connect(url);
			connection.method(Connection.Method.GET)
				.followRedirects(false)
				.header("Content-type", "application/x-www-form-urlencoded; charset=UTF-8")
				.header("Cookie", "sid=" + sid)
				.header("user-agent", MainActivity.instance.userAgent)
				.header("Referer", "http://www.bilibili.com/")
				.header("Connection", "Keep-Alive")
				.ignoreContentType(true);
			Connection.Response res=connection.execute();
			List<String> cookieList = res.headers("set-cookie");
            StringBuilder cookies = new StringBuilder();
            for (int i = 0; i < cookieList.size(); i++) {
                String cookie = cookieList.get(i).split("; ")[0];
				cookies.append(i == 0 ? "" : "; ").append(cookie);
            }
			return cookies.toString();
        } catch (IOException e) {
			SJFException sjfe=new SJFException();
			sjfe.initCause(e);
            throw sjfe;
        }
    }

    private static String encrypt(String str, String key) throws Exception {
        key = key.replace("-----BEGIN PUBLIC KEY-----", "");
        key = key.replace("-----END PUBLIC KEY-----", "");
        byte[] decoded = Base64.decode(key, Base64.DEFAULT);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return Base64.encodeToString(cipher.doFinal(str.getBytes("UTF-8")), Base64.NO_PADDING);
    }

    private static JSONObject getRequestKey() throws IOException {
        try {
			String url = "https://passport.bilibili.com/api/oauth2/getKey";
            String temp_per = "appkey=" + ConfInfoApi.getBConf("appkey");
            String sign = ConfInfoApi.calc_sign(temp_per, ConfInfoApi.getBConf("app_secret"));
			Connection connection=Jsoup.connect(url);
			connection.method(Connection.Method.POST)
				.header("Referer", "http://www.bilibili.com/")
				.header("User-Agent", MainActivity.instance.userAgent)
				.ignoreContentType(true)
				.requestBody("appkey=" + ConfInfoApi.getBConf("appkey") + "&sign=" + sign);
			Connection.Response response=connection.execute();
            sid = response.header("set-header");
            return new JSONObject(response.body()).getJSONObject("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static String getAccessKey(final String cookie) throws IOException {
//        try {
//            String url = "https://passport.bilibili.com/login/app/third";
//            String temp_per = "api=http://link.acg.tv/forum.php&appkey=27eb53fc9058f8c3&sign=67ec798004373253d60114caaad89a8c";
//			Connection connection=Jsoup.connect(url + "?" + temp_per);
//			connection.method(Connection.Method.GET)
//				.header("Cookie", cookie)
//				.header("Host", "passport.bilibili.com")
//				.header("Referer", "http://www.bilibili.com/")
//				.header("User-Agent", MainActivity.instance.userAgent)
//				.ignoreContentType(true);
//			Connection.Response response=connection.execute();	
//			url = new JSONObject(response.body()).getJSONObject("data").getString("confirm_uri");
//			Connection connection2=Jsoup.connect(url);
//			connection2.method(Connection.Method.GET)
//				.followRedirects(false)
//				.header("Cookie", cookie)
//				.header("User-Agent", MainActivity.instance.userAgent)
//				.ignoreContentType(true);
//			Connection.Response response2=connection2.execute();
//			String url_location = response2.header("location");
//            return url_location.substring(url_location.indexOf("access_key=") + 11, url_location.indexOf("&", url_location.indexOf("access_key=")));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

//    public String getOauthKey() {
//        return oauthKey;
//    }
}
