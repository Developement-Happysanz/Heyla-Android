
package com.findafun.bean.gamification;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LeaderBoard {

    @SerializedName("user_image")
    @Expose
    private String userImageUrl;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("total_points")
    @Expose
    private String userPointDetail;

    private String rank;

    private String level_name;



    public String getLevel_name() {
        return level_name;
    }

    public void setLevel_name(String level_name) {
        this.level_name = level_name;
    }



    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    /**
     * 
     * @return
     *     The userImageUrl
     */
    public String getUserImageUrl() {
        return userImageUrl;
    }

    /**
     * 
     * @param userImageUrl
     *     The userImageUrl
     */
    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    /**
     * 
     * @return
     *     The userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 
     * @param userName
     *     The userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 
     * @return
     *     The userPointDetail
     */
    public String getUserPointDetail() {
        return userPointDetail;
    }

    /**
     * 
     * @param userPointDetail
     *     The userPointDetail
     */
    public void setUserPointDetail(String userPointDetail) {
        this.userPointDetail = userPointDetail;
    }

}
