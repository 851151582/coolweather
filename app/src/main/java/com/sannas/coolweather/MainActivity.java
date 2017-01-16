package com.sannas.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sannas.coolweather.util.CloseActivitysUtill;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CloseActivitysUtill.add(this);

        /*
         *  一开始先从SharedPreference文件中读取缓存数据,如果不为null则说明之前已经请求过天气数据了，
         *  就不需要让用户再次选择城市，而是直接跳转但WeatherActivity即可
         */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getString("weather",null) != null){
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
