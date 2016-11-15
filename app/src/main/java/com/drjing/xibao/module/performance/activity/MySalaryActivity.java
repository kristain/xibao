package com.drjing.xibao.module.performance.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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
import com.drjing.xibao.module.entity.SearchParam;
import com.drjing.xibao.module.entity.SearchSalary;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.performance.adapter.QuickAdapter;
import com.drjing.xibao.module.ui.UIHelper;
import com.drjing.xibao.provider.db.DatabaseHelper;
import com.drjing.xibao.provider.db.entity.User;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * 我的历史工资
 * Created by kristain on 16/1/3.
 */
public class MySalaryActivity extends SwipeBackActivity {

    /**
     * 后退返回按钮
     */
    private Button btnBack;
    /**
     * 标题栏文案
     */
    private TextView textHeadTitle;

    private ListView list_view;

    private SearchParam param = new SearchParam();

    QuickAdapter<SearchSalary> adapter;

    private DatabaseHelper dbHelper;

    private Dao<User, String> userDao;

    private User user;

    private TextView month_area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_salarylist);
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
            UIHelper.showLogin(this);
        }
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void initView() {
        btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("我的历史工资");
        list_view = (ListView)findViewById(R.id.list_view);
        month_area = (TextView)findViewById(R.id.month_area);
        adapter = new QuickAdapter<SearchSalary>(this, R.layout.my_salary_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, SearchSalary item) {
                helper.setText(R.id.month, item.getWorkMonth())
                        .setText(R.id.money, item.getSalary()+"元")
                        .setText(R.id.role_name, item.getRoleName());
            }
        };
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

    private void loadData() {
        param.setUid(user.getId());
        if(!StringUtils.isEmpty(param.getUid())){
            HttpClient.getSalaryList(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getSalaryListTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        List<SearchSalary> list = JSONArray.parseArray(object.getString("data"), SearchSalary.class);
                        if(list!=null&& list.size()>0){
                            month_area.setText(list.get(list.size()-1).getWorkMonth()+"-"+list.get(0).getWorkMonth());
                        }
                        adapter.addAll(list);
                        list_view.setAdapter(adapter);
                    }else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(MySalaryActivity.this);
                    }else{
                        Log.i("getSalaryListTAG", "失败返回数据:" + body.toString());
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getSalaryListTAG", "失败返回数据:" + request.toString());
                }
            },this);
        }else{
            Toast.makeText(this,"缺少请求参数[uid]",Toast.LENGTH_LONG).show();

        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != dbHelper) dbHelper.releaseAll();
    }
}
