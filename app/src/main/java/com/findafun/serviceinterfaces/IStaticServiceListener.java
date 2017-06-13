package com.findafun.serviceinterfaces;

import org.json.JSONObject;

/**
 * Created by Admin on 12-06-2017.
 */

public interface IStaticServiceListener {

    void onStaticEventResponse(JSONObject response);

    void onStaticEventError(String error);
}
