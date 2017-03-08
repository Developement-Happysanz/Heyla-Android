
package com.findafun.bean.gamification;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PhotosBoard {

    @SerializedName("totalpoints")
    @Expose
    private String totalPoints;

    @SerializedName("photo_sharing_count")
    @Expose
    private String totalPhotos;

    @SerializedName("allPhotos")
    @Expose
    private List<PhotoDetail> allPhotos = new ArrayList<PhotoDetail>();

    /**
     * 
     * @return
     *     The totalPoints
     */
    public String getTotalPoints() {
        return totalPoints;
    }

    /**
     * 
     * @param totalPoints
     *     The totalPoints
     */
    public void setTotalPoints(String totalPoints) {
        this.totalPoints = totalPoints;
    }

    /**
     * 
     * @return
     *     The totalPhotos
     */
    public String getTotalPhotos() {
        return totalPhotos;
    }

    /**
     * 
     * @param totalPhotos
     *     The totalPhotos
     */
    public void setTotalPhotos(String totalPhotos) {
        this.totalPhotos = totalPhotos;
    }

    /**
     * 
     * @return
     *     The allPhotos
     */
    public List<PhotoDetail> getAllPhotos() {
        return allPhotos;
    }

    /**
     * 
     * @param allPhotos
     *     The allPhotos
     */
    public void setAllPhotos(List<PhotoDetail> allPhotos) {
        this.allPhotos = allPhotos;
    }

}
