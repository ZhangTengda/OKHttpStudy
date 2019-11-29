package com.roger.okhttpstudy.okhttp;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtils {

    // 1.创建OkHttpClient对象
    private static OkHttpClient okHttpClient = new OkHttpClient();

    /**
     * 异步get请求
     *
     * @param url       请求URL
     * @param parameter 请求参数
     * @param header    请求Header
     * @param listener  请求返回的Listener
     */
    public static void doGet(String url, Map<String, Object> parameter, Map<String, Object> header,
                             final HttpRequestListener listener) {
        //2.创建Request对象，设置一个url地址（百度地址）,设置请求方式。
        Request.Builder builder = new Request.Builder();

        Request request = builder.url(url).build();

        HttpUrl.Builder urlBuilder = request.url().newBuilder();

        Headers.Builder headersBuilder = request.headers().newBuilder();

        // 装载请求的参数
        if (parameter != null) {
            for (Map.Entry<String, Object> nextEntry : parameter.entrySet()) {
                urlBuilder.addEncodedQueryParameter(nextEntry.getKey(), (String) nextEntry.getValue());
            }
        }

        if (header != null) {
            // 装载请求的Header
            for (Map.Entry<String, Object> nextEntry : header.entrySet()) {
                headersBuilder.add(nextEntry.getKey(), (String) nextEntry.getValue());
            }
        }

        builder.url(urlBuilder.build()).headers(headersBuilder.build());

        //3.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(builder.build());
        //4.请求加入调度，重写回调方法
        call.enqueue(new Callback() {
            //请求失败执行的方法
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                listener.onError(e.getMessage());
            }

            //请求成功执行的方法
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String data = response.body().string();
                    listener.onSuccess(data);
                } else {
                    listener.onFail(String.valueOf(response.code()));
                }
            }
        });
    }

    /**
     * 异步Post请求
     *
     * @param url       请求URL
     * @param parameter 请求参数
     * @param header    请求Header
     * @param listener  请求返回的Listener
     */
    public static void doPost(String url, Map<String, Object> parameter, Map<String, Object> header,
                              final HttpRequestListener listener) {
        // 创建一个请求 Builder
        Request.Builder builder = new Request.Builder();

        // 创建一个 Headers.Builder
        if (header != null) {
            // 装载请求头参数
            for (Map.Entry<String, Object> nextEntry : header.entrySet()) {
                builder.addHeader(nextEntry.getKey(), (String) nextEntry.getValue());
            }
        }

        FormBody.Builder requestBuilder = new FormBody.Builder();

        if (parameter != null) {
            // 状态请求参数
            for (Map.Entry<String, Object> nextEntry : parameter.entrySet()) {
                requestBuilder.add(nextEntry.getKey(), (String) nextEntry.getValue());
            }
        }

        // 设置自定义的 builder
        builder.post(requestBuilder.build()).url(url);
        Request request = builder.build();

        //3.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);
        //4.请求加入调度，重写回调方法
        call.enqueue(new Callback() {
            //请求失败执行的方法
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                listener.onError(e.getMessage());
                Log.d("roger", "error ++" + e.getMessage());
            }

            //请求成功执行的方法
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        String data = response.body().string();
                        Log.d("roger", data);
                        listener.onSuccess(data);
                    } else {
                        listener.onFail("handlerResponse body is null");
                    }
                } else {
                    listener.onFail("request fail errorcode is " + response.code());
                }
            }
        });
    }


}
