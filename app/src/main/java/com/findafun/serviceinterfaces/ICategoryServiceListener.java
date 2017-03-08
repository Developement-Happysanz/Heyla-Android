package com.findafun.serviceinterfaces;


import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by zahid.r on 10/30/2015.
 */
public interface ICategoryServiceListener {
    void onCategoryResponse(JSONArray response);
    void onCategoryError(String error);

    void onSetCategoryResponse(JSONObject response);
    void onSetCategoryError(String error);
}
