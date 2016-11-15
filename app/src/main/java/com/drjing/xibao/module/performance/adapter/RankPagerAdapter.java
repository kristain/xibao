package com.drjing.xibao.module.performance.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.drjing.xibao.module.performance.fragment.ShopRankFragment;

/**
 * Created by kristain on 16/1/4.
 */
public class RankPagerAdapter extends FragmentPagerAdapter {
    public RankPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        ShopRankFragment frag = new ShopRankFragment();
        Bundle bundle = new Bundle();
        bundle.putString("key", "hello world " + position);
        frag.setArguments(bundle);
        return frag;
    }
    @Override
    public int getCount() {
        return 2;
    }
}
