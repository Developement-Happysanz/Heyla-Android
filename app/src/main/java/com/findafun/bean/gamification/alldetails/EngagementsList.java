
package com.findafun.bean.gamification.alldetails;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class EngagementsList {

    @SerializedName("dataArr")
    @Expose
    private List<DataArr__> dataArr = new ArrayList<DataArr__>();

    /**
     * 
     * @return
     *     The dataArr
     */
    public List<DataArr__> getDataArr() {
        return dataArr;
    }

    /**
     * 
     * @param dataArr
     *     The dataArr
     */
    public void setDataArr(List<DataArr__> dataArr) {
        this.dataArr = dataArr;
    }

}
