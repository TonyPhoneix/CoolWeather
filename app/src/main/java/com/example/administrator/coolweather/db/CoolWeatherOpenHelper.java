package com.example.administrator.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tony on 2016/2/26 0026.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {


    //省份建表语句
    public static final String CREATE_PROVINCE ="create table Province(id integer primary key autoincrement,province_code text,province_name text);";
    //城市建表语句
    public static final String CREATE_CITY = "create table City(id integer primary key autoincrement, city_name text,city_code text, province_id integer);";

    //县城建表语句
    public static final String CREATE_COUNTRY = "create table Country( id integer primary key autoincrement,country_name text, country_code text,city_id integer);";


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
