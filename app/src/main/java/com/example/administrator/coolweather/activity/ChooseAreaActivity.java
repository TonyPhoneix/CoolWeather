package com.example.administrator.coolweather.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.coolweather.R;
import com.example.administrator.coolweather.model.City;
import com.example.administrator.coolweather.model.CoolWeatherDB;
import com.example.administrator.coolweather.model.Country;
import com.example.administrator.coolweather.model.Province;
import com.example.administrator.coolweather.util.HttpCallbackListener;
import com.example.administrator.coolweather.util.HttpUtil;
import com.example.administrator.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony on 2016/2/27 0027.
 */
public class ChooseAreaActivity extends AppCompatActivity {

    //状态码
    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVLE_CITY = 1;
    private static final int LEVLE_COUNTRY = 2;
    //当前的状态码
    private int currentlevel;

    //控件
    private TextView title_text;
    private ListView list_item;
    private ProgressDialog progressDialog;
    //listView
    private ArrayAdapter<String> adapter;

    //listView需要的数据
    List<String> dataList = new ArrayList<String>();

    //操作数据库
    private CoolWeatherDB weatherDB;

    //省份集合
    List<Province> provinces;

    //城市集合
    List<City> cities;

    //农村集合
    List<Country> countries;

    //选中的省份
    Province selectedProvince;

    //选中的城市
    City selectedCity;

    //选中的乡村
    Country selectedCountry;

    /**
     * 是否从WeatherActivity中跳转过来。
     */
    private boolean isFromWeatherActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity",false);
        if (preferences.getBoolean("city_selected",false)&&!isFromWeatherActivity){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.choose_area);
        getSupportActionBar().hide();
        title_text = (TextView)findViewById(R.id.title_text);
        list_item = (ListView)findViewById(R.id.list_item);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        list_item.setAdapter(adapter);
        weatherDB = CoolWeatherDB.getInstance(this);

        list_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentlevel==LEVEL_PROVINCE){
                    selectedProvince = provinces.get(position);
                    queryCities();
                }else if (currentlevel == LEVLE_CITY){
                    selectedCity = cities.get(position);
                    queryCounties();
                }else if (currentlevel==LEVLE_COUNTRY){
                    String countryCode = countries.get(position).getCountryCode();
                    Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("country_code",countryCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces(); // 加载省级数据
    }

    //先从数据库里查找所有的省份，如果为空，再去服务器查找
    private void queryProvinces() {
        provinces =  weatherDB.loadProvinces();
        if (provinces.size()>0){
            dataList.clear();
            for (Province province : provinces){
                String province_name = province.getProvinceName();
                dataList.add(province_name);
            }
            adapter.notifyDataSetChanged();
            list_item.setSelection(0);
            title_text.setText("中国");
            currentlevel = LEVEL_PROVINCE;
        }else {
            queryFromServer(null, "province");
        }
    }
    //先从数据库里查找所有的县市，如果为空，再去服务器查找
    private void queryCities(){
        cities = weatherDB.loadCities(selectedProvince.getId());
        if (cities.size()>0){
            dataList.clear();
            for (City city : cities){
                String city_name = city.getCityName();
                dataList.add(city_name);
            }
            adapter.notifyDataSetChanged();
            list_item.setSelection(0);
            title_text.setText(selectedProvince.getProvinceName());
            currentlevel = LEVLE_CITY;
        }else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    //先从数据库里查找所有的乡村，如果为空，再去服务器查找
    private void queryCounties(){
        countries = weatherDB.loadCountries(selectedCity.getId());
        if (countries.size()>0){
            dataList.clear();
            for (Country country : countries){
                String country_name = country.getCountryName();
                dataList.add(country_name);
            }
            adapter.notifyDataSetChanged();
            list_item.setSelection(0);
            title_text.setText(selectedCity.getCityName());
            currentlevel = LEVLE_COUNTRY;
        }else {
            queryFromServer(selectedCity.getCityCode(),"country");
        }
    }

    /**
     * 根据传入的代号和传入的类型来查询数据
     * @param code
     * @param type
     */
    private void queryFromServer(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city" + code +".xml";
        }else
            address = "http://www.weather.com.cn/data/list3/city.xml";
            showProgressDialog();
            HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                @Override
                public void finish(String response) {
                    boolean result = false;
                    if (type.equals("province")){
                        result = Utility.handleProvincesResponse(weatherDB,response);
                    }else if (type.equals("city")){
                        result = Utility.handleCitiesResponse(weatherDB,response,selectedProvince.getId());
                    }else if (type.equals("country")){
                        result = Utility.handleCountiesResponse(weatherDB,response,selectedCity.getId());
                    }
                    if (result){
                        // 通过runOnUiThread()方法回到主线程处理逻辑
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                                if (type.equals("province")){
                                    queryProvinces();
                                }else if (type.equals("city")){
                                    queryCities();
                                }else if (type.equals("country")){
                                    queryCounties();
                                }
                            }
                        });
                    }
                }

                @Override
                public void error(Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
    }

    //县市ProgressDialog
    private void showProgressDialog() {
        if (progressDialog==null){
            progressDialog = new ProgressDialog(ChooseAreaActivity.this);
            progressDialog.setMessage("正在加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    //关闭ProgressDialog
    private void closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    /**
     *捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出。
     */
    @Override
    public void onBackPressed() {
        if (currentlevel==LEVEL_PROVINCE){
            finish();
        }else if (currentlevel==LEVLE_CITY){
            queryProvinces();
        }else if (currentlevel==LEVLE_COUNTRY){
            queryCities();
        }else if(isFromWeatherActivity){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
