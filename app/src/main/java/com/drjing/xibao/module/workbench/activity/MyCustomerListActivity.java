package com.drjing.xibao.module.workbench.activity;

import android.content.Intent;
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
import com.drjing.xibao.common.view.pulltorefresh.PullToRefreshBase;
import com.drjing.xibao.common.view.pulltorefresh.PullToRefreshListView;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.entity.SearchCustomer;
import com.drjing.xibao.module.entity.SearchParam;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.performance.adapter.QuickAdapter;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.List;

/**
 * 行动计划-我的客户(获取门店客户)
 * Created by kristain on 16/1/3.
 */
public class MyCustomerListActivity extends SwipeBackActivity {

    private static final String TAG = "MyCustomerListActivity";
    /**
     * 后退返回按钮
     */
    private Button btnBack;
    /**
     * 标题栏文案
     */
    private TextView textHeadTitle;
    private TextView search_btn;

    private final static int  REQUESTCODE=0;


    private SearchParam param;
    private int pno = 1;
    private boolean isLoadAll;


    PullToRefreshListView listView;
    /**
     *
     */
    QuickAdapter<SearchCustomer> adapter;


    private int pageSize=0;

    private Bundle bundle;


    private String search_name="";
    private String search_mobile="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycustomer_list);
        bundle = getIntent().getExtras();
        initView();
        initSearchParam();
        loadData();
    }

    void initView() {
        search_btn =(TextView)findViewById(R.id.search_btn);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle.setText("我的客户");
        listView = (PullToRefreshListView)findViewById(R.id.listView);
        adapter = new QuickAdapter<SearchCustomer>(this, R.layout.customer_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, SearchCustomer customer) {
                helper.setText(R.id.custom_name, StringUtils.formatCustomerName(customer.getName()))
                        .setText(R.id.custom_phone, customer.getMobile())
                        .setText(R.id.shop_name, customer.getStoreName())
                        .setText(R.id.beautician_name, "美容师:" + StringUtils.formatNull(customer.getStaffName()))
                        .setText(R.id.counselor_name, "顾问:" + StringUtils.formatNull(customer.getAdviser()));
                if("1".equals(customer.getVip())){
                    helper.getView().findViewById(R.id.logo).setVisibility(View.VISIBLE);
                }
                //.setImageUrl(R.id.logo, shop.getCounselor_name()); // 自动异步加载图片
            }
        };
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
                UIHelper.showCustomerSearch(MyCustomerListActivity.this, REQUESTCODE);
            }
        });


        listView.addFooterView();
        listView.setAdapter(adapter);
        // 下拉刷新
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                initSearchParam();
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
                SearchCustomer shop = adapter.getItem(i - 1);
                if (shop != null) {
                    Intent intent = getIntent();
                    intent.putExtra("customer_id",shop.getId());
                    intent.putExtra("customername",shop.getName());
                    intent.putExtra("staffname",shop.getStaffName());
                    intent.putExtra("adviser",shop.getAdviser());
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });
    }

    /**
     * 初始化搜索条件
     */
    private void initSearchParam() {
        param = new SearchParam();
        pno = 1;
        isLoadAll = false;
        param.setType("0");
        param.setStaff_id(bundle.getString("user_id"));
        param.setName(search_name);
        param.setMobile(search_mobile);
    }


    /**
     * 加载数据
     */
    private void loadData() {
        if (isLoadAll) {
            return;
        }
        listView.setFooterViewTextNormal();
        param.setPno(pno);
        if(!StringUtils.isEmpty(param.getStaff_id())){
            HttpClient.getCustomers(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    listView.onRefreshComplete();
                    Log.i("getCustomersTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        JSONObject data = JSON.parseObject(object.getString("data"));
                        List<SearchCustomer> list = JSONArray.parseArray(data.getString("list"), SearchCustomer.class);
                        if (pageSize == 0) {
                            pageSize = data.getInteger("pageSize");
                        }
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
                        if (pno > 1 && (list.isEmpty() || list.size() < pageSize)) {
                            if(list.size() >0){
                                adapter.addAll(list);
                            }
                            listView.setFooterViewTextNoMoreData();
                            isLoadAll = true;
                            return;
                        }
                        adapter.addAll(list);
                        if (pno == 1 && (list.isEmpty() || list.size() < pageSize)) {
                            listView.onRefreshComplete();
                            listView.setFooterViewTextNoMoreData();
                            isLoadAll = true;
                            return;
                        }
                        pno++;
                    } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(MyCustomerListActivity.this);
                    }else {
                        listView.onRefreshComplete();
                        listView.setFooterViewTextError();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getCustomersTAG", "失败返回数据:" + request.toString());
                    listView.onRefreshComplete();
                    listView.setFooterViewTextError();
                }
            }, this);
        }else{
            Toast.makeText(this, "缺少请求参数[staff_id]", Toast.LENGTH_LONG).show();
        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case REQUESTCODE:
                //
                if(intent!=null){
                    Bundle b=intent.getExtras(); //data为B中回传的Intent
                    search_name=b.getString("name");//str即为回传的值
                    search_mobile = b.getString("mobile");
                    if(!StringUtils.isEmpty(search_name)|| !StringUtils.isEmpty(search_mobile)){
                        initSearchParam();
                        loadData();
                    }
                }

                break;
            default:
                break;
        }
    }

}
