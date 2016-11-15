package com.drjing.xibao.module.workbench.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.library.third.ormlite.dao.Dao;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.view.materialspinner.NiceSpinner;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.config.AppConfig;
import com.drjing.xibao.module.entity.ActionPlanEntity;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.performance.adapter.QuickAdapter;
import com.drjing.xibao.module.ui.UIHelper;
import com.drjing.xibao.provider.db.DatabaseHelper;
import com.drjing.xibao.provider.db.entity.User;
import com.kristain.common.utils.DateTimeUtils;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 行动计划表
 * Created by kristain on 15/12/30.
 */
public class ActionPlanActivity extends SwipeBackActivity {

    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;

    private NiceSpinner select_month,select_customer;

    private LinearLayout content_layout;

    private TextView medical_beauty,health_beauty,project_target,consume_target;

    private ListView listView;

    private ImageView add_btn;

    ArrayList<String> dataset = new ArrayList<>(Arrays.asList("1月", "2月", "3月", "4月", "5月","6月", "7月", "8月", "9月", "10月","11月","12月"));

    private DatabaseHelper dbHelper;
    private Dao<User, String> userDao;
    private User user;
    /**
     * 选择客户ID
     */
    String customer_id="";

    int month=1;

    QuickAdapter<ActionPlanEntity> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_plan);
        dbHelper = DatabaseHelper.gainInstance(this, AppConfig.DB_NAME, AppConfig.DB_VERSION);
        try {
            userDao = (Dao<User, String>)dbHelper.getDao(User.class);
            List<User> users = userDao.queryBuilder().query();
            if(users==null || users.size()==0 || StringUtils.isEmpty(users.get(0).getId())){
                UIHelper.showLogin(this);
                return;
            }
            user = users.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            UIHelper.showLogin(this);
        }
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
        add_btn = (ImageView)findViewById(R.id.add_btn);

        select_month = (NiceSpinner)findViewById(R.id.select_month);
        select_customer = (NiceSpinner)findViewById(R.id.select_customer);

        listView = (ListView)findViewById(R.id.listView);
        content_layout = (LinearLayout)findViewById(R.id.content_layout);
        medical_beauty = (TextView)findViewById(R.id.medical_beauty);
        health_beauty= (TextView)findViewById(R.id.health_beauty);
        project_target= (TextView)findViewById(R.id.project_target);
        consume_target= (TextView)findViewById(R.id.consume_target);

        select_month.attachDataSource(dataset);
        content_layout.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);

        adapter = new QuickAdapter<ActionPlanEntity>(this, R.layout.actionplan_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, final ActionPlanEntity item) {
                //List<ActionPlanEntity> catelist = JSONArray.parseArray(item.getList(), ActionPlanEntity.class);
                helper.setText(R.id.customername_text, item.getCustomerName())
                        .setText(R.id.staffname, "美容师:" + item.getUserName())
                        .setText(R.id.date_text, StringUtils.isEmpty(item.getArriveTime()) ? "未知" : DateTimeUtils.formatDateTime(Long.parseLong(item.getArriveTime()), DateTimeUtils.DF_YYYY_MM_DD));
                /**
                 * 删除行动计划
                 */
                helper.getView().findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteAction(item.getCustomerId(), StringUtils.isEmpty(item.getArriveTime()) ? "" : DateTimeUtils.formatDateTime(Long.parseLong(item.getArriveTime()), DateTimeUtils.DF_YYYY_MM_DD));
                    }
                });

                if(!StringUtils.isEmpty(item.getMedical_product_text())){
                    helper.getView().findViewById(R.id.medical_beauty_layout).setVisibility(View.VISIBLE);
                    ((TextView)helper.getView().findViewById(R.id.medical_beauty_money)).setText(item.getMedical_product_amount() + "元");
                    ((TextView)helper.getView().findViewById(R.id.medical_beauty_product_content)).setText(item.getMedical_product_text());
                }else{
                    helper.getView().findViewById(R.id.medical_beauty_layout).setVisibility(View.GONE);
                }
                if(!StringUtils.isEmpty(item.getHealth_product_text())){
                    helper.getView().findViewById(R.id.health_beauty_layout).setVisibility(View.VISIBLE);
                    ((TextView)helper.getView().findViewById(R.id.health_beauty_money)).setText(item.getHealth_product_amount() + "元");
                    ((TextView)helper.getView().findViewById(R.id.health_beauty_product_content)).setText(item.getHealth_product_text());
                }else{
                    helper.getView().findViewById(R.id.health_beauty_layout).setVisibility(View.GONE);
                }
                if(!StringUtils.isEmpty(item.getProject_product_text())){
                    helper.getView().findViewById(R.id.project_target_layout).setVisibility(View.VISIBLE);
                    ((TextView)helper.getView().findViewById(R.id.project_target_money)).setText(item.getProject_product_amount() + "元");
                    ((TextView)helper.getView().findViewById(R.id.project_target_product_content)).setText(item.getProject_product_text());
                }else{
                    helper.getView().findViewById(R.id.project_target_layout).setVisibility(View.GONE);
                }
                if(!StringUtils.isEmpty(item.getConsume_product_text())){
                    helper.getView().findViewById(R.id.consume_target_layout).setVisibility(View.VISIBLE);
                    ((TextView)helper.getView().findViewById(R.id.consume_target_money)).setText(item.getConsume_product_amount()+ "元");
                    ((TextView)helper.getView().findViewById(R.id.consume_target_product_content)).setText(item.getConsume_product_text());
                }else{
                    helper.getView().findViewById(R.id.consume_target_layout).setVisibility(View.GONE);
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
         * 点击添加行动计划
         */
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle item = new Bundle();
                item.putString("user_id", user.getId() + "");
                UIHelper.showAddActionPlan(ActionPlanActivity.this, item);
            }
        });

        /**
         * 点击选择客户
         */
        select_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle item = new Bundle();
                item.putString("user_id", user.getId() + "");
                UIHelper.showMyCustomerList(ActionPlanActivity.this, 0, item);
            }
        });
        /**
         * 点击选择月份
         */
        select_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                month = position+1;
                getActionList(month);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 删除行动计划
     * @param arriveTime
     */
    private void deleteAction(String customer_id,String arriveTime){
        ActionPlanEntity param= new ActionPlanEntity();
        param.setCustomerId(customer_id);
        param.setArriveTime(arriveTime);
        HttpClient.deleteActionPlan(param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                Log.i("getActionPlanListTAG", "成功返回数据:" + body);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    getActionList(month);
                } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                    UIHelper.showLogin(ActionPlanActivity.this);
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
     * 获取行动计划列表
     */
    private void getActionList(int month){
        ActionPlanEntity param= new ActionPlanEntity();
        param.setCustomerId(customer_id);
        if(!StringUtils.isEmpty(select_month.getText().toString())){
            Log.e("TAG","month:"+select_month.getText().toString().replace("月",""));
            param.setMonth(DateTimeUtils.gainCurrentMonth(month));
        }
        param.setStoreId("");
        param.setUserId(user.getId());
        HttpClient.getActionPlanList(param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                Log.i("getActionPlanListTAG", "成功返回数据:" + body);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    JSONObject data = JSON.parseObject(object.getString("data"));
                    //单项list
                    List<ActionPlanEntity> list = JSONArray.parseArray(data.getString("list"), ActionPlanEntity.class);
                    List<ActionPlanEntity> listtmp = new ArrayList<ActionPlanEntity>();
                    if(list!=null && list.size()>0){
                        for (int i=0;i<list.size();i++){
                            ActionPlanEntity item = list.get(i);
                            //单项行动计划list
                            List<ActionPlanEntity> actionlist = JSONArray.parseArray(list.get(i).getList(), ActionPlanEntity.class);
                            for (int k=0;k<actionlist.size();k++){
                                Log.e("TAG","CategoryName:"+actionlist.get(k).getCategoryName()+"Amount:"+actionlist.get(k).getAmount());
                                List<ActionPlanEntity> productlist = JSONArray.parseArray(actionlist.get(k).getList(), ActionPlanEntity.class);
                                String product_text="";
                                if(productlist!=null && productlist.size()>0){
                                    for (int j=0;j<productlist.size();j++){
                                        product_text+=productlist.get(j).getName();
                                    }
                                }
                                if("医美".equals(actionlist.get(k).getCategoryName())){
                                    item.setMedical_product_amount(actionlist.get(k).getAmount());
                                    item.setMedical_product_text(product_text);
                                }else if("生美".equals(actionlist.get(k).getCategoryName())){
                                    item.setHealth_product_amount(actionlist.get(k).getAmount());
                                    item.setHealth_product_text(product_text);
                                }else if("产品".equals(actionlist.get(k).getCategoryName())){
                                    item.setProject_product_amount(actionlist.get(k).getAmount());
                                    item.setProject_product_text(product_text);
                                }else if("消耗".equals(actionlist.get(k).getCategoryName())){
                                    item.setConsume_product_amount(actionlist.get(k).getAmount());
                                    item.setConsume_product_text(product_text);
                                }
                            }
                            listtmp.add(item);
                        }
                    }

                    JSONObject targets = JSON.parseObject(data.getString("targets"));
                    content_layout.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.VISIBLE);
                    medical_beauty.setText("医美目标:" + targets.getLongValue("action_medical_beauty") + "元");
                    health_beauty.setText("生美目标:" + targets.getLongValue("action_health_beauty") + "元");
                    project_target.setText("产品目标:" + targets.getLongValue("action_project")+ "元");
                    consume_target.setText("消耗目标:" + targets.getLongValue("action_consume")+ "元");
                    adapter.clear();
                    adapter.addAll(listtmp);
                    listView.setAdapter(adapter);
                }  else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                    UIHelper.showLogin(ActionPlanActivity.this);
                }else {
                    Log.i("getActionPlanListTAG", "失败返回数据:" + body.toString());
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Log.i("getActionPlanListTAG", "失败返回数据:" + request.toString());
            }
        }, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != dbHelper) dbHelper.releaseAll();
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
            default:
                break;
        }
    }
}
