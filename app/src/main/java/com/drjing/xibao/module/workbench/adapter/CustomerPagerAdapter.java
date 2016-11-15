package com.drjing.xibao.module.workbench.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.drjing.xibao.module.workbench.fragment.MyCustomerListFragment;
import com.drjing.xibao.module.workbench.fragment.ServiceCustomerListFragment;

/**
 * 美容师客户列表适配器
 * Created by kristain on 16/1/4.
 */
public class CustomerPagerAdapter extends FragmentPagerAdapter {

    public CustomerPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        Fragment frag;
        if(position==0){
            frag = new MyCustomerListFragment();
        }else {
            frag = new ServiceCustomerListFragment();
        }
        Bundle bundle = new Bundle();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
