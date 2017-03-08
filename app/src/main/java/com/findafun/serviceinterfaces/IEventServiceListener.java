package com.findafun.serviceinterfaces;


import org.json.JSONObject;

/**
 * Created by zahid.r on 10/30/2015.
 */
public interface IEventServiceListener {
    void onEventResponse(JSONObject response);

    void onEventError(String error);
}
