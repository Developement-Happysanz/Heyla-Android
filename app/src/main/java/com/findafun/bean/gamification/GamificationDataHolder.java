package com.findafun.bean.gamification;

import android.util.Log;

import com.findafun.bean.events.Event;
import com.findafun.bean.gamification.alldetails.AllDetailsBoard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by BXDC46 on 1/23/2016.
 */
public class GamificationDataHolder {
    private static GamificationDataHolder ourInstance = new GamificationDataHolder();

    public static GamificationDataHolder getInstance() {
        return ourInstance;
    }
    private Rewards mRewards = null;
    private List<LeaderBoard> mLeaderboardList = new ArrayList<LeaderBoard>();
    private Event mCurrentEvent = null;
    private EngagementBoard mEngagementBoardDdetails = new EngagementBoard();
    private BookingsBoard mBookingBoard = new BookingsBoard();
    private CheckinsBoard mCheckinsBoard = new CheckinsBoard();

    //photos GamificationData
    private List<String> mPhotoDates = new ArrayList<String>();
    private HashMap<String,List<String>> mImageAlbum = new HashMap<String,List<String>>();
    private String mTotalPhotoPoints = null;
    private String mPhotosBoardTotalPhotos= null;

    //store bookmarked events
    private Set<String> mBookmarkedEvents = new HashSet<String>();

    public void addBookmarkedEvent(String id){
        mBookmarkedEvents.add(id);
    }

    public  void removeBookmark(String id){
        if(mBookmarkedEvents.contains(id)){
            mBookmarkedEvents.remove(id);
        }
    }

    public boolean isEventBookmarked(String id){
        return mBookmarkedEvents.contains(id);
    }

    public void clearBookmarks(){
        mBookmarkedEvents.clear();
    }

    //All Rewards details
    private AllDetailsBoard mAllDetailsBoard = new AllDetailsBoard();

    private GamificationDataHolder() {
    }

    public Rewards getmRewards() {
        return mRewards;
    }

    public void setmRewards(Rewards mRewards) {
        this.mRewards = mRewards;
    }

    public List<LeaderBoard> getmLeaderboardList() {
        return mLeaderboardList;
    }

    public void addLeaderBoard(LeaderBoard leaderboard) {
        mLeaderboardList.add(leaderboard);

    }

    public LeaderBoard getLeaderBoardat(int pos){
        return mLeaderboardList.get(pos);
    }

    public void clearLeaderBoardData(){
        mLeaderboardList.clear();
    }

    public int getLeaderboardCount(){
       return mLeaderboardList.size();
    }

    public Event getmCurrentEvent() {
        return mCurrentEvent;
    }

    public void setmCurrentEvent(Event mCurrentEvent) {
        this.mCurrentEvent = mCurrentEvent;
    }

    public EngagementBoard getmEngagementBoardDdetails() {
        return mEngagementBoardDdetails;
    }

    public void setmEngagementBoardDdetails(EngagementBoard mEngagementBoardDdetails) {
        this.mEngagementBoardDdetails = mEngagementBoardDdetails;
    }

    public BookingsBoard getmBookingBoard() {
        return mBookingBoard;
    }

    public void setmBookingBoard(BookingsBoard mBookingBoard) {
        this.mBookingBoard = mBookingBoard;
    }

    public CheckinsBoard getmCheckinsBoard() {
        return mCheckinsBoard;
    }

    public void setmCheckinsBoard(CheckinsBoard mCheckinsBoard) {
        this.mCheckinsBoard = mCheckinsBoard;
    }

    public void clearPhotosData(){
        mPhotoDates.clear();
        mImageAlbum.clear();
        mPhotosBoardTotalPhotos= null;
        mTotalPhotoPoints = null;
    }

    public String getmPhotosBoardTotalPhotos() {
        return mPhotosBoardTotalPhotos;
    }

    public String getmTotalPhotoPoints() {
        return mTotalPhotoPoints;
    }

    public void setmPhotosBoardTotalPhotos(String mPhotosBoardTotalPhotos) {
        this.mPhotosBoardTotalPhotos = mPhotosBoardTotalPhotos;
    }

    public void setmTotalPhotoPoints(String mTotalPhotoPoints) {
        this.mTotalPhotoPoints = mTotalPhotoPoints;
    }

    public HashMap<String, List<String>> getmImageAlbum() {
        return mImageAlbum;
    }

    public List<String> getmPhotoDates() {
        return mPhotoDates;
    }

    public void setmPhotoDates(List<String> mPhotoDates) {
        this.mPhotoDates = mPhotoDates;
    }

    public void setmImageAlbum(HashMap<String, List<String>> mImageAlbum) {
        this.mImageAlbum = mImageAlbum;
    }

    public int getTotalPhotDates(){
        return mPhotoDates.size();
    }

    public int getImageCountforDate(String date){
        if(mImageAlbum.get(date) != null){
            Log.d("GamificationDataHolder","count for date"+ date+"is"+mImageAlbum.get(date).size());
            return mImageAlbum.get(date).size();
        }else{
            return 0;
        }

    }

    //get date
    public String getPhotoDateat(int pos){
        return mPhotoDates.get(pos);
    }
    //get Imgeurl for a date and pos
    public String getImageUrlforDateAt(String date, int pos){
        if(mImageAlbum.get(date) != null){
            return mImageAlbum.get(date).get(pos);
        }else{
            return null;
        }
    }


    //All Rewards detail borad

    public AllDetailsBoard getmAllDetailsBoard() {
        return mAllDetailsBoard;
    }

    public void setmAllDetailsBoard(AllDetailsBoard mAllDetailsBoard) {
        this.mAllDetailsBoard = mAllDetailsBoard;
    }

    public void clearGamificationData(){
        clearPhotosData();
        mEngagementBoardDdetails.clearEngagementList();
        mBookingBoard.clearData();
        mAllDetailsBoard.clearData();
        mCheckinsBoard.clearData();
    }
}
