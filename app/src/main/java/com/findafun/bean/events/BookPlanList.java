package com.findafun.bean.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Nandha on 11-12-2016.
 */

public class BookPlanList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("plans")
    @Expose
    private ArrayList<BookPlan> plans = new ArrayList<BookPlan>();

    /**
     *
     * @return
     *     The count
     */
    public int getCount() {
        return count;
    }

    /**
     *
     * @param count
     *     The count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     *
     * @return
     *     The events
     */
    public ArrayList<BookPlan> getPlans() {
        return plans;
    }

    /**
     *
     * @param plans
     *     The events
     */
    public void setEvents(ArrayList<BookPlan> plans) {
        this.plans = plans;
    }

}
