package com.findafun.bean.gamification;

/**
 * Created by BXDC46 on 1/22/2016.
 */
public class Rewards {

    private String level_name;
    private String level_image_url;
    private int total_points;
    private int visa_count;
    private int rank;
    private int photo_sharing_count;
    private int promo_count;
    private int booking_count;
    private int checkin_count;
    private int engagements_count;
    private String pointsfornextrank;
    private String pointsfornextlevel;

    public String getPointsfornextlevel() {
        return pointsfornextlevel;
    }

    public String getPointsfornextrank() {
        return pointsfornextrank;
    }

    public void setPointsfornextlevel(String pointsfornextlevel) {
        this.pointsfornextlevel = pointsfornextlevel;
    }

    public void setPointsfornextrank(String pointsfornextrank) {
        this.pointsfornextrank = pointsfornextrank;
    }

    public int getCheckInCount() {
        return checkin_count;
    }

    public int getEngagementsCount() {
        return engagements_count;
    }

    public int getEventBookingCount() {
        return booking_count;
    }

    public int getReferralPromoCount() {
        return promo_count;
    }

    public int getLeaderboardPosition() {
        return rank;
    }

    public int getPhotoSharingCount() {
        return photo_sharing_count;
    }

    public int getTotalPoint() {
        return total_points;
    }

    public int getVisaCount() {
        return visa_count;
    }

    public String getLevelImageUrl() {
        return level_image_url;
    }

    public String getLevelName() {
        return level_name;
    }

    public void setCheckInCount(int checkInCount) {
        this.checkin_count = checkInCount;
    }

    public void setEngagementsCount(int engagementsCount) {
        this.engagements_count = engagementsCount;
    }

    public void setEventBookingCount(int eventBookingCount) {
        this.booking_count = eventBookingCount;
    }

    public void setReferralPromoCount(int referralPromoCount) {
        this.promo_count = referralPromoCount;
    }

    public void setLeaderboardPosition(int leaderboardPosition) {
        this.rank = leaderboardPosition;
    }

    public void setLevelImageUrl(String levelImageUrl) {
        this.level_image_url = levelImageUrl;
    }

    public void setLevelName(String levelName) {
        this.level_name = levelName;
    }

    public void setPhotoSharingCount(int photoSharingCount) {
        this.photo_sharing_count = photoSharingCount;
    }

    public void setTotalPoint(int totalPoint) {
        this.total_points = totalPoint;
    }

    public void setVisaCount(int visaCount) {
        this.visa_count = visaCount;
    }

}
