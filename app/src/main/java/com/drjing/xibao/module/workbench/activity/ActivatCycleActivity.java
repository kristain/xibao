package com.drjing.xibao.module.workbench.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.library.third.ormlite.dao.Dao;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.config.AppConfig;
import com.drjing.xibao.module.entity.CustomerEntity;
import com.drjing.xibao.module.ui.UIHelper;
import com.drjing.xibao.provider.db.DatabaseHelper;
import com.drjing.xibao.provider.db.entity.User;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * 激活周期
 * Created by kristain on 15/12/30.
 */
public class ActivatCycleActivity extends SwipeBackActivity {

    private static final String TAG="ActivatCycleActivity";
    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;


    private LinearLayout one_weekday_layout,two_weekday_layout,one_month_layout,two_month_layout,three_month_layout,six_month_layout;

    private TextView one_weekday_person,six_month_person,two_weekday_person,one_month_person,two_month_person,three_month_person;
    private DatabaseHelper dbHelper;
    private Dao<User, String> userDao;
    private User user;

    private Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_cycle);
        dbHelper = DatabaseHelper.gainInstance(this, AppConfig.DB_NAME, AppConfig.DB_VERSION);
        try {
            userDao = (Dao<User, String>)dbHelper.getDao(User.class);
            List<User> users = userDao.queryBuilder().query();
            if(users==null || users.size()==0 || StringUtils.isEmpty(users.get(0).getId())){
                UIHelper.showLogin(this);
                return;
            }
            user = users.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            UIHelper.showLogin(this);
        }
        initView();
        initEvent();
        getCustomerQueryCondition();
    }


    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("激活周期");

        one_weekday_layout =(LinearLayout) findViewById(R.id.one_weekday_layout);
        two_weekday_layout =(LinearLayout) findViewById(R.id.two_weekday_layout);
        one_month_layout =(LinearLayout) findViewById(R.id.one_month_layout);
        two_month_layout =(LinearLayout) findViewById(R.id.two_month_layout);
        three_month_layout =(LinearLayout) findViewById(R.id.three_month_layout);
        six_month_layout =(LinearLayout) findViewById(R.id.six_month_layout);

        one_weekday_person = (TextView)findViewById(R.id.one_weekday_person);
        six_month_person= (TextView)findViewById(R.id.six_month_person);
        two_weekday_person= (TextView)findViewById(R.id.two_weekday_person);
        one_month_person= (TextView)findViewById(R.id.one_month_person);
        two_month_person= (TextView)findViewById(R.id.two_month_person);
        three_month_person= (TextView)findViewById(R.id.three_month_person);

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


        one_weekday_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("days","7");
                UIHelper.showUnActivateCustomList(ActivatCycleActivity.this,bundle);
            }
        });

        two_weekday_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("days","14");
                UIHelper.showUnActivateCustomList(ActivatCycleActivity.this,bundle);
            }
        });

        one_month_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("days","30");
                UIHelper.showUnActivateCustomList(ActivatCycleActivity.this,bundle);
            }
        });


        two_month_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("days","60");
                UIHelper.showUnActivateCustomList(ActivatCycleActivity.this,bundle);
            }
        });


        three_month_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("days","90");
                UIHelper.showUnActivateCustomList(ActivatCycleActivity.this,bundle);
            }
        });


        six_month_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("days","180");
                UIHelper.showUnActivateCustomList(ActivatCycleActivity.this,bundle);
            }
        });
    }



    /**
     * 获取获取待激活客户筛选条件
     */
    private void getCustomerQueryCondition(){
        CustomerEntity param = new CustomerEntity();
        param.setStaff_id(user.getId());
        if(!StringUtils.isEmpty(param.getStaff_id())){
            HttpClient.getCustomerQueryCondition(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    JSONObject object = JSON.parseObject(body);
                    Log.i("ConditionTAG", "成功返回数据:" + body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        List<CustomerEntity> tagList = JSONArray.parseArray(object.getString("data"), CustomerEntity.class);
                        if(tagList!=null && tagList.size()>0){
                            int size = tagList.size();
                            for (int i=0;i<size;i++){
                                if(tagList.get(i).getDays()==7){
                                    one_weekday_person.setText(tagList.get(i).getPersons()+"人");
                                }else if(tagList.get(i).getDays()==14){
                                    two_weekday_person.setText(tagList.get(i).getPersons() + "人");
                                }else if(tagList.get(i).getDays()==30){
                                    one_month_person.setText(tagList.get(i).getPersons()+"人");
                                }else if(tagList.get(i).getDays()==60){
                                    two_month_person.setText(tagList.get(i).getPersons()+"人");
                                }else if(tagList.get(i).getDays()==90){
                                    three_month_person.setText(tagList.get(i).getPersons()+"人");
                                }else if(tagList.get(i).getDays()==180){
                                    six_month_person.setText(tagList.get(i).getPersons()+"人");
                                }
                            }
                        }
                    }  else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(ActivatCycleActivity.this);
                    }else {
                        Log.i("ConditionTAG", "成功返回数据:" + body);
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("ConditionTAG", "失败返回数据:" + request.toString());
                }
            }, ActivatCycleActivity.this);
        }else{
            Toast.makeText(this,"缺少请求参数[staff_id]",Toast.LENGTH_LONG).show();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != dbHelper) dbHelper.releaseAll();
    }
}
