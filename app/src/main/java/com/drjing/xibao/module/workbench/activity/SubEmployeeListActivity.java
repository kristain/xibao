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
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.entity.RoleEnum;
import com.drjing.xibao.module.entity.UserParam;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.performance.adapter.QuickAdapter;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.List;

/**
 * 行动计划下级所有员工列表
 * Created by kristain on 16/1/3.
 */
public class SubEmployeeListActivity extends SwipeBackActivity {

    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;

    private ListView list_view;

    QuickAdapter<UserParam> adapter;

    /**
     * 上一个页面传递参数对象,含用户信息
     */
    private Bundle bundle;

    /**
     * 分配到的美容师id
     */
    private String selectedStaffId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subemployee_list);
        bundle = getIntent().getExtras();
        initView();
        initEvent();
        loadData();
    }


    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("行动计划");
        list_view = (ListView) findViewById(R.id.list_view);

        adapter = new QuickAdapter<UserParam>(this, R.layout.adviser_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, final UserParam item) {
                helper.setText(R.id.name, item.getUsername())
                        .setText(R.id.rolename, item.getRoleName())
                        .setText(R.id.store_name, item.getStore_name())
                        .setText(R.id.adviser_id, item.getId() + "");
                helper.getView().findViewById(R.id.store_name).setVisibility(View.VISIBLE);
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


        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView adviser_id = (TextView) view.findViewById(R.id.adviser_id);
                if (!StringUtils.isEmpty(adviser_id.getText().toString())) {
                    Intent intent = getIntent();
                    intent.putExtra("employee_id", adviser_id.getText().toString());
                    intent.putExtra("employee_name", ((TextView) view.findViewById(R.id.name)).getText().toString());
                    setResult(LeaderActionPlanActivity.SELECTEMPLOYEE, intent);
                    finish();
                }
            }
        });

    }

    /**
     * 查询下级所有用户列表
     */
    private void loadData() {
        UserParam param = new UserParam();
        param.setUid(bundle.getString("user_id"));
        if(!StringUtils.isEmpty(param.getUid())){
            HttpClient.getSubAllPersonList(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getSubAllPersonListTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        List<UserParam> list = JSONArray.parseArray(object.getString("data"), UserParam.class);
                        if(list!=null && list.size()>0){
                            for (int i=0;i<list.size();i++){
                                if(RoleEnum.AREAMANAGER.getCode().equals(list.get(i).getRoleKey())|| RoleEnum.STOREMANAGER.getCode().equals(list.get(i).getRoleKey())){
                                    list.remove(i);
                                }
                            }
                        }
                        adapter.addAll(list);
                        list_view.setAdapter(adapter);
                    }  else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(SubEmployeeListActivity.this);
                    }else {
                        Log.i("getSubAllPersonListTAG", "失败返回数据:" + body.toString());
                        Toast.makeText(SubEmployeeListActivity.this, "获取员工列表失败", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getSubAllPersonListTAG", "失败返回数据:" + request.toString());
                    Toast.makeText(SubEmployeeListActivity.this, "获取员工列表失败", Toast.LENGTH_LONG).show();
                }
            }, this);
        }else{
            Toast.makeText(SubEmployeeListActivity.this, "缺少请求参数[uid]", Toast.LENGTH_LONG).show();
        }

    }



}
