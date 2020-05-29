package com.meng.sjfmd.activity;

/**
 * 被 luern0313 创建于 2019/10/13.
 */

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.*;
import okhttp3.*;

public class NetWorkUtil {
    public static Response get(String url) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).readTimeout(15, TimeUnit.SECONDS).build();
        Request.Builder requestb = new Request.Builder().url(url).header("Referer", "https://www.bilibili.com/").addHeader("Accept", "*/*").addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        Request request = requestb.build();
        Response response = client.newCall(request).execute();

        if (response.isSuccessful())
            return response;
        return null;
    }

    public static Response get(String url, ArrayList<String> headers) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).readTimeout(15, TimeUnit.SECONDS).build();
        Request.Builder requestBuilder = new Request.Builder().url(url).get();
        for (int i = 0; i < headers.size(); i += 2)
            requestBuilder = requestBuilder.addHeader(headers.get(i), headers.get(i + 1));
        Request request = requestBuilder.build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful())
            return response;
        return null;
    }

    public static Response post(String url, String data, ArrayList<String> headers) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).readTimeout(15, TimeUnit.SECONDS).build();
        RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), data);
        Request.Builder requestBuilder = new Request.Builder().url(url).post(body);
        for (int i = 0; i < headers.size(); i += 2)
            requestBuilder = requestBuilder.addHeader(headers.get(i), headers.get(i + 1));
        Request request = requestBuilder.build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) return response;
        return null;
    }

    public static byte[] readStream(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    public static byte[] uncompress(byte[] inputByte) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(inputByte.length);
        try {
            Inflater inflater = new Inflater(true);
            inflater.setInput(inputByte);
            byte[] buffer = new byte[4 * 1024];
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] output = outputStream.toByteArray();
        outputStream.close();
        return output;
    }
}

