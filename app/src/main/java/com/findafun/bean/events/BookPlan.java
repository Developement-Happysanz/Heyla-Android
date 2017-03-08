package com.findafun.bean.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Nandha on 11-12-2016.
 */

public class BookPlan implements Serializable{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("event_id")
    @Expose
    private String eventId;
    @SerializedName("seat_available")
    @Expose
    private String seatAvailable;
    @SerializedName("seat_plan")
    @Expose
    private String seatPlan;
    @SerializedName("seat_rate")
    @Expose
    private String seatRate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getSeatAvailable() {
        return seatAvailable;
    }

    public void setSeatAvailable(String seatAvailable) {
        this.seatAvailable = seatAvailable;
    }

    public String getSeatPlan() {
        return seatPlan;
    }

    public void setSeatPlan(String seatPlan) {
        this.seatPlan = seatPlan;
    }

    public String getSeatRate() {
        return seatRate;
    }

    public void setSeatRate(String seatRate) {
        this.seatRate = seatRate;
    }
}
