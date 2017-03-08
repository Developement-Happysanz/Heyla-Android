package com.findafun.serviceinterfaces;

import org.json.JSONObject;

/**
 * Created by Data Crawl 6 on 18-05-2016.
 */
public interface IStaticEventServiceListener {

    void onEventResponse(JSONObject response);

    void onEventError(String error);
}
