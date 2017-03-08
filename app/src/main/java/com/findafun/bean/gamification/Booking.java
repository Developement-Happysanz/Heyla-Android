
package com.findafun.bean.gamification;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Booking {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("event_name")
    @Expose
    private String eventName;
    @SerializedName("ticket_count")
    @Expose
    private String ticketCount;

    /**
     * 
     * @return
     *     The date
     */
    public String getDate() {
        return date;
    }

    /**
     * 
     * @param date
     *     The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * 
     * @return
     *     The imageUrl
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * 
     * @param imageUrl
     *     The image_url
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * 
     * @return
     *     The eventName
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * 
     * @param eventName
     *     The event_name
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * 
     * @return
     *     The ticketCount
     */
    public String getTicketCount() {
        return ticketCount;
    }

    /**
     * 
     * @param ticketCount
     *     The ticket_count
     */
    public void setTicketCount(String ticketCount) {
        this.ticketCount = ticketCount;
    }

}
