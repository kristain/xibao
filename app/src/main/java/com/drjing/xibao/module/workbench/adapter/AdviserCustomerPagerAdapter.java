package com.drjing.xibao.module.workbench.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.drjing.xibao.module.workbench.fragment.AssignCustomerListFragment;
import com.drjing.xibao.module.workbench.fragment.MyCustomerListFragment;
import com.drjing.xibao.module.workbench.fragment.ServiceCustomerListFragment;

/**
 * 顾问客户列表适配器
 * Created by kristain on 16/1/4.
 */
public class AdviserCustomerPagerAdapter extends FragmentPagerAdapter {
    public AdviserCustomerPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        Fragment frag;
        if(position==0){
            frag = new MyCustomerListFragment();
        }else if(position==1){
            frag = new ServiceCustomerListFragment();
        }else{
            frag = new AssignCustomerListFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putString("key", "hello world " + position);
        frag.setArguments(bundle);
        return frag;
    }
    @Override
    public int getCount() {
        return 3;
    }
}
