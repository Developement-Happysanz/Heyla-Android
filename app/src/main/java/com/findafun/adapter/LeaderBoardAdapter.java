package com.findafun.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.findafun.fragment.LeaderCityBoardFragment;
import com.findafun.fragment.LeaderFullBoardFragment;

/**
 * Created by Nandha on 25-08-2016.
 */
public class LeaderBoardAdapter extends FragmentStatePagerAdapter {

    public LeaderBoardAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                // Top Rated fragment activity
                return new LeaderFullBoardFragment();
            case 1:
                // Games fragment activity
                return new LeaderCityBoardFragment();
               // return new LeaderFullBoardFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
