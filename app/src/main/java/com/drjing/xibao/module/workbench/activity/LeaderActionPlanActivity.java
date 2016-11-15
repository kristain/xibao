package com.drjing.xibao.module.workbench.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.view.materialspinner.NiceSpinner;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.entity.ActionPlanEntity;
import com.drjing.xibao.module.entity.RoleEnum;
import com.drjing.xibao.module.entity.StoreEntity;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.performance.adapter.QuickAdapter;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.DateTimeUtils;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 上级领导行动计划表
 * Created by kristain on 15/12/30.
 */
public class LeaderActionPlanActivity extends SwipeBackActivity {

    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;

    private NiceSpinner select_month,select_customer,select_store,select_employee;

    private LinearLayout content_layout;

    private TextView medical_beauty,health_beauty,project_target,consume_target;

    private ListView listView;

    /**
     * 门店列表
     */
    List<StoreEntity> storelist;
    ArrayList<String> storeDataset = new ArrayList<String>();

    ArrayList<String> dataset = new ArrayList<>(Arrays.asList("1月", "2月", "3月", "4月", "5月","6月", "7月", "8月", "9月", "10月","11月","12月"));
    /**
     * 选择客户ID
     */
    String customer_id="";

    /**
     * 选择员工ID
     */
    String employee_id="";


    int month=1;

    QuickAdapter<ActionPlanEntity> adapter;

    Bundle bundle;

    public static final  int SELECTEMPLOYEE=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_action_plan);
        bundle = getIntent().getExtras();
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("行动计划表");

        select_month = (NiceSpinner)findViewById(R.id.select_month);
        select_customer = (NiceSpinner)findViewById(R.id.select_customer);
        select_store = (NiceSpinner)findViewById(R.id.select_store);
        select_employee = (NiceSpinner)findViewById(R.id.select_employee);
        listView = (ListView)findViewById(R.id.listView);
        content_layout = (LinearLayout)findViewById(R.id.content_layout);
        medical_beauty = (TextView)findViewById(R.id.medical_beauty);
        health_beauty= (TextView)findViewById(R.id.health_beauty);
        project_target= (TextView)findViewById(R.id.project_target);
        consume_target= (TextView)findViewById(R.id.consume_target);
        //顾问、店长角色不需要显示门店选择
        if(RoleEnum.CONSULTANT.getCode().equals(bundle.getString("rolekey"))|| RoleEnum.STOREMANAGER.getCode().equals(bundle.getString("rolekey"))){
            select_store.setVisibility(View.INVISIBLE);
        }else{
            //区域经理、老板 加载数据
            getStoreList();
        }

        select_month.attachDataSource(dataset);
        content_layout.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);

        adapter = new QuickAdapter<ActionPlanEntity>(this, R.layout.actionplan_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, ActionPlanEntity item) {
                List<ActionPlanEntity> catelist = JSONArray.parseArray(item.getList(), ActionPlanEntity.class);
                helper.setText(R.id.customername_text, item.getCustomerName())
                        .setText(R.id.staffname, "美容师:"+item.getUserName())
                        .setText(R.id.date_text, StringUtils.isEmpty(item.getArriveTime())?"未知":DateTimeUtils.formatDateTime(Long.parseLong(item.getArriveTime()),DateTimeUtils.DF_YYYY_MM_DD));
                if(catelist != null && catelist.size()>0){
                    for (int i=0;i<catelist.size();i++){
                        if("医美".equals(catelist.get(i).getCategoryName())){
                            helper.getView().findViewById(R.id.medical_beauty_layout).setVisibility(View.VISIBLE);
                            ((TextView)helper.getView().findViewById(R.id.medical_beauty_money)).setText(catelist.get(i).getAmount() + "元");
                            List<ActionPlanEntity> productlist = JSONArray.parseArray(catelist.get(i).getList(), ActionPlanEntity.class);
                            String product_text="";
                            if(productlist!=null && productlist.size()>0){
                               for (int k=0;k<productlist.size();k++){
                                   product_text+=productlist.get(k).getName();
                               }
                            }
                            ((TextView)helper.getView().findViewById(R.id.medical_beauty_product_content)).setText(product_text);
                        }
                        if("生美".equals(catelist.get(i).getCategoryName())){
                            helper.getView().findViewById(R.id.health_beauty_layout).setVisibility(View.VISIBLE);
                            ((TextView)helper.getView().findViewById(R.id.health_beauty_money)).setText(catelist.get(i).getAmount() + "元");
                            List<ActionPlanEntity> productlist = JSONArray.parseArray(catelist.get(i).getList(), ActionPlanEntity.class);
                            String product_text="";
                            if(productlist!=null && productlist.size()>0){
                                for (int k=0;k<productlist.size();k++){
                                    product_text+=productlist.get(k).getName();
                                }
                            }
                            ((TextView)helper.getView().findViewById(R.id.health_beauty_product_content)).setText(product_text);
                        }
                        if("产品".equals(catelist.get(i).getCategoryName())){
                            helper.getView().findViewById(R.id.project_target_layout).setVisibility(View.VISIBLE);
                            ((TextView)helper.getView().findViewById(R.id.project_target_money)).setText(catelist.get(i).getAmount() + "元");
                            List<ActionPlanEntity> productlist = JSONArray.parseArray(catelist.get(i).getList(), ActionPlanEntity.class);
                            String product_text="";
                            if(productlist!=null && productlist.size()>0){
                                for (int k=0;k<productlist.size();k++){
                                    product_text+=productlist.get(k).getName();
                                }
                            }
                            ((TextView)helper.getView().findViewById(R.id.project_target_product_content)).setText(product_text);
                        }
                        if("消耗".equals(catelist.get(i).getCategoryName())){
                            helper.getView().findViewById(R.id.consume_target_layout).setVisibility(View.VISIBLE);
                            ((TextView)helper.getView().findViewById(R.id.consume_target_money)).setText(catelist.get(i).getAmount() + "元");
                            List<ActionPlanEntity> productlist = JSONArray.parseArray(catelist.get(i).getList(), ActionPlanEntity.class);
                            String product_text="";
                            if(productlist!=null && productlist.size()>0){
                                for (int k=0;k<productlist.size();k++){
                                    product_text+=productlist.get(k).getName()+" ";
                                }
                            }
                            ((TextView)helper.getView().findViewById(R.id.consume_target_product_content)).setText(product_text);
                        }
                    }
                }
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
         * 点击选择员工
         */
        select_employee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RoleEnum.CONSULTANT.getCode().equals(bundle.getString("rolekey"))|| RoleEnum.STOREMANAGER.getCode().equals(bundle.getString("rolekey"))){
                    //选择下级所有员工列表
                    UIHelper.showSubEmployeeList(LeaderActionPlanActivity.this, 0,bundle);
                }else{
                    //选择门店所有员工
                    bundle.putString("user_id", getStoreUserIdByName(storelist,select_store.getText().toString()));
                    UIHelper.showSubEmployeeList(LeaderActionPlanActivity.this, 0,bundle);
                }

            }
        });

        /**
         * 点击选择客户
         */
        select_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isEmpty(select_employee.getText().toString()) || "选择员工".equals(select_employee.getText().toString()) || StringUtils.isEmpty(employee_id)) {
                    Toast.makeText(LeaderActionPlanActivity.this, "请选择员工", Toast.LENGTH_LONG).show();
                    return;
                }
                Bundle item = new Bundle();
                item.putString("user_id", employee_id);
                UIHelper.showMyCustomerList(LeaderActionPlanActivity.this, 0, item);
            }
        });



        /**
         * 点击选择月份
         */
        select_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                month = position + 1;
                getActionList(month);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 获取行动计划列表
     */
    private void getActionList(int month){
        ActionPlanEntity param= new ActionPlanEntity();
        //顾客id
        param.setCustomerId(customer_id);
        if(!StringUtils.isEmpty(select_month.getText().toString())){
            param.setMonth(DateTimeUtils.gainCurrentMonth(month));
        }
        //门店id
        if(RoleEnum.CONSULTANT.getCode().equals(bundle.getString("rolekey"))|| RoleEnum.STOREMANAGER.getCode().equals(bundle.getString("rolekey"))){
            param.setStoreId("");
        }else{
            param.setStoreId(getStoreIdByName(storelist,select_store.getText().toString()));
        }
        //员工id
        param.setUserId(employee_id);
        HttpClient.getActionPlanList(param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                Log.i("getActionPlanListTAG", "成功返回数据:" + body);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    JSONObject data = JSON.parseObject(object.getString("data"));
                    List<ActionPlanEntity> list = JSONArray.parseArray(data.getString("list"), ActionPlanEntity.class);
                    JSONObject targets = JSON.parseObject(data.getString("targets"));
                    content_layout.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.VISIBLE);
                    medical_beauty.setText("医美目标:" + targets.getLongValue("action_medical_beauty") + "元");
                    health_beauty.setText("生美目标:" + targets.getLongValue("action_health_beauty") + "元");
                    project_target.setText("产品目标:" + targets.getLongValue("action_project") + "元");
                    consume_target.setText("消耗目标:" + targets.getLongValue("action_consume") + "元");
                    adapter.clear();
                    adapter.addAll(list);
                    listView.setAdapter(adapter);
                } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                    UIHelper.showLogin(LeaderActionPlanActivity.this);
                } else {
                    Log.i("getActionPlanListTAG", "失败返回数据:" + body.toString());
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Log.i("getActionPlanListTAG", "失败返回数据:" + request.toString());
            }
        }, this);
    }

    /**
     * 获取门店列表
     */
    private void getStoreList(){
        StoreEntity param = new StoreEntity();
        if(!StringUtils.isEmpty(bundle.getString("user_id"))){
            param.setId(Integer.parseInt(bundle.getString("user_id")));
            HttpClient.getCompanyStoreList(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getCompanyStoreListTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        storelist = JSONArray.parseArray(object.getString("data"), StoreEntity.class);
                        for (int i = 0; i < storelist.size(); i++) {
                            storeDataset.add(storelist.get(i).getName());
                        }
                        select_store.attachDataSource(storeDataset);
                    } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(LeaderActionPlanActivity.this);
                    } else {
                        Log.i("getCompanyStoreListTAG", "获取数据失败:" + body);
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getCompanyStoreListTAG", "失败返回数据:" + request.toString());
                }
            }, this);
        }else{
            Toast.makeText(this,"缺少请求参数[uid]",Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 根据门店名称获取门店ID
     * @param list
     * @param storeName
     * @return
     */
    private String getStoreIdByName(List<StoreEntity> list,String storeName){
        if(list==null || list.size()==0){
            return "";
        }

        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).getName().equals(storeName)){
                return list.get(i).getId()+"";
            }
        }
        return "";
    }


    /**
     * 根据门店名称获取门店ID
     * @param list
     * @param storeName
     * @return
     */
    private String getStoreUserIdByName(List<StoreEntity> list,String storeName){
        if(list==null || list.size()==0){
            return "";
        }

        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).getName().equals(storeName)){
                JSONObject object = JSON.parseObject(list.get(i).getShopowner());
                return object.getString("id");
            }
        }
        return "";
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                //选择我的客户
                if(data!=null){
                    Bundle b=data.getExtras(); //data为B中回传的Intent
                    customer_id=b.getString("customer_id");//str即为回传的值
                    select_customer.setText(b.getString("customername"));
                    getActionList(month);
                }
                break;
            case SELECTEMPLOYEE:
                //选择员工
                if(data!=null){
                    Bundle b=data.getExtras(); //data为B中回传的Intent
                    employee_id=b.getString("employee_id");//str即为回传的值
                    select_employee.setText(b.getString("employee_name"));
                    getActionList(month);
                }
                break;
            default:
                break;
        }
    }



}
