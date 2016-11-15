package com.drjing.xibao.module.workbench.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.library.third.ormlite.dao.Dao;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.utils.FuncUtils;
import com.drjing.xibao.common.view.pulltorefresh.PullToRefreshBase;
import com.drjing.xibao.common.view.pulltorefresh.PullToRefreshListView;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.config.AppConfig;
import com.drjing.xibao.module.entity.OrderEntity;
import com.drjing.xibao.module.entity.OrderServiceTypeEnum;
import com.drjing.xibao.module.entity.OrderStatusEnum;
import com.drjing.xibao.module.entity.OrderTypeEnum;
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
 * 我的订单列表页面
 * Created by kristain on 16/1/3.
 */
public class MyOrderListActivity extends SwipeBackActivity {
    private static final String TAG="MyOrderListActivity";

    private OrderEntity param;
    private int pno = 1;
    private boolean isLoadAll;
    private final static int  REQUESTCODE=0;

    /**
     * 下拉加载列表组件
     */
    private PullToRefreshListView listView;
    /**
     * 后退返回按钮
     */
    private Button btnBack;
    /**
     * 标题栏文案
     */
    private TextView textHeadTitle;

    private TextView search_btn;


    QuickAdapter<OrderEntity> adapter;

    private DatabaseHelper dbHelper;
    private Dao<User, String> userDao;
    private User user;

    private TextView list_sum;

    private RelativeLayout search_layout;

    /**
     * 订单号
     */
    private String code="";
    /**
     * 美容师ID
     */
    private String staffId="";
    /**
     * 顾问ID
     */
    private String adviserId="";
    /**
     * 顾问姓名
     */
    private String customerName="";
    /**
     * 手机号码
     */
    private String mobile="";
    /**
     * 是否会员
     */
    private String isVip="";
    /**
     * 服务日期 - 开始
     */
    private String serverTimeBegin="";
    /**
     * 服务日期－结束
     */
    private String serverTimeEnd="";
    /**
     * 下单日志-开始
     */
    private String orderTimeBegin="";
    /**
     * 下单日志－结束
     */
    private String orderTimeEnd="";

    /**
     * 门店ID
     */
    private String storeId="";

    /**
     *  状态
     */
    private String status="";

    private Bundle bundle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);
        bundle = getIntent().getExtras();
        if(bundle!=null){
            status =bundle.getString("status");
            storeId =bundle.getString("storeId");
            orderTimeEnd =bundle.getString("orderTimeEnd");
            orderTimeBegin =bundle.getString("orderTimeBegin");
            serverTimeEnd =bundle.getString("serverTimeEnd");
            serverTimeBegin =bundle.getString("serverTimeBegin");
            isVip =bundle.getString("isVip");
            mobile =bundle.getString("mobile");
            customerName =bundle.getString("customerName");
            adviserId =bundle.getString("adviserId");
            staffId =bundle.getString("staffId");
            code =bundle.getString("code");
        }
        dbHelper = DatabaseHelper.gainInstance(this, AppConfig.DB_NAME, AppConfig.DB_VERSION);
        initView();
        initEvent();
    }

    void initView() {
        listView = (PullToRefreshListView)findViewById(R.id.listView);
        btnBack = (Button)findViewById(R.id.btnBack);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        search_btn = (TextView)findViewById(R.id.search_btn);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle.setText("我的订单");
        list_sum = (TextView)findViewById(R.id.list_sum);
        search_layout = (RelativeLayout)findViewById(R.id.search_layout);
        adapter = new QuickAdapter<OrderEntity>(this, R.layout.orders_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, OrderEntity order) {
                JSONObject customer = JSON.parseObject(order.getCustomer());
                if(customer!=null) {
                    helper.setText(R.id.custom_name, StringUtils.formatCustomerName(customer.getString("name")))
                            .setText(R.id.order_no, "订单号：" + order.getCode())
                            .setText(R.id.order_status, OrderStatusEnum.getMsgByCode(order.getStatus()))
                            .setText(R.id.custom_phone, FuncUtils.formatPhone(customer.getString("mobile")))
                            .setText(R.id.service_type, OrderServiceTypeEnum.getMsgByCode(order.getType()))
                            .setText(R.id.order_date, FuncUtils.getOrderServerTime(order.getStartTime(), order.getEndTime()))
                            .setText(R.id.service_content, OrderServiceTypeEnum.STORETYPE.code.equals(order.getType()) ? " " + order.getStoreName() : ":" + order.getContent());
                    if (!StringUtils.isEmpty(customer.getString("vip")) && "1".equals(customer.getString("vip"))) {
                        helper.getView().findViewById(R.id.logo).setVisibility(View.VISIBLE);
                    }
                }
            }
        };
        listView.addFooterView();
        listView.setAdapter(adapter);
    }

    private void initEvent(){
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
        search_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showOrderSearch(MyOrderListActivity.this,REQUESTCODE);
            }
        });

        // 加载更多
        listView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                loadData();
            }
        });
        // 点击事件
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OrderEntity shop = adapter.getItem(i - 1);
                if (shop != null) {
                    Bundle item = new Bundle();
                    item.putString("code", shop.getId() + "");
                    item.putString("order_type", OrderTypeEnum.MYORDER.getCode());
                    UIHelper.showOrdersDetail(MyOrderListActivity.this, item);
                }
            }
        });
        // 下拉刷新
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                initData();
                loadData();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("TAG","onresume");
        if(user==null){
            try {
                userDao = (Dao<User, String>)dbHelper.getDao(User.class);
                List<User> users = userDao.queryBuilder().query();
                if(users==null || users.size()==0 || StringUtils.isEmpty(users.get(0).getId())){
                    UIHelper.showLogin(this);
                    return;
                }
                user = users.get(0);
                initData();
                loadData();
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
                UIHelper.showLogin(this);
            }
        }
    }

    private void initData() {
        param = new OrderEntity();
        pno = 1;
        isLoadAll = false;
        param.setUser_id(user.getId());
        param.setOrder_type(OrderTypeEnum.MYORDER.code);
        param.setCode(code);
        param.setStaffId(staffId);
        param.setAdviserId(adviserId);
        param.setCustomerName(customerName);
        param.setMobile(mobile);
        param.setIsVip(isVip);
        param.setServer_time_begin(serverTimeBegin);
        param.setServer_time_end(serverTimeEnd);
        param.setOrder_time_begin(orderTimeBegin);
        param.setOrder_time_end(orderTimeEnd);
        param.setStore_id(storeId);
        param.setStatus(status);
    }

    private void loadData() {
        if (isLoadAll) {
            return;
        }
        if(param==null){
            initData();
        }
        param.setPage(pno);
        param.setPageSize(HttpClient.PAGE_SIZE);
        listView.setFooterViewTextNormal();
        if(!StringUtils.isEmpty(param.getUser_id())){
            HttpClient.getOrders(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    listView.onRefreshComplete();
                    Log.i(TAG, "getOrdersTAG成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if(HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        JSONObject data = JSON.parseObject(object.getString("data"));
                        list_sum.setText("共"+data.getString("totalRow")+"条");
                        List<OrderEntity> list = JSONArray.parseArray(data.getString("list"), OrderEntity.class);
                        // 下拉刷新
                        if (pno == 1 && adapter.getCount() != 0) {
                            adapter.clear();
                        }

                        // 暂无数据
                        if (pno == 1 && list.isEmpty()) {
                            listView.setFooterViewTextNoData();
                            return;
                        }

                        // 已加载全部
                        if (pno > 1 && (list.isEmpty() || list.size() < HttpClient.PAGE_SIZE)) {
                            if(list.size()>0){
                                adapter.addAll(list);
                            }
                            listView.setFooterViewTextNoMoreData();
                            isLoadAll = true;
                            return;
                        }
                        adapter.addAll(list);
                        if(pno ==1 && (list.isEmpty() || list.size() < HttpClient.PAGE_SIZE)){
                            listView.onRefreshComplete();
                            listView.setFooterViewTextNoMoreData();
                            return;
                        }
                        pno++;
                    }else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(MyOrderListActivity.this);
                    }else{
                        listView.onRefreshComplete();
                        listView.setFooterViewTextError();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i(TAG, "getOrdersTAG失败返回数据:" + request.toString());
                    listView.onRefreshComplete();
                    listView.setFooterViewTextError();
                }
            },this);
        }else{
            Toast.makeText(this,"缺少请求参数[user_id]",Toast.LENGTH_LONG).show();
        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != dbHelper) dbHelper.releaseAll();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.e("TAG", "onActivityResult" + requestCode + ":" + resultCode);
        switch (resultCode) {
            case RESULT_OK:
                if(intent!=null){
                    Bundle b=intent.getExtras(); //data为B中回传的Intent
                    if(!StringUtils.isEmpty(b.getString("code"))){
                        code =  b.getString("code");
                    }
                    if(!StringUtils.isEmpty(b.getString("customerName"))){
                        customerName =  b.getString("customerName");
                    }
                    if(!StringUtils.isEmpty(b.getString("mobile"))){
                        mobile =  b.getString("mobile");
                    }
                    if(!StringUtils.isEmpty(b.getString("isVip"))){
                        isVip =  b.getString("isVip");
                    }
                    if(!StringUtils.isEmpty(b.getString("serverTimeBegin"))){
                        serverTimeBegin =  b.getString("serverTimeBegin");
                    }
                    if(!StringUtils.isEmpty(b.getString("serverTimeEnd"))){
                        serverTimeEnd =  b.getString("serverTimeEnd");
                    }
                    if(!StringUtils.isEmpty(b.getString("orderTimeBegin"))){
                        orderTimeBegin =  b.getString("orderTimeBegin");
                    }
                    if(!StringUtils.isEmpty(b.getString("orderTimeEnd"))){
                        orderTimeEnd =  b.getString("orderTimeEnd");
                    }
                    if(!StringUtils.isEmpty(b.getString("status"))){
                        status =  b.getString("status");
                    }
                    Log.e("TAG","onActivityResult:code:"+code);
                    initData();
                    loadData();
                }
                break;
            default:
                break;
        }
    }



}
