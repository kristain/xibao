package com.drjing.xibao.module.performance.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.drjing.xibao.R;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.performance.adapter.RankPagerAdapter;

/**
 * 英雄榜
 * Created by kristain on 16/1/3.
 */
public class RankActivity extends SwipeBackActivity {

    /**
     * 后退返回按钮
     */
    private Button btnBack;
    /**
     * 标题栏文案
     */
    private TextView textHeadTitle;

    private ViewPager mPager;
    private RadioGroup mGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        initView();
        initEvent();
    }

    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("英雄榜");

        mPager = (ViewPager) findViewById(R.id.content);
        mGroup = (RadioGroup) findViewById(R.id.group);
        mGroup.setOnCheckedChangeListener(new CheckedChangeListener());
        mGroup.check(R.id.personal_rank);
        mPager.setAdapter(new RankPagerAdapter(getSupportFragmentManager()));
        mPager.setOnPageChangeListener(new PageChangeListener());
        mPager.setOffscreenPageLimit(2);
    }

    private void initEvent() {
        /**
         * 返回后退点击事件
         */
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private class CheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.personal_rank:
                    mPager.setCurrentItem(0);
                    break;
                case R.id.shop_rank:
                    mPager.setCurrentItem(1);
                    break;
            }
        }
    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    mGroup.check(R.id.personal_rank);
                    break;
                case 1:
                    mGroup.check(R.id.shop_rank);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }
}
