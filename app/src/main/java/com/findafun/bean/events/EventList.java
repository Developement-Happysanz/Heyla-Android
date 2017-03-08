
package com.findafun.bean.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EventList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("events")
    @Expose
    private ArrayList<Event> events = new ArrayList<Event>();

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
    public ArrayList<Event> getEvents() {
        return events;
    }

    /**
     * 
     * @param events
     *     The events
     */
    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

}
