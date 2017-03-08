package com.findafun.serviceinterfaces;

/**
 * Created by BXDC46 on 2/1/2016.
 */
public interface IServiceListener {
    void onSuccess(int resultCode, Object result);
    void onError(String erorr);
}
