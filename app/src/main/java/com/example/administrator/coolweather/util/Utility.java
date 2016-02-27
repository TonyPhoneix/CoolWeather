package com.example.administrator.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.administrator.coolweather.model.City;
import com.example.administrator.coolweather.model.CoolWeatherDB;
import com.example.administrator.coolweather.model.Country;
import com.example.administrator.coolweather.model.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tony on 2016/2/27 0027.
 */
public class Utility {
    //将所有省份存入数据库
    public static boolean handleProvincesResponse(CoolWeatherDB weatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
                String[] provinces = response.split(",");
                if (provinces!=null&&provinces.length>0){
                for (String p : provinces){
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    weatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    //将某个省份下的县市存入数据库
    public static boolean handleCitiesResponse(CoolWeatherDB weatherDB, String response,int province_id){
                if (!TextUtils.isEmpty(response)) {
                    String[] cities = response.split(",");
                    if (cities != null && cities.length > 0) {
                        for (String c : cities) {
                            String[] array = c.split("\\|");
                            City city = new City();
                            city.setProvince_id(province_id);
                            city.setCityCode(array[0]);
                            city.setCityName(array[1]);
                            weatherDB.saveCity(city);
                        }
                        return true;
                    }
        }
        return false;
    }

    //将某个县市下的乡村存入数据库
    public static boolean handleCountiesResponse(CoolWeatherDB weatherDB, String response, int city_id){

        if (!TextUtils.isEmpty(response)) {
            String[] countries = response.split(",");
            if (countries != null && countries.length > 0) {
                for (String coun : countries) {
                    String[] array = coun.split("\\|");
                    Country country = new Country();
                    country.setCity_id(city_id);
                    country.setCountryCode(array[0]);
                    country.setCountryName(array[1]);
                    weatherDB.saveCountry(country);
                }
                return true;
            }
        }
        return false;
    }

    //解析服务器返回的数据，并存入本地
    public static void handleWeatherResponse(Context context, String response,String weatherCode){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            jsonObject = jsonObject.getJSONObject("data");
            //城市信息
            String cityName = jsonObject.getString("city");
            //当前的温度
            String wendu = jsonObject.getString("wendu");
            //生活提示
            String cold_tip = jsonObject.getString("ganmao");
            //天气数组
            JSONArray jsonArray = jsonObject.getJSONArray("forecast");
            //获取第一天
            JSONObject weatherInfo = jsonArray.getJSONObject(0);
            //TODO 修改城市代码
            String fengxiang = weatherInfo.getString("fengxiang");
            String high = weatherInfo.getString("high");
            String low = weatherInfo.getString("low");
            String date = weatherInfo.getString("date");
            String weather_type = weatherInfo.getString("type");
            saveWeatherInfo(context,cityName,wendu,cold_tip,fengxiang,high,low,date,weather_type,weatherCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 将解析出来的数据存到本地
     * @param context
     * @param cityName
     * @param wendu
     * @param cold_tip
     * @param fengxiang
     * @param high
     * @param low
     * @param date
     */
    private static void saveWeatherInfo(Context context, String cityName, String wendu, String cold_tip, String fengxiang, String high, String low, String date,String weather_type,String weatherCode) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("wendu",wendu);
        editor.putString("cold_tip",cold_tip);
        editor.putString("fengxiang",fengxiang);
        editor.putString("high",high);
        editor.putString("low",low);
        editor.putString("date",date);
        editor.putString("weather_type",weather_type);
        editor.putString("weatherCode",weatherCode);
        editor.commit();
    }
}
