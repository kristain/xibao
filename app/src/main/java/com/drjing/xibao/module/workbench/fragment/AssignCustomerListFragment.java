package com.drjing.xibao.module.workbench.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.library.third.ormlite.dao.Dao;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.view.pulltorefresh.PullToRefreshBase;
import com.drjing.xibao.common.view.pulltorefresh.PullToRefreshListView;
import com.drjing.xibao.config.AppConfig;
import com.drjing.xibao.module.entity.SearchCustomer;
import com.drjing.xibao.module.entity.SearchParam;
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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 未分配的客户列表页面
 * Created by kristain on 15/12/20.
 */
public class AssignCustomerListFragment extends Fragment {

    private SearchParam param;
    private int pno = 1;
    private boolean isLoadAll;

    private DatabaseHelper dbHelper;

    private Dao<User, String> userDao;
    private User user;
    private Dao<com.drjing.xibao.provider.db.entity.SearchParam, String> searchDao;
    private com.drjing.xibao.provider.db.entity.SearchParam searcParam;

    @Bind(R.id.listView)
    PullToRefreshListView listView;
    /**
     *
     */
    QuickAdapter<SearchCustomer> adapter;

    @Bind(R.id.list_sum)
    TextView list_sum;


    private int pageSize = 0;

    private Bundle bundle = new Bundle();

    String name="";
    String mobile="";

    public static AssignCustomerListFragment newInstance(String content) {
        AssignCustomerListFragment fragment = new AssignCustomerListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mycustomerlist, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dbHelper = DatabaseHelper.gainInstance(getActivity(), AppConfig.DB_NAME, AppConfig.DB_VERSION);
        try {
            userDao = (Dao<User, String>) dbHelper.getDao(User.class);
            List<User> users = userDao.queryBuilder().query();
            if (users == null || users.size() == 0 || StringUtils.isEmpty(users.get(0).getId())) {
                UIHelper.showLogin(getActivity());
                return;
            }
            user = users.get(0);
            searchDao = (Dao<com.drjing.xibao.provider.db.entity.SearchParam, String>) dbHelper.getDao(com.drjing.xibao.provider.db.entity.SearchParam.class);
            List<com.drjing.xibao.provider.db.entity.SearchParam> param = searchDao.queryBuilder().query();
            if (param == null || param.size() == 0 || StringUtils.isEmpty(param.get(0).getType())) {
            }else{
                if("2".equals(param.get(0).getType())){
                    name = StringUtils.formatNull(param.get(0).getName());
                    mobile = StringUtils.formatNull(param.get(0).getMobile());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            UIHelper.showLogin(getActivity());
        }
        initSearchParam();
        adapter = new QuickAdapter<SearchCustomer>(getActivity(), R.layout.customer_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, SearchCustomer customer) {
                helper.setText(R.id.custom_name, StringUtils.formatCustomerName(customer.getName()))
                        .setText(R.id.custom_phone, customer.getMobile())
                        .setText(R.id.shop_name, customer.getStoreName())
                        .setText(R.id.beautician_name, "美容师:" + StringUtils.formatNull(customer.getStaffName()))
                        .setText(R.id.counselor_name, "顾问:" + StringUtils.formatNull(customer.getAdviser()));
                if ("1".equals(customer.getVip())) {
                    helper.getView().findViewById(R.id.logo).setVisibility(View.VISIBLE);
                }
                //.setImageUrl(R.id.logo, shop.getCounselor_name()); // 自动异步加载图片
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
                    UIHelper.showCustomerDetail(getActivity(), bundle);
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
        param.setName(name);
        param.setMobile(mobile);
        param.setRoleKey(user.getRoleKey());
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

        HttpClient.getCustomers(param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                listView.onRefreshComplete();
                Log.i("getCustomersTAG", "成功返回数据:" + body);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    JSONObject data = JSON.parseObject(object.getString("data"));
                    List<SearchCustomer> list = JSONArray.parseArray(data.getString("list"), SearchCustomer.class);
                    list_sum.setText("共" + data.getString("totalRow") + "条");
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
                        if (list.size() > 0) {
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
                } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                    UIHelper.showLogin(getActivity());
                } else {
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
        }, getActivity());


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != dbHelper) dbHelper.releaseAll();
    }


}
