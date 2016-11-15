package com.drjing.xibao.module.workbench.activity;

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
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.utils.FuncUtils;
import com.drjing.xibao.common.view.pulltorefresh.PullToRefreshBase;
import com.drjing.xibao.common.view.pulltorefresh.PullToRefreshListView;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.entity.OrderEntity;
import com.drjing.xibao.module.entity.OrderServiceTypeEnum;
import com.drjing.xibao.module.entity.OrderStatusEnum;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.performance.adapter.QuickAdapter;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.List;

/**
 * 客户订单列表页面
 * Created by kristain on 16/1/3.
 */
public class CustomerOrderListActivity extends SwipeBackActivity {
    private static final String TAG="CustomOrderListAct";

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

    private Bundle bundle;

    private TextView list_sum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);
        bundle=getIntent().getExtras();
        initView();
        initEvent();
        initData();
        loadData();
    }

    void initView() {
        listView = (PullToRefreshListView)findViewById(R.id.listView);
        btnBack = (Button)findViewById(R.id.btnBack);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        search_btn = (TextView)findViewById(R.id.search_btn);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle.setText("客户订单");
        list_sum = (TextView)findViewById(R.id.list_sum);

        adapter = new QuickAdapter<OrderEntity>(this, R.layout.orders_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, OrderEntity order) {
                helper.setText(R.id.custom_name, StringUtils.formatCustomerName(order.getCustomerName()))
                        .setText(R.id.order_no, "订单号：" + order.getCode())
                        .setText(R.id.order_status, OrderStatusEnum.getMsgByCode(order.getStatus()))
                        .setText(R.id.custom_phone, FuncUtils.formatPhone(order.getMobile()))
                        .setText(R.id.service_type, OrderServiceTypeEnum.getMsgByCode(order.getType()))
                        .setText(R.id.order_date, FuncUtils.getOrderServerTime(order.getStartTime(), order.getEndTime()))
                        .setText(R.id.service_content, OrderServiceTypeEnum.STORETYPE.code.equals(order.getType()) ? " " + order.getStoreName() : ":" + order.getContent());
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
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showOrderSearch(CustomerOrderListActivity.this,REQUESTCODE);
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
                    Bundle bundle = new Bundle();
                    bundle.putString("code",shop.getId()+"");
                    bundle.putString("order_no",shop.getCode());
                    bundle.putString("order_type","customer_order");
                    UIHelper.showOrdersDetail(CustomerOrderListActivity.this, bundle);
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




    private void initData() {
        param = new OrderEntity();
        pno = 1;
        isLoadAll = false;
        param.setCustomerId(bundle.getString("customer_id"));
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
        if(!StringUtils.isEmpty(param.getCustomerId())){
            HttpClient.getCustomerOrders(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    listView.onRefreshComplete();
                    Log.i(TAG, "getCustomerOrdersTAG成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
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
                    } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(CustomerOrderListActivity.this);
                    } else {
                        listView.onRefreshComplete();
                        listView.setFooterViewTextError();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i(TAG, "getOrders失败返回数据:" + request.toString());
                    listView.onRefreshComplete();
                    listView.setFooterViewTextError();
                }
            },this);
        }else{
            Toast.makeText(this,"缺少请求参数[customer_id]",Toast.LENGTH_LONG).show();
        }

    }



}
