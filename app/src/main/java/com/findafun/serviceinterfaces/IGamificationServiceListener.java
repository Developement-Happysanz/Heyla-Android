package com.findafun.serviceinterfaces;

/**
 * Created by BXDC46 on 1/22/2016.
 */
public interface IGamificationServiceListener {
    void onSuccess(int resultCode, Object result);
    void onError(String erorr);
}
