package com.roger.okhttpstudy.okhttp;

import android.text.TextUtils;

import com.google.common.collect.Maps;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpRequestUtils {

    /**
     * 定义请求客户端
     */
    private static OkHttpClient client = new OkHttpClient();

    /**
     * get 请求
     *
     * @param url 请求URL
     * @return
     * @throws Exception
     */
    public static void doGet(String url, HttpRequestListener listener) throws Exception {
        doGet(url, Maps.newHashMap(), listener);
    }


    /**
     * get 请求
     *
     * @param url   请求URL
     * @param query 携带参数参数
     * @return
     * @throws Exception
     */
    public static void doGet(String url, Map<String, Object> query, HttpRequestListener listener) {

        doGet(url, Maps.newHashMap(), query, listener);
    }

    /**
     * get 请求
     *
     * @param url    url
     * @param header 请求头参数
     * @param query  参数
     * @return
     */
    public static void doGet(String url, Map<String, Object> header,
                             Map<String, Object> query, HttpRequestListener listener) {

        // 创建一个请求 Builder
        Request.Builder builder = new Request.Builder();
        // 创建一个 request
        Request request = builder.url(url).build();

        // 创建一个 HttpUrl.Builder
        HttpUrl.Builder urlBuilder = request.url().newBuilder();
        // 创建一个 Headers.Builder
        Headers.Builder headerBuilder = request.headers().newBuilder();

        // 装载请求头参数
        for (Map.Entry<String, Object> next : header.entrySet()) {
            headerBuilder.add(next.getKey(), (String) next.getValue());
        }

        // 装载请求的参数
        for (Map.Entry<String, Object> next : query.entrySet()) {
            urlBuilder.addQueryParameter(next.getKey(), (String) next.getValue());
        }

//----------------------------------------------------------------------------
        // 设置自定义的 builder
        // 因为 get 请求的参数，是在 URL 后面追加  http://xxxx:8080/user?name=xxxx?sex=1
        builder.url(urlBuilder.build()).headers(headerBuilder.build());

        Call call = client.newCall(builder.build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                listener.onError(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        listener.onSuccess(response.body().string());
                    } else {
                        listener.onFail("handlerResponse body is null");
                    }
                } else {
                    listener.onFail("request fail errorcode is " + response.code());
                }
            }
        });
    }

    /**
     * post 请求， 请求参数 并且 携带文件上传
     *
     * @param url
     * @param header
     * @param parameter
     * @param file         文件
     * @param fileFormName 远程接口接收 file 的参数
     * @return
     * @throws Exception
     */
    public static void doPost(String url, Map<String, Object> header, Map<String, Object> parameter,
                              File file, String fileFormName, HttpRequestListener listener) {

        // 创建一个请求 Builder
        Request.Builder builder = new Request.Builder();
        // 创建一个 request
        Request request = builder.url(url).build();

        // 创建一个 Headers.Builder
        Headers.Builder headerBuilder = request.headers().newBuilder();

        // 装载请求头参数

        for (Map.Entry<String, Object> next : header.entrySet()) {
            headerBuilder.add(next.getKey(), (String) next.getValue());
        }

        // 或者 FormBody.create 方式，只适用于接口只接收文件流的情况
        // RequestBody requestBody = FormBody.create(MediaType.parse("application/octet-stream"), file);
//        MultipartBody.Builder requestBuilder = new MultipartBody.Builder();
        FormBody.Builder requestBuilder = new FormBody.Builder();

        // 状态请求参数
        for (Map.Entry<String, Object> next : parameter.entrySet()) {
            requestBuilder.add(next.getKey(), (String) next.getValue());
        }

//        if (null != file) {
//            // application/octet-stream
//            requestBuilder.addFormDataPart(!TextUtils.isEmpty(fileFormName) ? fileFormName : "file",
//                    file.getName(), RequestBody.create(file, MediaType.parse("application/octet-stream")));
//        }

        // 设置自定义的 builder
        builder.headers(headerBuilder.build()).post(requestBuilder.build());

        Call call = client.newCall(builder.build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                listener.onError(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        listener.onSuccess(response.body().string());
                    } else {
                        listener.onFail("handlerResponse body is null");
                    }
                } else {
                    listener.onFail("request fail errorcode is " + response.code());
                }
            }
        });
    }

    /**
     * post 请求， 请求参数 并且 携带文件上传二进制流
     *
     * @param url
     * @param header
     * @param parameter
     * @param fileName     文件名
     * @param fileByte     文件的二进制流
     * @param fileFormName 远程接口接收 file 的参数
     * @return
     * @throws Exception
     */
    public static void doPost(String url, Map<String, Object> header, Map<String, Object> parameter,
                              String fileName, byte[] fileByte, String fileFormName,
                              HttpRequestListener listener) {
        // 创建一个请求 Builder
        Request.Builder builder = new Request.Builder();
        // 创建一个 request
        Request request = builder.url(url).build();

        // 创建一个 Headers.Builder
        Headers.Builder headerBuilder = request.headers().newBuilder();

        // 装载请求头参数
        for (Map.Entry<String, Object> next : header.entrySet()) {
            headerBuilder.add(next.getKey(), (String) next.getValue());
        }

        MultipartBody.Builder requestBuilder = new MultipartBody.Builder();

        // 状态请求参数
        for (Map.Entry<String, Object> next : parameter.entrySet()) {
            requestBuilder.addFormDataPart(next.getKey(), (String) next.getValue());
        }

        if (fileByte.length > 0) {
            // application/octet-stream
            requestBuilder.addFormDataPart(!TextUtils.isEmpty(fileFormName) ? fileFormName : "file",
                    fileName, RequestBody.create(fileByte, MediaType.parse("application/octet-stream")));
        }

        // 设置自定义的 builder
        builder.headers(headerBuilder.build()).post(requestBuilder.build());

        Call call = client.newCall(builder.build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                listener.onError(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        listener.onSuccess(response.body().string());
                    } else {
                        listener.onFail("handlerResponse body is null");
                    }
                } else {
                    listener.onFail("request fail errorcode is " + response.code());
                }
            }
        });
    }


    /**
     * post 请求  携带文件上传
     *
     * @param url
     * @param file
     * @return
     * @throws Exception
     */
    public static void doPost(String url, File file, String fileFormName, HttpRequestListener listener) {
        doPost(url, Maps.newHashMap(), Maps.newHashMap(), file, fileFormName, listener);
    }

    /**
     * post 请求
     *
     * @param url
     * @param header    请求头
     * @param parameter 参数
     * @return
     * @throws Exception
     */
    public static void doPost(String url, Map<String, Object> header, Map<String, Object> parameter
            , HttpRequestListener listener) {
        doPost(url, header, parameter, null, null, listener);
    }

    /**
     * post 请求
     *
     * @param url
     * @param parameter 参数
     * @return
     * @throws Exception
     */
    public static void doPost(String url, Map<String, Object> parameter, HttpRequestListener listener) {
        doPost(url, Maps.newHashMap(), parameter, null, null, listener);
    }

    /**
     * JSON数据格式请求
     *
     * @param url
     * @param header
     * @param json
     * @return
     */
    private static void json(String url, Map<String, Object> header, String json, HttpRequestListener listener) {
        // 创建一个请求 Builder
        Request.Builder builder = new Request.Builder();
        // 创建一个 request
        Request request = builder.url(url).build();

        // 创建一个 Headers.Builder
        Headers.Builder headerBuilder = request.headers().newBuilder();

        // 装载请求头参数
        for (Map.Entry<String, Object> next : header.entrySet()) {
            headerBuilder.add(next.getKey(), (String) next.getValue());
        }

        // application/octet-stream
        RequestBody requestBody = FormBody.create(json, MediaType.parse("application/json"));

        // 设置自定义的 builder
        builder.headers(headerBuilder.build()).post(requestBody);

        Call call = client.newCall(builder.build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                listener.onError(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        listener.onSuccess(response.body().string());
                    } else {
                        listener.onFail("handlerResponse body is null");
                    }
                } else {
                    listener.onFail("request fail errorcode is " + response.code());
                }
            }
        });
    }

    /**
     * post请求  参数JSON格式
     *
     * @param url
     * @param header 请求头
     * @param json   JSON数据
     * @return
     * @throws IOException
     */
    public static void doPost(String url, Map<String, Object> header, String json, HttpRequestListener listener) {
        json(url, header, json, listener);
    }

    /**
     * post请求  参数JSON格式
     *
     * @param url
     * @param json JSON数据
     * @return
     * @throws IOException
     */
    public static void doPost(String url, String json, HttpRequestListener listener) {
        json(url, Maps.newHashMap(), json, listener);
    }
}
