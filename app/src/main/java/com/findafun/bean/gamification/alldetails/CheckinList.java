
package com.findafun.bean.gamification.alldetails;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class CheckinList {

    @SerializedName("dataArr")
    @Expose
    private List<DataArr> dataArr = new ArrayList<DataArr>();

    /**
     * 
     * @return
     *     The dataArr
     */
    public List<DataArr> getDataArr() {
        return dataArr;
    }

    /**
     * 
     * @param dataArr
     *     The dataArr
     */
    public void setDataArr(List<DataArr> dataArr) {
        this.dataArr = dataArr;
    }

}
