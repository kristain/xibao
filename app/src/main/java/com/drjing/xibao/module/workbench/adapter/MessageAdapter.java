package com.drjing.xibao.module.workbench.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.drjing.xibao.R;
import com.drjing.xibao.module.workbench.fragment.ActivateMessageFragment;
import com.drjing.xibao.module.workbench.fragment.MyMessageFragment;
import com.drjing.xibao.module.workbench.fragment.ReturnMessageFragment;

/**
 * 信息模版适配器
 * Created by kristain on 15/12/30.
 */
public class MessageAdapter extends FragmentPagerAdapter {

    private static String[] TITLES;
    private static String[] URLS = new String[]{"", "", "",""};
    private FragmentManager mFM;

    public MessageAdapter(FragmentManager fm,Context context) {
        super(fm);
        mFM= fm;
        TITLES = context.getResources().getStringArray(R.array.message_titles);
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return  ReturnMessageFragment.newInstance(URLS[position % URLS.length]);
        }
        if(position==1){
            return ActivateMessageFragment.newInstance(URLS[position % URLS.length]);
        }
        return MyMessageFragment.newInstance(URLS[position % URLS.length]);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position % TITLES.length];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }


}
