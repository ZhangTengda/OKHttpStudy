package com.roger.okhttpstudy;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.roger.okhttpstudy.okhttp.HttpRequestListener;
import com.roger.okhttpstudy.okhttp.HttpUtils;
import com.roger.okhttpstudy.okhttp.OkHttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String AppKey = "cad9b4a9fe342e5a3a9fec8fe4db6d2a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.text1).setOnClickListener(this);
        findViewById(R.id.text2).setOnClickListener(this);
        findViewById(R.id.text3).setOnClickListener(this);
        findViewById(R.id.text4).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text1:
                getOne();

                break;

            case R.id.text2:

                getTwo();
                break;

            case R.id.text3:

                postOne();
                break;

            case R.id.text4:
                postTwo();
                break;
        }
    }


    private void getOne() {

        Map<String, Object> params = new HashMap<>();
        params.put("type", "top");
        params.put("key", AppKey);
        OkHttpRequestUtils.doGet("http://v.juhe.cn/toutiao/index", params, new HttpRequestListener() {
            @Override
            public void onSuccess(String str) {
                Log.d("roger", "GetOne Success == " + str);
            }

            @Override
            public void onFail(String str) {
                Log.d("roger", "GetONe onFail == " + str);
            }

            @Override
            public void onError(String error) {
                Log.d("roger", "GetOne onError == " + error);
            }
        });
    }


    private void getThree() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "top");
        params.put("key", AppKey);
        OkHttpRequestUtils.doPost("http://v.juhe.cn/toutiao/index", params, new HttpRequestListener() {
            @Override
            public void onSuccess(String str) {
                Log.d("roger", "GetONe onSuccess == " + str);
            }

            @Override
            public void onFail(String str) {
                Log.d("roger", "GetONe onSuccess == " + str);
            }

            @Override
            public void onError(String error) {
                Log.d("roger", "GetONe onSuccess == " + error);
            }
        });

    }

    private void getTwo() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "top");
        params.put("key", AppKey);
        HttpUtils.doGet("http://v.juhe.cn/toutiao/index", params, null, new HttpRequestListener() {
            @Override
            public void onSuccess(String str) {
                Log.d("roger", "getTwo onSuccess == " + str);
            }

            @Override
            public void onFail(String str) {
                Log.d("roger", "getTwo onFail == " + str);
            }

            @Override
            public void onError(String error) {
                Log.d("roger", "getTwo onError == " + error);
            }

        });
    }

    private void postOne() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "top");
        params.put("key", AppKey);


        HttpUtils.doPost("http://v.juhe.cn/toutiao/index", params, null, new HttpRequestListener() {
            @Override
            public void onSuccess(String str) {
                Log.d("roger", "postOne onSuccess == " + str);
            }

            @Override
            public void onFail(String str) {
                Log.d("roger", "postOne onFail == " + str);
            }

            @Override
            public void onError(String error) {
                Log.d("roger", "postOne onError == " + error);
            }
        });
    }

    private void postTwo() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "top");
        params.put("key", AppKey);
        OkHttpRequestUtils.doPost("http://v.juhe.cn/toutiao/index", params, new HttpRequestListener() {
            @Override
            public void onSuccess(String str) {
                Log.d("roger", "postTwo onSuccess" + str);
            }

            @Override
            public void onFail(String str) {
                Log.d("roger", "postTwo onFail" + str);
            }

            @Override
            public void onError(String error) {
                Log.d("roger", "postTwo onError" + error);

            }
        });
    }
}
