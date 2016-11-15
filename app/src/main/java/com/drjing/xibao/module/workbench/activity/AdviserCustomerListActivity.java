package com.drjing.xibao.module.workbench.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.library.third.ormlite.dao.Dao;
import com.drjing.xibao.R;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.config.AppConfig;
import com.drjing.xibao.module.ui.UIHelper;
import com.drjing.xibao.module.workbench.adapter.AdviserCustomerPagerAdapter;
import com.drjing.xibao.provider.db.DatabaseHelper;
import com.drjing.xibao.provider.db.entity.SearchParam;
import com.kristain.common.utils.StringUtils;

import java.sql.SQLException;

/**
 *  顾问角色客户管理
 * Created by kristain on 16/1/3.
 */
public class AdviserCustomerListActivity extends SwipeBackActivity {

    private static final String TAG = "CustomerListActivity";
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
    private TextView search_btn;

    private final static int  REQUESTCODE=0;

    private TextView btnRight;

    private Bundle bundle;

    private DatabaseHelper dbHelper;
    private Dao<SearchParam, String> searchDao;
    private SearchParam searcParam;

    private AdviserCustomerPagerAdapter mCustomerPagerAdapter;

    private int checkedPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle=getIntent().getExtras();
        setContentView(R.layout.activity_advisercustomer_list);
        dbHelper = DatabaseHelper.gainInstance(this, AppConfig.DB_NAME, AppConfig.DB_VERSION);
        dbHelper.createTable(SearchParam.class);
        try {
            searchDao = (Dao<SearchParam, String>) dbHelper.getDao(SearchParam.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initView();
    }

    void initView() {
        search_btn =(TextView)findViewById(R.id.search_btn);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        btnBack = (Button)findViewById(R.id.btnBack);
        btnRight = (TextView)findViewById(R.id.btnRight);
        btnBack.setVisibility(View.VISIBLE);
        btnRight.setVisibility(View.VISIBLE);
        btnRight.setTextSize(12);
        btnRight.setText("分配客户");
        textHeadTitle.setText("客户管理");

        mPager = (ViewPager) findViewById(R.id.content);
        mGroup = (RadioGroup) findViewById(R.id.group);
        mGroup.setOnCheckedChangeListener(new CheckedChangeListener());
        mGroup.check(R.id.my_customer);
        mCustomerPagerAdapter = new AdviserCustomerPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mCustomerPagerAdapter);
        mPager.setOnPageChangeListener(new PageChangeListener());
        mPager.setOffscreenPageLimit(3);


        /**
         * 后退返回键点击事件
         */
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /**
         * 点击搜索
         */
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showCustomerSearch(AdviserCustomerListActivity.this, REQUESTCODE);
            }
        });

        /**
         * 点击分配客户
         */
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showUnAssignCustomList(AdviserCustomerListActivity.this, bundle);
            }
        });
    }



    private class CheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.my_customer:
                    mPager.setCurrentItem(0);
                    checkedPos = 0;
                    break;
                case R.id.order_customer:
                    mPager.setCurrentItem(1);
                    checkedPos = 1;
                    break;
                case R.id.unassign_customer:
                    mPager.setCurrentItem(2);
                    checkedPos = 2;
                    break;
            }
        }
    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    mGroup.check(R.id.my_customer);
                    break;
                case 1:
                    mGroup.check(R.id.order_customer);
                    break;
                case 2:
                    mGroup.check(R.id.unassign_customer);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.e("TAG", "onActivityResultTAG:" + requestCode + "resultCode:" + resultCode);
        switch (requestCode) {
            case REQUESTCODE:
                if (intent != null) {
                    Bundle b = intent.getExtras(); //data为B中回传的Intent
                    Log.e("TAG", "name:" + b.getString("name"));
                    if (!StringUtils.isEmpty(b.getString("name")) || !StringUtils.isEmpty(b.getString("mobile"))) {
                        searcParam = new SearchParam();
                        searcParam.setMobile(b.getString("mobile"));
                        searcParam.setName(b.getString("name"));
                        searcParam.setType(checkedPos + "");
                        try {
                            if (!dbHelper.isOpen()) {
                                dbHelper = DatabaseHelper.gainInstance(AdviserCustomerListActivity.this, AppConfig.DB_NAME, AppConfig.DB_VERSION);
                                searchDao = (Dao<SearchParam, String>) dbHelper.getDao(SearchParam.class);
                            }
                            searchDao.deleteBuilder().delete();
                            searchDao.create(searcParam);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    mCustomerPagerAdapter = new AdviserCustomerPagerAdapter(getSupportFragmentManager());
                    mPager.setAdapter(mCustomerPagerAdapter);
                    mPager.setCurrentItem(checkedPos);
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (!dbHelper.isOpen()) {
                dbHelper = DatabaseHelper.gainInstance(AdviserCustomerListActivity.this, AppConfig.DB_NAME, AppConfig.DB_VERSION);
                searchDao = (Dao<SearchParam, String>) dbHelper.getDao(SearchParam.class);
            }
            searchDao.deleteBuilder().delete();
            dbHelper.releaseAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
