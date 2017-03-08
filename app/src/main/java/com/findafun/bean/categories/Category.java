package com.findafun.bean.categories;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("img_path")
    @Expose
    private String imgPath;
    @SerializedName("category_preference")
    @Expose
    private String categoryPreference;
    @SerializedName("size")
    @Expose
    private int size;

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The category
     */
    public String getCategory() {
        return category;
    }

    /**
     *
     * @param category
     * The category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     *
     * @return
     * The imgPath
     */
    public String getImgPath() {
        return imgPath;
    }

    /**
     *
     * @param imgPath
     * The img_path
     */
    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    /**
     *
     * @return
     * The categoryPreference
     */
    public String getCategoryPreference() {
        return categoryPreference;
    }

    /**
     *
     * @param categoryPreference
     * The category_preference
     */
    public void setCategoryPreference(String categoryPreference) {
        this.categoryPreference = categoryPreference;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}