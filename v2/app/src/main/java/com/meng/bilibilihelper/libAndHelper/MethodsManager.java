package com.meng.bilibilihelper.libAndHelper;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class MethodsManager {
    private Context context;

    public MethodsManager(Context context) {
        this.context = context;
    }

    public void fileCopy(String src, String des) {
        //io流固定格式
        try {
            BufferedInputStream bis = null;
            bis = new BufferedInputStream(new FileInputStream(src));
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(des));
            int i = -1;//记录获取长度
            byte[] bt = new byte[2014];//缓冲区
            while ((i = bis.read(bt)) != -1) {
                bos.write(bt, 0, i);
            }
            bis.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
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

    public String readFileToString(File file) {
        try {
            Long filelength = file.length();
            byte[] filecontent = new byte[filelength.intValue()];
            FileInputStream in = null;
            in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
            return new String(filecontent, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public File newFile(String path, Runnable fileExist, Runnable fileNotExist) {
        File file = new File(path);
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
                if (fileNotExist != null) {
                    fileNotExist.run();
                }
            } else {
                if (fileExist != null) {
                    fileExist.run();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    private void deleteFiles(File folder) {
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
}
