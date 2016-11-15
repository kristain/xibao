package com.drjing.xibao.module.news.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.drjing.xibao.module.entity.StoreEntity;
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
 * 客户列表－选择门店
 * Created by kristain on 15/12/30.
 */
public class CustomerStoreActivity extends SwipeBackActivity {

    private static final String TAG="CustomerStoreActivity";
    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;

    private DatabaseHelper dbHelper;
    private Dao<User, String> userDao;
    private User user;
    private ListView store_listview;
    QuickAdapter<StoreEntity> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_store);
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
        loadData();
    }


    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("客户列表");
        store_listview = (ListView)findViewById(R.id.store_listview);
        adapter = new QuickAdapter<StoreEntity>(this, R.layout.store_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, StoreEntity item) {
                JSONObject object = JSON.parseObject(item.getShopowner());
                helper.setText(R.id.store_name, item.getName())
                .setText(R.id.storeid,item.getId()+"")
                .setText(R.id.name,(object!=null)?object.getString("username"):"暂无店长");
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

        store_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle=new Bundle();
                bundle.putString("storeid",((TextView) view.findViewById(R.id.storeid)).getText().toString());
                bundle.putString("role_key",user.getRoleKey());
                UIHelper.showAllCustomerList(CustomerStoreActivity.this,bundle);
            }
        });

    }


    private void loadData(){
        StoreEntity param = new StoreEntity();
        if(!StringUtils.isEmpty(user.getId()+"")){
            param.setId(Integer.parseInt(user.getId()));
            HttpClient.getCompanyStoreList(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getCompanyStoreListTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        List<StoreEntity> list = JSONArray.parseArray(object.getString("data"), StoreEntity.class);
                        adapter.addAll(list);
                        store_listview.setAdapter(adapter);
                    }  else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(CustomerStoreActivity.this);
                    }else {
                        Log.i("getCompanyStoreListTAG", "获取数据失败:" + body);
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getCompanyStoreListTAG", "失败返回数据:" + request.toString());
                }
            }, CustomerStoreActivity.this);
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
