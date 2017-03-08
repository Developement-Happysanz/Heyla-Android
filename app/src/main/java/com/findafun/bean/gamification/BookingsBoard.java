
package com.findafun.bean.gamification;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class BookingsBoard {

    @SerializedName("booking_count")
    @Expose
    private String bookingCount;

    @SerializedName("bookingPoints")
    @Expose
    private String bookingPoints;

    @SerializedName("dataArr")
    @Expose
    private List<Booking> dataArr = new ArrayList<Booking>();

    /**
     * 
     * @return
     *     The bookingCount
     */
    public String getBookingCount() {
        return bookingCount;
    }

    /**
     * 
     * @param bookingCount
     *     The booking_count
     */
    public void setBookingCount(String bookingCount) {
        this.bookingCount = bookingCount;
    }

    /**
     * 
     * @return
     *     The bookingPoints
     */
    public String getBookingPoints() {
        return bookingPoints;
    }

    /**
     * 
     * @param bookingPoints
     *     The bookingPoints
     */
    public void setBookingPoints(String bookingPoints) {
        this.bookingPoints = bookingPoints;
    }

    /**
     * 
     * @return
     *     The dataArr
     */
    public List<Booking> getDataArr() {
        return dataArr;
    }

    /**
     * 
     * @param dataArr
     *     The dataArr
     */
    public void setDataArr(List<Booking> dataArr) {
        this.dataArr = dataArr;
    }



    public int getAvailableCount(){
        return dataArr.size();

    }

    public Booking getAtPos(int index){
        return dataArr.get(index);
    }

    public void clearData(){
        dataArr.clear();
    }

}
