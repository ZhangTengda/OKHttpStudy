package com.roger.okhttpstudy.okhttp;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * okHttp 请求类
 */

public class MyRequest {
    private static MyRequest myRequest;
    private Intent intentNetWork;
    public static boolean isShowAppUpdata = true;//是否需要显示更新提示框


    public synchronized static MyRequest getInstance() {
        if (myRequest == null)
            myRequest = new MyRequest();
        return myRequest;
    }

    private MyRequest() {
    }

    private static final String TAG = "MyRequest";
    private Handler handler;

    private RequestBody requestBody;

    public Call request(int method, String url, final Class clazz, final HttpRequestListener okHttpRequestListener) {
        handler = new Handler(Looper.getMainLooper());

//        final OkHttpClient okHttpClient = BaseRequest.getInstance().getOkHttpClient();
        final OkHttpClient okHttpClient = new OkHttpClient();

        Request request = null;
        Request.Builder builder = null;
        switch (method) {
            case 1:
                builder = new Request.Builder().tag("url:" + url).get().url(url);
                builder.addHeader("source", "android");
//                builder.addHeader("cancel", cancel + "");

                request = builder.build();
                break;
            case 2:
                if (requestBody == null) {
                    requestBody = createBody();
                }
                builder = new Request.Builder().post(requestBody).url(url);
//                builder.addHeader("cancel", cancel + "");
                request = builder.build();
                break;
        }

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull final Call call, @NonNull final IOException e) {
                if (e.toString().contains("closed")) {
                    //如果是主动取消的情况下
                } else if (okHttpRequestListener == null) {
                    return;
                }
                /*handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        okHttpRequestListener.onFail(call, e);
                        if (!NetWorkUtil.isOpenNoNetWork) {
                            Activity activity = MyApplication.appManager.currentActivity();
                            activity.startActivityForResult(new Intent(activity, NoNetWorkActivity.class), ForResultCode.NOT_NETWORK_TYPE);
                        }
                    }
                }, 0);*/
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (okHttpRequestListener != null){}
//                            okHttpRequestListener.onFail();
                    }
                }, 0);
            }

            @Override
            public void onResponse(@Nullable final Call call, @NonNull final Response response) throws IOException {
                if (okHttpRequestListener == null) {
                    return;
                }
                okHttpRequestListener.onSuccess(response.body().string());
//                handlerResponse(response, clazz, okHttpRequestListener, call);
            }
        });
        return call;
    }

    private void handlerResponse(@NonNull final Response response, Class clazz, final HttpRequestListener okHttpRequestListener, @Nullable final Call call) {
        try {
            if (response.isSuccessful()) {
                final ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    final String result = responseBody.string();
                    if (clazz == String.class) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                okHttpRequestListener.onSuccess(responseBody.toString());
                            }
                        }, 0);
                    } else {
                        okHttpRequestListener.onSuccess(responseBody.toString());
//                        final BaseRespBean baseRespBean = (BaseRespBean) new Gson().fromJson(result, clazz);
//                        try {
//                            if (RequestUtils.isAccessTokenInvalid(baseRespBean.getErrcode() + "")) {
//                                RequestUtils.reLogin(call, okHttpRequestListener);
//                                return;
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                RequestUtils.fail_Login = 0;
//                                okHttpRequestListener.onResponse(call, baseRespBean, result);
//                            }
//                        }, 0);
                    }
                } else {
                    okHttpRequestListener.onSuccess(responseBody.toString());
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            okHttpRequestListener.onFail(call, new Exception("handlerResponse body is null"));
//                        }
//                    }, 2000);
                }
            } else {
//                okHttpRequestListener.onFail();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        okHttpRequestListener.onFail(call, new Exception(String.valueOf(response.code())));
//                    }
//                }, 2000);
            }
        } catch (final Exception e) {
            e.printStackTrace();
            if (!e.getMessage().contains("Expected BEGIN_OBJECT but was STRING")) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        okHttpRequestListener.onFail();
                    }
                }, 2000);
            }
        }
    }

    /**
     * 主动关闭之前请求的同一个接口,通过tag进行判断是否是同一个，而call中存储的时https或者http接口
     */
    public void cancel() {
        cancel(null);
    }

    /**
     * 主动关闭之前请求的同一个接口,通过tag进行判断是否是同一个，而call中存储的时https或者http接口
     *
     * @param tag
     */
    public void cancel(Object tag) {
        try {
//            OkHttpClient okHttpClient = BaseRequest.getInstance().getOkHttpClient();
            final OkHttpClient okHttpClient = new OkHttpClient();
            for (Call call : okHttpClient.dispatcher().queuedCalls())
                if (tag == null)
                    call.cancel();
                else if (call.request().tag().toString().contains(tag.toString())) {
                    call.cancel();
                }
            for (Call call : okHttpClient.dispatcher().runningCalls()) {
                if (tag == null)
                    call.cancel();
                else if (call.request().tag().toString().contains(tag.toString())) {
                    call.cancel();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RequestBody createPublicBody() {
        return new FormBody.Builder().add("test", "test").build();
    }

    private RequestBody createBody() {
        return new FormBody.Builder().add("type", "top").add("key", "cad9b4a9fe342e5a3a9fec8fe4db6d2a").build();
    }

    public void putBody(String key, String value) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(key, value);
        requestBody = builder.build();
    }

    public void putFormBody(Map<String, String> formparam) {
        FormBody.Builder builder = new FormBody.Builder();
        Set<String> keySet = formparam.keySet();
        if (keySet == null) return;
        for (String key : keySet) {
            builder.add(key, formparam.get(key));
        }
        requestBody = builder.build();
    }

    public void createUploadFileBody(File file) {
        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addPart(Headers.of("Content-Disposition", "form-data; name=\"username\""), RequestBody.create(null, "HGR")).addPart(Headers.of("Content-Disposition",
                "form-data; name=\"file\"; filename=\"" + file.getName() + "\""), fileRequestBody).build();
    }

    public void createUploadSubvideoBody(File file) {
        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addPart(Headers.of("Content-Disposition", "form-data; name=\"username\""), RequestBody.create(null, "HGR")).addPart(Headers.of("Content-Disposition",
                "form-data; name=\"videourl\"; filename=\"" + file.getName() + "\""), fileRequestBody).build();
    }

    public void putJSONBody(String json) {
        requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
    }

    //投稿文件上传
    //语音
    public void createContributeUploadAudioBody(File file) {
        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                .addPart(Headers.of("Content-Disposition", "form-data; name=\"username\""), RequestBody.create(null, "HGR"))
//                .addPart(Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"" + file.getName() + "\""), fileRequestBody)
                .addFormDataPart("audio", file.getName(), fileRequestBody)
                .addFormDataPart("type", "voice")
                .build();
    }

    public void createContributeUploadImageBody(File file) {
        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                .addPart(Headers.of("Content-Disposition", "form-data; name=\"username\""), RequestBody.create(null, "HGR"))
//                .addPart(Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"" + file.getName() + "\""), fileRequestBody)
                .addFormDataPart("audio", file.getName(), fileRequestBody)
                .addFormDataPart("type", "cover")
                .build();
    }

    //投稿文件上传
    public void createContributeUploadVideoBody(File file) {
        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                .addPart(Headers.of("Content-Disposition", "form-data; name=\"username\""), RequestBody.create(null, "HGR"))
//                .addPart(Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"" + file.getName() + "\""), fileRequestBody)
                .addFormDataPart("file", file.getName(), fileRequestBody)
                .addFormDataPart("id", "null")
                .build();
    }

//    //投稿文件上传
//    public void createContributeUploadVideoBodyOnProgress(File file, FileProgressRequestBody.ProgressListener listener) {
//        FileProgressRequestBody fileRequestBody = new FileProgressRequestBody(file, "application/octet-stream", listener);
//        requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                .addFormDataPart("file", file.getName(), fileRequestBody)
//                .addFormDataPart("id", "null")
//                .build();
//    }


}
