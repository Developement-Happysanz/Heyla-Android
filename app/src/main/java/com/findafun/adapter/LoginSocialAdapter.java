package com.findafun.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.findafun.fragment.LoginFragment;
import com.findafun.fragment.LoginSocialFragment;
import com.findafun.fragment.SignupFragment;

/**
 * Created by Nandha on 11-03-2017.
 */

public class LoginSocialAdapter extends FragmentStatePagerAdapter {

    public LoginSocialAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                // Top Rated fragment activity
                return new LoginSocialFragment();
            case 1:
                // Games fragment activity
                return new SignupFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
