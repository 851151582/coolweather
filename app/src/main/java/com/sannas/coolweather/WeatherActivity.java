package com.sannas.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.sannas.coolweather.gson.Forecast;
import com.sannas.coolweather.gson.Weather;
import com.sannas.coolweather.util.CloseActivitysUtill;
import com.sannas.coolweather.util.HttpUtil;
import com.sannas.coolweather.util.Utility;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/*
 *  在活动中请求天气数据，以及将数据展示到界面上
 */
public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private Button back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        CloseActivitysUtill.add(this);

        //初始化各控件
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        back_button = (Button) findViewById(R.id.back_button);
        back_button.setVisibility(View.VISIBLE);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //当点击上方的按钮时，会自动清除sp中保存的weather数据，同时返回到选择地区界面
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.remove("weather");
                editor.commit();
                Intent intent = new Intent(WeatherActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if(weatherString != null){
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }else{
            //无缓存时去服务器查询天气
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);         //设置不可见，请求数据时先将ScrollView进行隐藏
            requestWeather(weatherId);
        }
    }

    /*
     *  根据天气id请求城市天气信息
     */
    public void requestWeather(final String weatherId){
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" +
                weatherId + "&key=a55c7e2a76c6433692aeb3ce495ed728";     //http://console.heweather.com/my/service查询
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            //发送请求会自动开子线程进行
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {     //将当前线程切换到主线程
                    @Override
                    public void run() {
                        //将请求成功返回的天气数据存储起来,并显示
                        if(weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);        //显示天气数据
                        }
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /*
     *  处理并展示Weather 实体类中的数据
     */
    private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(Forecast forecast : weather.forecastList){      //处理未来几天的天气信息
            //在循环中动态加载forecast_item.xml布局并设置响应的数据
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dataText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dataText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if(weather.aqi != null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度： " + weather.suggestion.comfort.info;
        String carWash = "洗车指数： " + weather.suggestion.carWash.info;
        String sport = "运动建议： " + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);         //加载好数据后使其变为可见
    }

    /**
     *  返回键点击事件
     *  当点击系统返回键时，直接关闭所有Activity,退出软件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //点击返回键关闭所有Activity,退出软件
        if(keyCode == KeyEvent.KEYCODE_BACK ){
            CloseActivitysUtill.close();
        }
        return super.onKeyDown(keyCode, event);
    }
}
