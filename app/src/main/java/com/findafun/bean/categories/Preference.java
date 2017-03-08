
package com.findafun.bean.categories;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Preference {

    @SerializedName("category_id")
    @Expose
    private String categoryId;

    /**
     * 
     * @return
     *     The categoryId
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * 
     * @param categoryId
     *     The category_id
     */
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

}
