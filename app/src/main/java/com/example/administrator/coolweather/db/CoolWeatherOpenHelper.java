package com.example.administrator.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tony on 2016/2/26 0026.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {


    //省份建表语句
    public static final String CREATE_PROVINCE = "create table Province\n" +
            "(\n" +
            " id integer primary key autoincrement,\n" +
            " province_name text,\n" +
            " province_code text\n" +
            ");\n";

    //城市建表语句
    public static final String CREATE_CITY = "create table City\n" +
            "(\n" +
            " id integer primary key autoincrement,\n" +
            " city_name text,\n" +
            " city_code text,\n" +
            " province_id integer\n" +
            ");";

    //县城建表语句
    public static final String CREATE_COUNTRY = "create table Country\n" +
            "(\n" +
            "  id integer primary key autoincrement,\n" +
            "  country_name text,\n" +
            "  country_code text,\n" +
            "  city_id integer\n" +
            ");";


    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE); // 创建Province表
        db.execSQL(CREATE_CITY); // 创建City表
        db.execSQL(CREATE_COUNTRY); // 创建County表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
