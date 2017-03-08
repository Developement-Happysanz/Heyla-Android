
package com.findafun.bean.gamification;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckinsBoard {

    @SerializedName("checkin_count")
    @Expose
    private String checkinCount;

    @SerializedName("checkinPoints")
    @Expose
    private String checkinPoints;

    @SerializedName("dataArr")
    @Expose
    private List<Checkins> dataArr = new ArrayList<Checkins>();

    /**
     * 
     * @return
     *     The checkinCount
     */
    public String getCheckinCount() {
        return checkinCount;
    }

    /**
     * 
     * @param checkinCount
     *     The checkin_count
     */
    public void setCheckinCount(String checkinCount) {
        this.checkinCount = checkinCount;
    }

    /**
     * 
     * @return
     *     The checkinPoints
     */
    public String getCheckinPoints() {
        return checkinPoints;
    }

    /**
     * 
     * @param checkinPoints
     *     The checkinPoints
     */
    public void setCheckinPoints(String checkinPoints) {
        this.checkinPoints = checkinPoints;
    }

    /**
     * 
     * @return
     *     The dataArr
     */
    public List<Checkins> getDataArr() {
        return dataArr;
    }

    /**
     * 
     * @param dataArr
     *     The dataArr
     */
    public void setDataArr(List<Checkins> dataArr) {
        this.dataArr = dataArr;
    }

    public int getAvailableCount(){
        return dataArr.size();

    }

    public Checkins getAtPos(int index){
        return dataArr.get(index);
    }

    public void clearData(){
        dataArr.clear();
    }


}
