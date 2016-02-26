package com.example.administrator.coolweather.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.coolweather.db.CoolWeatherOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony on 2016/2/26 0026.
 */
public class CoolWeatherDB  {

    //数据库版本
    private static final int VERSION = 1;
    //数据库名称
    private static final String DATABASE_NAME = "cool_weather";

    private CoolWeatherOpenHelper coolWeatherOpenHelper;

    private static CoolWeatherDB coolWeatherDB;

    private SQLiteDatabase database;

    private CoolWeatherDB(Context context){
        //初始化类
        coolWeatherOpenHelper = new CoolWeatherOpenHelper(context,DATABASE_NAME,null,VERSION);
        database = coolWeatherOpenHelper.getWritableDatabase();
    }

    public synchronized static CoolWeatherDB getInstance(Context context){
        if (coolWeatherDB==null){
            coolWeatherDB =  new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }
    /**
     * 将Province实例存储到数据库。
     */
    public void saveProvince(Province province){
       if (province!=null){
           ContentValues values = new ContentValues();
           values.put("province_name",province.getProvinceName());
           values.put("province_code",province.getProvinceCode());
           database.insert("Province",null,values);
       }
    }

    /**
     * 从数据库读取全国所有的省份信息。
     */
    public List<Province> loadProvinces(){
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = database.query("Province",null,null,null,null,null,null);
        while (cursor.moveToNext()){
            Province province = new Province();
            province.setId(cursor.getInt(cursor.getColumnIndex("id")));
            province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
            province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
            list.add(province);
        }
        return list;
    }
    /**
     * 将City实例存储到数据库。
     */
    public void saveCity(City city){
        if (city!=null){
            ContentValues values = new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id ",city.getProvince_id());
            database.insert("City",null,values);
        }
    }
    /**
     * 从数据库读取某省下所有的城市信息。
     */
    public List<City> loadCities(){
        List<City> list = new ArrayList<City>();
        Cursor cursor = database.query("City",null,null,null,null,null,null);
        while (cursor.moveToNext()){
            City city = new City();
            city.setId(cursor.getInt(cursor.getColumnIndex("id")));
            city.setCityName(cursor.getString(cursor.getColumnIndex("City_name")));
            city.setCityCode(cursor.getString(cursor.getColumnIndex("City_code")));
            city.setProvince_id(cursor.getInt(cursor.getColumnIndex("province_id")));
            list.add(city);
        }
        return list;
    }
    /**
     * 将Country实例存储到数据库。
     * ca
     */
    public void saveCountry(Country country){
        if (country!=null){
            ContentValues values = new ContentValues();
            values.put("country_name",country.getCountryName());
            values.put("country_code",country.getCountryCode());
            values.put("city_id ",country.getCity_id());
            database.insert("Country",null,values);
        }
    }
    /**
     * 从数据库读取某省下所有的城市信息。
     */
    public List<Country> loadCountries(){
        List<Country> list = new ArrayList<Country>();
        Cursor cursor = database.query("Country",null,null,null,null,null,null);
        while (cursor.moveToNext()){
            Country country = new Country();
            country.setId(cursor.getInt(cursor.getColumnIndex("id")));
            country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
            country.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
            country.setCity_id(cursor.getInt(cursor.getColumnIndex("city_id")));
            list.add(country);
        }
        return list;
    }
}
