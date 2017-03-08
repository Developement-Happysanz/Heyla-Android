
package com.findafun.bean.gamification.alldetails;


import com.findafun.bean.gamification.BookingsBoard;
import com.findafun.bean.gamification.CheckinsBoard;
import com.findafun.bean.gamification.EngagementBoard;
import com.findafun.bean.gamification.PhotosBoard;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AllDetailsBoard {

    @SerializedName("photoList")
    @Expose
    private PhotosBoard photoList;

    @SerializedName("checkinList")
    @Expose
    private CheckinsBoard checkinList;

    @SerializedName("bookingList")
    @Expose
    private BookingsBoard bookingList;

    @SerializedName("engagementsList")
    @Expose
    private EngagementBoard engagementsList;

    private boolean mFetchData = true;

    /**
     * 
     * @return
     *     The photoList
     */
    public PhotosBoard getPhotoList() {
        return photoList;
    }

    /**
     * 
     * @param photoList
     *     The photoList
     */
    public void setPhotoList(PhotosBoard photoList) {
        this.photoList = photoList;
    }

    /**
     * 
     * @return
     *     The checkinList
     */
    public CheckinsBoard getCheckinList() {
        return checkinList;
    }

    /**
     * 
     * @param checkinList
     *     The checkinList
     */
    public void setCheckinList(CheckinsBoard checkinList) {
        this.checkinList = checkinList;
    }

    /**
     * 
     * @return
     *     The bookingList
     */
    public BookingsBoard getBookingList() {
        return bookingList;
    }

    /**
     * 
     * @param bookingList
     *     The bookingList
     */
    public void setBookingList(BookingsBoard bookingList) {
        this.bookingList = bookingList;
    }

    /**
     * 
     * @return
     *     The engagementsList
     */
    public EngagementBoard getEngagementsList() {
        return engagementsList;
    }

    /**
     * 
     * @param engagementsList
     *     The engagementsList
     */
    public void setEngagementsList(EngagementBoard engagementsList) {
        this.engagementsList = engagementsList;
    }

    public void clearData(){
        mFetchData = true;

    }

    public boolean ismFetchData() {
        return mFetchData;
    }

    public void setmFetchData(boolean mFetchData) {
        this.mFetchData = mFetchData;
    }
}
