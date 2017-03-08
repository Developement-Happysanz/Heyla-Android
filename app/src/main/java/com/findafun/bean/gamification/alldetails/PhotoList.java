
package com.findafun.bean.gamification.alldetails;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PhotoList {

    @SerializedName("allPhotos")
    @Expose
    private List<Object> allPhotos = new ArrayList<Object>();

    /**
     * 
     * @return
     *     The allPhotos
     */
    public List<Object> getAllPhotos() {
        return allPhotos;
    }

    /**
     * 
     * @param allPhotos
     *     The allPhotos
     */
    public void setAllPhotos(List<Object> allPhotos) {
        this.allPhotos = allPhotos;
    }

}
