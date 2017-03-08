
package com.findafun.bean.gamification.alldetails;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class BookingList {

    @SerializedName("dataArr")
    @Expose
    private List<DataArr_> dataArr = new ArrayList<DataArr_>();

    /**
     * 
     * @return
     *     The dataArr
     */
    public List<DataArr_> getDataArr() {
        return dataArr;
    }

    /**
     * 
     * @param dataArr
     *     The dataArr
     */
    public void setDataArr(List<DataArr_> dataArr) {
        this.dataArr = dataArr;
    }

}
