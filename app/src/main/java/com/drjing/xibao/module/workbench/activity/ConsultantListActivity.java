package com.drjing.xibao.module.workbench.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.view.materialwidget.PaperButton;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.entity.CustomerEntity;
import com.drjing.xibao.module.entity.UserParam;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.performance.adapter.QuickAdapter;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.List;

/**
 * 单选分配给顾问 单选顾问列表
 * Created by kristain on 16/1/3.
 */
public class ConsultantListActivity extends SwipeBackActivity {

    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;


    private ListView list_view;

    private PaperButton submit_btn;

    QuickAdapter<UserParam> adapter;

    /**
     * 上一个页面传递参数对象,含用户信息
     */
    private Bundle bundle;

    /**
     * 选择分配用户id
     */
    private String selectedIds;

    /**
     * 分配到的顾问id
     */
    private String selectedStaffId="";

    List<UserParam> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultant_list);
        bundle = getIntent().getExtras();
        selectedIds = bundle.getString("selectCustomers");
        initView();
        initEvent();
        loadData();
    }


    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("顾问列表");
        list_view = (ListView)findViewById(R.id.list_view);
        submit_btn = (PaperButton)findViewById(R.id.submit_btn);

        //adapter = new QuickAdapter<UserParam>(this, R.layout.adviser_list_item) {
        adapter = new QuickAdapter<UserParam>(this, R.layout.staff_single_choice ) {
            @Override
            protected void convert(BaseAdapterHelper helper, final UserParam item) {
                ((CheckedTextView)helper.getView().findViewById(R.id.text1)).setText(item.getUsername());
                /*helper.setText(R.id.name, item.getUsername())
                        .setText(R.id.rolename, item.getRoleName())
                        .setText(R.id.store_name, item.getAccountname())
                        .setText(R.id.adviser_id, item.getId() + "");
                helper.getView().findViewById(R.id.arraw_btn).setVisibility(View.GONE);
                ((CheckBox)helper.getView().findViewById(R.id.select_btn)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            buttonView.setVisibility(View.VISIBLE);
                            selectedStaffId = item.getId() + "";
                        } else {
                            buttonView.setVisibility(View.GONE);
                            selectedStaffId = "";
                        }
                    }
                });*/
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

        /**
         * 点击提交
         */
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ListView.INVALID_POSITION != list_view.getCheckedItemPosition()){
                    selectedStaffId = list.get(list_view.getCheckedItemPosition()).getId()+"";
                }
                if(StringUtils.isEmpty(selectedStaffId)){
                    Toast.makeText(ConsultantListActivity.this,"请选择一个顾问",Toast.LENGTH_LONG).show();
                    return;
                }
                assignCustomer();
            }
        });

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               /* TextView adviser_id = (TextView) view.findViewById(R.id.adviser_id);
                if (!StringUtils.isEmpty(adviser_id.getText().toString())) {
                    CheckBox item = ((CheckBox) view.findViewById(R.id.select_btn));
                    if (View.VISIBLE == item.getVisibility()) {
                        item.setChecked(false);
                    } else {
                        item.setChecked(true);
                    }
                }*/
            }
        });

    }

    /**
     * 查询顾问列表
     */
    private void loadData() {
        /*UserParam param= new UserParam();
        param.setCompanyid(bundle.getString("company_id"));
        if(!StringUtils.isEmpty(param.getCompanyid())){
            HttpClient.getAdviserList(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getAdviserListTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        list = JSONArray.parseArray(object.getString("data"), UserParam.class);
                        adapter.addAll(list);
                        list_view.setAdapter(adapter);
                    } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(ConsultantListActivity.this);
                    }else {
                        Log.i("getAdviserListTAG", "失败返回数据:" + body.toString());
                        Toast.makeText(ConsultantListActivity.this,"获取顾问列表失败",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getAdviserListTAG", "失败返回数据:" + request.toString());
                    Toast.makeText(ConsultantListActivity.this,"获取顾问列表失败",Toast.LENGTH_LONG).show();
                }
            }, this);
        }else{
            Toast.makeText(this,"缺少请求参数[companyid]",Toast.LENGTH_LONG).show();
        }*/


        UserParam param= new UserParam();
        param.setUid(bundle.getString("user_id"));
        if(!StringUtils.isEmpty(param.getUid())){
            HttpClient.getSubPersonList(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getSubPersonListTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        list = JSONArray.parseArray(object.getString("data"), UserParam.class);
                        adapter.addAll(list);
                        list_view.setAdapter(adapter);
                    } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(ConsultantListActivity.this);
                    } else {
                        Log.i("getSubPersonListTAG", "失败返回数据:" + body.toString());
                        Toast.makeText(ConsultantListActivity.this, "获取顾问列表失败", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getSubPersonListTAG", "失败返回数据:" + request.toString());
                    Toast.makeText(ConsultantListActivity.this, "获取顾问列表失败", Toast.LENGTH_LONG).show();
                }
            }, this,true);
        }else{
            Toast.makeText(ConsultantListActivity.this, "缺少请求参数[uid]", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 分配用户
     */
    private void assignCustomer(){
        CustomerEntity param= new CustomerEntity();
        param.setRoleKey(bundle.getString("rolekey"));
        param.setCustomerIds(selectedIds);
        param.setStaff_id(selectedStaffId);
        if(!StringUtils.isEmpty(param.getStaff_id())){
            HttpClient.assignCustomer(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("assignCustomerTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Log.i("assignCustomerTAG", "失败返回数据:" + body.toString());
                        Toast.makeText(ConsultantListActivity.this,"分配失败",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("assignCustomerTAG", "失败返回数据:" + request.toString());
                    Toast.makeText(ConsultantListActivity.this,"分配失败",Toast.LENGTH_LONG).show();
                }
            }, this);
        }else{
            Toast.makeText(ConsultantListActivity.this,"缺少请求参数[staff_id]",Toast.LENGTH_LONG).show();
        }

    }

}
