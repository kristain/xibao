package com.drjing.xibao.module.news.activity;

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
 * 老总和区总 客户列表
 * Created by kristain on 15/12/21.
 */
public class AllCustomListActivity extends SwipeBackActivity {

    private SearchParam param;
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
    /**
     *
     */
    QuickAdapter<SearchCustomer> adapter;

    private Bundle bundle = new Bundle();
    private Bundle item;

    private final static int  REQUESTCODE=0;

    private int pageSize=0;

    private TextView btnRight;


    private String search_name="";
    private String search_mobile="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unactivatedcustomer_list);
        item=getIntent().getExtras();

        listView = (PullToRefreshListView)findViewById(R.id.listView);
        btnBack = (Button)findViewById(R.id.btnBack);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        btnRight = (TextView)findViewById(R.id.btnRight);
        initView();
    }

    void initView() {
        btnBack.setVisibility(View.VISIBLE);
        btnRight.setVisibility(View.VISIBLE);
        textHeadTitle.setText("客户列表");
        btnRight.setText("搜索");

        /**
         * 点击搜索
         */
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showCustomerSearch(AllCustomListActivity.this, REQUESTCODE);
            }
        });

        /**
         * 后退返回键点击事件
         */
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initSearchParam();
        adapter = new QuickAdapter<SearchCustomer>(this, R.layout.customer_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, SearchCustomer shop) {
                helper.setText(R.id.custom_name, StringUtils.formatCustomerName(shop.getName()))
                        .setText(R.id.custom_phone, shop.getMobile())
                        .setText(R.id.shop_name, shop.getStoreName())
                        .setText(R.id.beautician_name, "美容师:" + StringUtils.formatNull(shop.getStaffName()))
                        .setText(R.id.counselor_name, "顾问:" + StringUtils.formatNull(shop.getAdviser()));
                if("1".equals(shop.getVip())){
                    helper.getView().findViewById(R.id.logo).setVisibility(View.VISIBLE);
                }
            }
        };

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
                    bundle.putString("mobile",shop.getMobile());
                    bundle.putString("customer_id", shop.getId());
                    UIHelper.showCustomerDetail(AllCustomListActivity.this, bundle);
                }
            }
        });
        loadData();
    }

    /**
     * 初始化搜索条件
     */
    private void initSearchParam() {
        param = new SearchParam();
        pno = 1;
        isLoadAll = false;
        param.setType("2");
        param.setStoreId(item.getString("storeid"));
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
        param.setPno(pno);
        listView.setFooterViewTextNormal();
        if(!StringUtils.isEmpty(param.getStoreId())){
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
                            if(list.size()>0){
                                adapter.addAll(list);
                            }
                            listView.setFooterViewTextNoMoreData();
                            isLoadAll = true;
                            return;
                        }
                        adapter.addAll(list);
                        if (pno == 1 && (list.isEmpty() || list.size() < pageSize)) {
                            listView.setFooterViewTextNoMoreData();
                            isLoadAll = true;
                            return;
                        }
                        pno++;
                    }else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(AllCustomListActivity.this);
                    }else{
                        Log.i("getCustomersTAG", "获取数据失败:" + body);
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    listView.onRefreshComplete();
                    listView.setFooterViewTextError();
                }
            },this);
        }else{
            Toast.makeText(this,"缺少请求参数[store_id]",Toast.LENGTH_LONG).show();
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case REQUESTCODE:
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
