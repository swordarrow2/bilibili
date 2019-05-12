package com.meng.bilibilihelper.libAndHelper;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.os.*;

public class DataBaseHelper {
    //表名
    private static final String TABLE_NAME = "captcha";
    //表的主键
    private static final String KEY_ID = "_id";
    private static final String RESULT = "result";
    private static final String PICBASE64 = "picBase64";
    //创建一个表的sql语句
    private static final String sql = "create table "
            + TABLE_NAME + "( " + KEY_ID
            + " integer primary key autoincrement,"
            + RESULT + " text,"
            + PICBASE64 + " text)";
    private static SQLiteOpenHelper sqLiteOpenHelper;

    public static void init(Context context) {
        sqLiteOpenHelper = new SQLiteOpenHelper(context, Environment.getExternalStorageDirectory() + "/" + "bilibiliHelper.db", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(sql);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		db.execSQL("drop table notes if exits");
                onCreate(db);
            }
        };
    }

    //插入一条数据
    public static long insertData(String pic,String result) {
        if(updateData(pic, result) <= 0) {
            SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(PICBASE64, pic);
            values.put(RESULT, result);
            return db.insert(TABLE_NAME, null, values);
        }
                return 1;
        }

        public static String searchResult(String picBase64){
            Cursor cursor=query();
            if (cursor!=null&&cursor.getCount()>0) {
                while (cursor.moveToNext()) { //columindex代表列的索引
                    if(cursor.getString(1).equals(picBase64)){
                        return  cursor.getString(2);
                    }
                }
            }
            return null;
        }

    //查询数据，返回一个Cursor
    public static Cursor query() {
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        return db.rawQuery("select * from picBase64", null);
    }

    //根据主键删除某条记录
  //  public static void deleteData(String pixivId) {
  //      SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
  //      db.delete("captcha", "picBase64=?", new String[]{pixivId});
  //  }

   public static long updateData(String pic,String result) {
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PICBASE64,pic);
        values.put(RESULT, result);
        return db.update("captcha", values, PICBASE64 + "=?", new String[]{pic});
    }
}

