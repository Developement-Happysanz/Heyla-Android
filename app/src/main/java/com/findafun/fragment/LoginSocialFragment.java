package com.findafun.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.findafun.R;
import com.findafun.activity.LoginActivity;
import com.findafun.activity.heyla.LoginSocialActivity;

/**
 * Created by Nandha on 11-03-2017.
 */

public class LoginSocialFragment extends Fragment{

    private static final String TAG = LoginSocialActivity.class.getName();

    public View view;

    public LoginSocialFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login_social, container, false);

        // Inflate the layout for this fragment
        return view;
    }
}
