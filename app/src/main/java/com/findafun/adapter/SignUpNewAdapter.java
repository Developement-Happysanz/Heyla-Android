package com.findafun.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.findafun.fragment.LoginFragment;
import com.findafun.fragment.SignupFragment;

/**
 * Created by Nandha on 24-02-2017.
 */

public class SignUpNewAdapter extends FragmentStatePagerAdapter {
    public SignUpNewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                // Top Rated fragment activity
                return new SignupFragment();
            case 1:
                // Games fragment activity
                return new LoginFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
