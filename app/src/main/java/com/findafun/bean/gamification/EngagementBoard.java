
package com.findafun.bean.gamification;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class EngagementBoard {

    @SerializedName("engagements_count")
    @Expose
    private String engagementsCount;
    @SerializedName("engagementsPoints")
    @Expose
    private String engagementsPoints;
    @SerializedName("dataArr")
    @Expose
    private List<Engagement> dataArr = new ArrayList<Engagement>();

    /**
     * 
     * @return
     *     The engagementsCount
     */
    public String getEngagementsCount() {
        return engagementsCount;
    }

    /**
     * 
     * @param engagementsCount
     *     The engagements_count
     */
    public void setEngagementsCount(String engagementsCount) {
        this.engagementsCount = engagementsCount;
    }

    /**
     * 
     * @return
     *     The engagementsPoints
     */
    public String getEngagementsPoints() {
        return engagementsPoints;
    }

    /**
     * 
     * @param engagementsPoints
     *     The engagementsPoints
     */
    public void setEngagementsPoints(String engagementsPoints) {
        this.engagementsPoints = engagementsPoints;
    }

    /**
     * 
     * @return
     *     The dataArr
     */
    public List<Engagement> getDataArr() {
        return dataArr;
    }

    /**
     * 
     * @param dataArr
     *     The dataArr
     */
    public void setDataArr(List<Engagement> dataArr) {
        this.dataArr = dataArr;
    }

    public int getAvailableEngagementCount(){
        return dataArr.size();

    }

    public Engagement getEngagementAtPos(int index){
        return dataArr.get(index);
    }

    public void clearEngagementList(){
        dataArr.clear();
    }

}
