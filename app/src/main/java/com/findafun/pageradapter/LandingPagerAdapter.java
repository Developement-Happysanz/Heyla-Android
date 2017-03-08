package com.findafun.pageradapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.findafun.R;
import com.findafun.customview.PagerSlidingTabStrip;
import com.findafun.fragment.FavoriteFragment;
import com.findafun.fragment.PopularFragment;
import com.findafun.fragment.RewardsFragment;
import com.findafun.fragment.StaticFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nandhakumar.k on 30/10/15.
 */
public class LandingPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
    private static final String TAG = LandingPagerAdapter.class.getName();

    Context context;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    private final String[] TITLES = {"FAVOURITES","POPULAR", "HOTSPOT", "LEADERBOARD"};
    private List<Integer> mTabResources = new ArrayList<Integer>();
    private List<Integer> mUnselectedTabResources = new ArrayList<Integer>();
    onFragmentsRegisteredListener onFragmentsRegisteredListener;
    private boolean instantiated;

    public LandingPagerAdapter(onFragmentsRegisteredListener onFragmentsRegisteredListener, FragmentManager fm, Context context) {
        super(fm);
        this.onFragmentsRegisteredListener = onFragmentsRegisteredListener;
        instantiated = false;
        this.context = context;

        mTabResources.add(R.drawable.favorite_new_unselected);
        mTabResources.add(R.drawable.popular_new_unselected);
        mTabResources.add(R.drawable.hotspot_new_unselected);
        mTabResources.add(R.drawable.leaderboard_new_unselected);

        mUnselectedTabResources.add(R.drawable.favorite_new_selected);
        mUnselectedTabResources.add(R.drawable.popular_new_selected);
        mUnselectedTabResources.add(R.drawable.hotspot_new_selected);
        mUnselectedTabResources.add(R.drawable.leaderboard_new_selected);




        /*mTabResources.add(R.drawable.home_tab_selected);
        mTabResources.add(R.drawable.explore_white);
        mTabResources.add(R.drawable.hotspot_tab_selected);
        mTabResources.add(R.drawable.rewards_tab_selected);

        mUnselectedTabResources.add(R.drawable.home_tab_unselected);
        mUnselectedTabResources.add(R.drawable.export_tab_unselected);
        mUnselectedTabResources.add(R.drawable.hotspot_tab_unselected);
        mUnselectedTabResources.add(R.drawable.rewards_tab_unselected);*/
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem called" + position);

        switch (position) {
            case 0:
                Log.d(TAG, "returning Landing page fragment");
                return FavoriteFragment.newInstance(position);
//            return  LandingPagerFragment.newInstance(position);
            case 1:
                Log.d(TAG, "returning Popular fragment");
                return PopularFragment.newInstance(position);
//                return LandingPagerFragment.newInstance(position);
            case 2:
                Log.d(TAG, "returning Nearby fragment");
//                return NearbyFragment.newInstance(position);
                return StaticFragment.newInstance(position);
            case 3:
                return RewardsFragment.newInstance(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        //Log.d(TAG,"getCount called"+ TITLES.length);
        return TITLES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Log.d(TAG,"getTitle called");
        return TITLES[position];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //  Log.d(TAG, "Instantiate page item" + position);
        /*if(position>2){
            Intent navigation = new Intent(context, MapsActivity.class);
            navigation.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(navigation);
            return null;
        }else {*/
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
        //}
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.d(TAG, "destroy item" + position);
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        Log.d(TAG, "getting registered fragment");
        return registeredFragments.get(position);
    }

    @Override
    public int getPageIconResId(int position) {
        return mUnselectedTabResources.get(position);
    }

    @Override
    public int getCurrentPaeIconResId(int position) {
        Log.d(TAG, "returning current selected image");
        return mTabResources.get(position);
    }

    public interface onFragmentsRegisteredListener {
        void onFragmentsRegistered();
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        super.finishUpdate(container);
        Log.d(TAG, "finishedUpdating the PagerAdapter");
        //if (!instantiated) {
        instantiated = true;
        if (onFragmentsRegisteredListener != null) {
            onFragmentsRegisteredListener.onFragmentsRegistered();
        }
        //}
    }
}
