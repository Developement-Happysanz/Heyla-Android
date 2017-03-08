package com.findafun.serviceinterfaces;

import org.json.JSONObject;

/**
 * Created by Data Crawl 6 on 25-04-2016.
 */
public interface IForgotPasswordServiceListener {

    void onForgotPassword(JSONObject response);

    void onForgotPasswordError(String error);
}
