package com.roger.okhttpstudy.okhttp;

public interface HttpRequestListener {

    void onSuccess(String str);

    void onFail(String str);

    void onError(String error);
}
