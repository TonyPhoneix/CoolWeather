package com.example.administrator.coolweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.coolweather.R;
import com.example.administrator.coolweather.service.AutoUpdateService;
import com.example.administrator.coolweather.util.HttpCallbackListener;
import com.example.administrator.coolweather.util.HttpUtil;
import com.example.administrator.coolweather.util.Utility;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener {

    private String weatherCode;

    //显示状态
    TextView publishText;
    //地区
    TextView cityNameText;
    //显示当前温度
    TextView temperature;
    //天气类型
    TextView weather_type;
    //最高温度
    TextView high_temp;
    //最低温度
    TextView low_temp;
    //风向
    TextView wind_direction ;
    //生活贴士
    TextView cold_tip;
    //当前的日期
    TextView current_date;

    //当前的
    /**
     * 切换城市按钮
     */
    private Button switchCity;
    /**
     * 更新天气按钮
     */
    private Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        getSupportActionBar().hide();
        //初始化控件
        temperature = (TextView) findViewById(R.id.tmeperature_text);
        weather_type = (TextView) findViewById(R.id.weather_type);
        high_temp = (TextView) findViewById(R.id.high_temp);
        publishText = (TextView) findViewById(R.id.publishText);
        low_temp = (TextView) findViewById(R.id.low_temp);
        wind_direction= (TextView) findViewById(R.id.wind_direction);
        cold_tip = (TextView) findViewById(R.id.cold_tip);
        cityNameText = (TextView) findViewById(R.id.city_name);
        current_date = (TextView) findViewById(R.id.current_date);
        switchCity = (Button) findViewById(R.id.switch_city);
        switchCity.setOnClickListener(this);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        refreshWeather.setOnClickListener(this);
        String countryCode = getIntent().getStringExtra("country_code");
        if (!TextUtils.isEmpty(countryCode)) {
            publishText.setText("同步中...");
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countryCode);
            publishText.setText("");
        } else
            //没有县级代号是就直接显示本地天气
            showWeather();
    }

    /**
     * 响应点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switch_city:{
                Intent intent = new Intent(WeatherActivity.this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.refresh_weather:{
                publishText.setText("同步中...");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                queryWeatherInfo(weatherCode);
                publishText.setText("");
                break;
            }
            default:
                break;
        }
    }

    /**
     * 查询县城的天气码
     *
     * @param countryCode
     */
    private void queryWeatherCode(String countryCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countryCode + ".xml";
        System.out.println("xyz:" + "查询天气代码");
        queryFromServer(address, "countryCode",null);
    }

    /**
     * 查询天气代号所对应的天气。
     */
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://wthrcdn.etouch.cn/weather_mini?citykey=" + weatherCode;
        this.weatherCode = weatherCode;
        queryFromServer(address, "weatherCode",weatherCode);
    }

    /**
     *
     * @param adress
     * @param type
     * @param weatherCode
     */
    private void queryFromServer(final String adress, final String type, final String weatherCode) {
        HttpUtil.sendHttpRequest(adress, new HttpCallbackListener() {
            @Override
            public void finish(String response) {
                if (type.equals("countryCode")) {
                    if (!TextUtils.isEmpty(response)) {
                        String[] array = response.split("\\|");
                        System.out.println("xyz:" + array[1]);
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            System.out.println("xyz:" + response);
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if (type.equals("weatherCode")) {
                    Utility.handleWeatherResponse(WeatherActivity.this, response, weatherCode);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void error(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败...");
                    }
                });
            }
        });
    }

    /**
     * 显示天气信息
     */
    private void showWeather() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(sharedPreferences.getString("city_name", ""));
        temperature.setText(sharedPreferences.getString("wendu",""));
        cold_tip.setText(sharedPreferences.getString("cold_tip",""));
        wind_direction.setText(sharedPreferences.getString("fengxiang",""));
        high_temp.setText(sharedPreferences.getString("high",""));
        low_temp.setText(sharedPreferences.getString("low",""));
        current_date.setText(sharedPreferences.getString("date",""));
        weather_type.setText(sharedPreferences.getString("weather_type",""));
        cityNameText.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }
}
