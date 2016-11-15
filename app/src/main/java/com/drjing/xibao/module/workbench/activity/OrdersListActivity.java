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
import com.drjing.xibao.module.entity.OrderTypeEnum;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.performance.adapter.QuickAdapter;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.List;

/**
 * 服务日志、提醒日志、回访日志、订单备注、销售日志等订单列表
 * Created by kristain on 15/12/21.
 */
public class OrdersListActivity extends SwipeBackActivity {

    private static final String TAG="OrdersListActivity";

    private OrderEntity param;
    private int pno = 1;
    private boolean isLoadAll;

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


    private final static int  REQUESTCODE=0;

    private final static int REQUESTCODE1=1;

    private Bundle bundle;

    private TextView list_sum;

    private RelativeLayout search_layout;

    private String server_time="";

    private int totalRow=0;

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
        btnBack = (Button)findViewById(R.id.btnBack);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        search_btn = (TextView)findViewById(R.id.search_btn);
        search_layout = (RelativeLayout)findViewById(R.id.search_layout);
        btnBack.setVisibility(View.VISIBLE);
        list_sum = (TextView)findViewById(R.id.list_sum);
        textHeadTitle.setText(OrderTypeEnum.getMsgByCode(bundle.getString("order_type")));
        listView = (PullToRefreshListView)findViewById(R.id.listView);

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
                    if ("1".equals(customer.getString("vip"))) {
                        helper.getView().findViewById(R.id.logo).setVisibility(View.VISIBLE);
                    }else{
                        helper.getView().findViewById(R.id.logo).setVisibility(View.GONE);
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
                UIHelper.showOrderDateSearch(OrdersListActivity.this,REQUESTCODE);
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
                    item.putString("position",(i - 1)+"");
                    item.putString("code", shop.getId() + "");
                    item.putString("order_no",shop.getCode());
                    item.putString("customer_id",shop.getCustomerId());
                    item.putString("order_type", bundle.getString("order_type"));
                    item.putString("accountname", bundle.getString("accountname"));
                    if(OrderTypeEnum.SALELOG.getCode().equals( bundle.getString("order_type"))){
                        UIHelper.showSaleLogDetailForResult(OrdersListActivity.this, item, REQUESTCODE1);
                    }else{
                        UIHelper.showOrdersDetailForResult(OrdersListActivity.this, item, REQUESTCODE1);
                    }

                }
            }
        });
    }


    private void initData() {
        param = new OrderEntity();
        param.setUser_id(bundle.getString("user_id"));
        pno = 1;
        isLoadAll = false;
    }

    private void loadData() {
        if (isLoadAll) {
            return;
        }
        param.setPage(pno);
        param.setPageSize(HttpClient.PAGE_SIZE);
        if(!StringUtils.isEmpty(server_time)){
            param.setServer_time_begin(server_time+" 00:00:00");
            param.setServer_time_end(server_time + " 23:59:59");
        }
        param.setOrder_type(bundle.getString("order_type"));
        listView.setFooterViewTextNormal();
        if(!StringUtils.isEmpty(param.getUser_id())){
            HttpClient.getOrders(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    listView.onRefreshComplete();
                    Log.i(TAG, "getOrdersTAG返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if(HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))){
                        JSONObject data = JSON.parseObject(object.getString("data"));
                        if(!StringUtils.isEmpty( data.getString("totalRow"))){
                            totalRow = Integer.parseInt(data.getString("totalRow"));
                        }
                        list_sum.setText("共"+totalRow+"条");
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

                        for (int j=0;j<list.size();j++){
                            Log.e("TAG","code:"+list.get(j).getCode()+","+list.get(j).getCustomer());
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
                        UIHelper.showLogin(OrdersListActivity.this);
                    } else{
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
            Toast.makeText(this, "缺少请求参数[user_id]", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.e("TAG","onActivityResult"+requestCode+":"+resultCode);
        switch (resultCode) {
            case RESULT_OK:
                if(intent!=null){
                    Bundle b=intent.getExtras(); //data为B中回传的Intent
                    server_time=b.getString("server_time");//str即为回传的值
                    String position = b.getString("position");
                    Log.e("TAG","onActivityResult:"+position);
                    if(!StringUtils.isEmpty(position)){
                        list_sum.setText("共"+(totalRow-1)+"条");
                        adapter.remove(Integer.parseInt(position));
                        adapter.notifyDataSetChanged();
                    }else{
                        initData();
                        loadData();
                    }
                }
                break;
            default:
                break;
        }
    }


}
