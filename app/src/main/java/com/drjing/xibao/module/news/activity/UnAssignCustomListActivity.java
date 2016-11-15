package com.drjing.xibao.module.news.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.drjing.xibao.common.view.pulltorefresh.PullToRefreshBase;
import com.drjing.xibao.common.view.pulltorefresh.PullToRefreshListView;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.entity.RoleEnum;
import com.drjing.xibao.module.entity.SearchCustomer;
import com.drjing.xibao.module.entity.SearchParam;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.performance.adapter.QuickAdapter;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 未分配客户列表
 * Created by kristain on 15/12/21.
 */
public class UnAssignCustomListActivity extends SwipeBackActivity {

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
    /**
     * 上一个页面传递参数对象,含用户信息
     */
    private Bundle item;

    private final static int  REQUESTCODE=0;

    private int pageSize=0;

    private TextView btnRight;

    private List<SearchCustomer> selectedCustomerList = new ArrayList<SearchCustomer>();

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
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.setMargins(0, 0, 20, 0);

        textHeadTitle.setText("客户列表");
        btnRight.setBackgroundDrawable(getResources().getDrawable(R.drawable.rightbtn_drawable));
        btnRight.setTextSize(12);
        btnRight.setText("分配");
        btnRight.setLayoutParams(layoutParams);
        /**
         * 点击分配
         */
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectCustomers ="";
                if(selectedCustomerList==null || selectedCustomerList.size()==0){
                    Toast.makeText(UnAssignCustomListActivity.this,"请选择客户",Toast.LENGTH_LONG).show();
                    return;
                }
                for (int i=0;i<selectedCustomerList.size();i++){
                    selectCustomers+=selectedCustomerList.get(i).getId()+",";
                }
                if(StringUtils.isEmpty(selectCustomers)){
                    Toast.makeText(UnAssignCustomListActivity.this,"请选择客户",Toast.LENGTH_LONG).show();
                    return;
                }
                item.putString("selectCustomers",selectCustomers.substring(0,selectCustomers.length()-1));
                if(RoleEnum.STOREMANAGER.getCode().equals(item.getString("rolekey"))){
                   //分配给顾问
                   UIHelper.showConsultantList(UnAssignCustomListActivity.this, 0,item);
                }
                if(RoleEnum.CONSULTANT.getCode().equals(item.getString("rolekey"))){
                    //分配给美容师
                    UIHelper.showSubStaffList(UnAssignCustomListActivity.this, 0, item);
                }
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
        adapter = new QuickAdapter<SearchCustomer>(this, R.layout.assigncustomer_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, final SearchCustomer shop) {
                helper.setText(R.id.custom_name, StringUtils.formatCustomerName(shop.getName()))
                        .setText(R.id.custom_phone, shop.getMobile())
                        .setText(R.id.beautician_name, "美容师:" + StringUtils.formatNull(shop.getStaffName()))
                        .setText(R.id.counselor_name, "顾问:" + StringUtils.formatNull(shop.getAdviser()));
                if("1".equals(shop.getVip())){
                    helper.getView().findViewById(R.id.logo).setVisibility(View.VISIBLE);
                }
                ((CheckBox)helper.getView().findViewById(R.id.selected_btn)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            buttonView.setVisibility(View.VISIBLE);
                            if(selectedCustomerList!=null){
                                if(!selectedCustomerList.contains(shop)){
                                    selectedCustomerList.add(shop);
                                }
                            }
                        } else {
                            buttonView.setVisibility(View.GONE);
                            if(selectedCustomerList!=null && selectedCustomerList.size()>0){
                                selectedCustomerList.remove(item);
                            }
                        }
                    }
                });
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
                    bundle.putString("customer_id", shop.getId());
                    ((CheckBox) view.findViewById(R.id.selected_btn)).setChecked(true);
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
        param.setType("4");
        param.setStaff_id(item.getString("user_id"));
        param.setRoleKey(item.getString("rolekey"));
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
                            if(list.size()>0){
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
                            return;
                        }
                        pno++;
                    }else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(UnAssignCustomListActivity.this);
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
            Toast.makeText(this, "缺少请求参数[staff_id]", Toast.LENGTH_LONG).show();
        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case RESULT_OK:
                //分配给顾问结束
                initSearchParam();
                loadData();
                break;
            default:
                initSearchParam();
                loadData();
                break;
        }
    }


}
