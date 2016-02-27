package com.example.administrator.coolweather.util;

/**
 * Created by Tony on 2016/2/27 0027.
 */
public interface HttpCallbackListener {
    void finish(String response);

    void error(Exception e);
}
